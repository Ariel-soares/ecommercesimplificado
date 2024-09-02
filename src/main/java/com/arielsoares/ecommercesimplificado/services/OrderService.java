package com.arielsoares.ecommercesimplificado.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.arielsoares.ecommercesimplificado.entities.Order;
import com.arielsoares.ecommercesimplificado.entities.OrderItem;
import com.arielsoares.ecommercesimplificado.entities.Product;
import com.arielsoares.ecommercesimplificado.entities.User;
import com.arielsoares.ecommercesimplificado.entities.enums.OrderStatus;
import com.arielsoares.ecommercesimplificado.repositories.OrderRepository;

@Service
public class OrderService {

	@Autowired
	private OrderRepository repository;

	@Autowired
	private OrderItemService orderItemService;

	@Autowired
	private ProductService productService;

	@Autowired
	private UserService userService;

	@Cacheable(value = "orders")
	public List<Order> findAll() {
		return repository.findAll();
	}

	@Cacheable(value = "orders", key = "#orderId")
	public Order findById(Long orderId) {
		Optional<Order> obj = repository.findById(orderId);
		return obj.orElseThrow(() -> new RuntimeException("Order not found for Id: " + orderId));
	}

	@CachePut(value = "orders", key = "#result.id")
	@CacheEvict(value = "orders", allEntries = true)
	public Order insert(Order Order) {
		return repository.save(Order);
	}

	@CacheEvict(value = "orders", allEntries = true)
	public void delete(Long id) {
		repository.deleteById(id);
	}

	// Refatorar no futuro -> Revisar antes da entrega
	@CachePut(value = "orders", key = "#result.id")
	@CacheEvict(value = "orders", allEntries = true)
	public Order update(Long userId, Long id, String status) {

		if (!status.toUpperCase().equals("CANCELLED") && !status.toUpperCase().equals("PAID"))
			throw new IllegalArgumentException("Only CANCELLED status or PAID STATUS ACCEPTED ");

		List<Order> clientOrders = findByClientId(userId);
		Order obj = findById(id);
		if (!clientOrders.contains(obj))
			throw new RuntimeException("Client can update only their own orders");

		if (obj.getStatus() == OrderStatus.PAID || obj.getStatus() == OrderStatus.COMPLETE
				|| obj.getStatus() == OrderStatus.CANCELLED)
			throw new IllegalArgumentException("Order is already " + obj.getStatus());

		if (status.toUpperCase().equals("PAID"))
			completeOrder(obj);

		if (status.toUpperCase().equals("CANCELLED"))
			obj.setStatus(OrderStatus.CANCELLED);

		return insert(obj);
	}

	// OK + Conferir se está atualizando o estoque do produto ao fechar a compra
	private void completeOrder(Order obj) {

		Integer quantity = 0;
		for (OrderItem oi : obj.getItems()) {
			if (!oi.getProduct().getActive() || !oi.getActive())
				continue;
			quantity += 1;
		}
		if (quantity < 1)
			throw new RuntimeException("Order can only be completed if it has at least 1 active Item");

		// Confere se todos os items pedem menos que o do estoque + Refatorar para
		// devolver todos os OrderItem que não estão condizentes
		for (OrderItem oi : obj.getItems()) {
			if (!oi.getProduct().getActive() || !oi.getActive())
				continue;
			if (oi.getQuantity() > oi.getProduct().getStorage_quantity() || oi.getProduct().getStorage_quantity() == 0)
				throw new IllegalArgumentException(
						"Insuficient Storage Quantity of this product " + oi.getProduct().getName());
		}

		for (OrderItem oi : obj.getItems()) {
			if (!oi.getProduct().getActive() || !oi.getActive())
				continue;
			Product product = oi.getProduct();
			product.setStorage_quantity(product.getStorage_quantity() - oi.getQuantity());
			productService.update(product.getId(), product);
		}
		obj.setStatus(OrderStatus.PAID);
	}

	// Refatorar -> Order não pode ser atualizada caso já esteja fechada ou paga
	@CachePut(value = "orders", key = "#result.id")
	@CacheEvict(value = "orders", allEntries = true)
	public Order addOrderItem(Long userId, Long orderId, Integer quantity, Long productId) {
		Order order = findById(orderId);

		if (order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.COMPLETE
				|| order.getStatus() == OrderStatus.CANCELLED)
			throw new IllegalArgumentException("Order is already " + order.getStatus());

		User newUser = userService.findById(userId);
		if (!newUser.getOrders().contains(order))
			throw new RuntimeException();
		Product product = productService.findById(productId);

		OrderItem oi = orderItemService.insert(new OrderItem(product, quantity));
		for (OrderItem i : order.getItems()) {
			if (i.getProduct() == oi.getProduct()) {
				i.setQuantity(i.getQuantity() + quantity);
				return insert(order);
			}
		}
		order.getItems().add(oi);
		return insert(order);
	}

	public List<Order> findByClientId(Long clientId) {
		return repository.findByClientId(clientId);
	}

	// OK + Revisar mais tarde, ele funciona mas não sei o porquê
	@CachePut(value = "orders", key = "#result.id")
	@CacheEvict(value = "orders", allEntries = true)
	public Order inactiveOrderItem(Long userId, Long orderId, Long orderItemId) {

		List<Order> clientOrders = findByClientId(userId);
		Order order = findById(orderId);
		if (!clientOrders.contains(order))
			throw new RuntimeException("Client can update only their own orders");

		for (OrderItem oi : order.getItems()) {
			if (oi.getId() == orderItemId)
				orderItemService.update(orderItemId);
		}
		return findById(orderId);
	}

}
