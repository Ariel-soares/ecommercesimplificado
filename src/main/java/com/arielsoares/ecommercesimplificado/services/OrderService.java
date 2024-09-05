package com.arielsoares.ecommercesimplificado.services;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.arielsoares.ecommercesimplificado.entities.Order;
import com.arielsoares.ecommercesimplificado.entities.OrderItem;
import com.arielsoares.ecommercesimplificado.entities.Product;
import com.arielsoares.ecommercesimplificado.entities.User;
import com.arielsoares.ecommercesimplificado.entities.enums.OrderStatus;
import com.arielsoares.ecommercesimplificado.exception.InvalidArgumentException;
import com.arielsoares.ecommercesimplificado.exception.ResourceNotFoundException;
import com.arielsoares.ecommercesimplificado.repositories.OrderRepository;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Service
public class OrderService {

	//FALTA REVISAR
	
	private OrderRepository repository;
	private OrderItemService orderItemService;
	private ProductService productService;
	private UserService userService;

	public OrderService(OrderRepository repository, OrderItemService orderItemService, ProductService productService,
			UserService userService) {
		this.repository = repository;
		this.orderItemService = orderItemService;
		this.productService = productService;
		this.userService = userService;
	}

	@Cacheable(value = "orders")
	public List<Order> findAll() {
		return repository.findAll();
	}

	public Order findById(Long orderId) {
		return repository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));
	}

	@JsonIgnoreProperties(value = {"client"})
	public List<Order> findByClientId(Long clientId) {
		return repository.findByClientId(clientId);
	}

	@CacheEvict(value = "orders", allEntries = true)
	public Order insert(Order Order) {
		return repository.save(Order);
	}

	@CacheEvict(value = "orders", allEntries = true)
	public void delete(Long id) {
		repository.deleteById(id);
	}

	// Refatorar no futuro -> Revisar antes da entrega
	@CacheEvict(value = "orders", allEntries = true)
	public Order update(Long userId, Long id, String status) {

		if (!status.toUpperCase().equals("CANCELLED") && !status.toUpperCase().equals("PAID"))
			throw new InvalidArgumentException("Only CANCELLED status or PAID STATUS ACCEPTED ");

		List<Order> clientOrders = findByClientId(userId);
		Order obj = findById(id);
		if (!clientOrders.contains(obj))
			throw new InvalidArgumentException("Client can update only their own orders");

		if (obj.getStatus() == OrderStatus.PAID || obj.getStatus() == OrderStatus.COMPLETE
				|| obj.getStatus() == OrderStatus.CANCELLED)
			throw new InvalidArgumentException("Order is already " + obj.getStatus());

		if (status.toUpperCase().equals("PAID"))
			completeOrder(obj);

		if (status.toUpperCase().equals("CANCELLED"))
			obj.setStatus(OrderStatus.CANCELLED);

		return insert(obj);
	}

	// OK + Conferir se está atualizando o estoque do produto ao fechar a compra
	@CacheEvict(value = "orders", allEntries = true)
	private void completeOrder(Order obj) {

		Integer quantity = 0;
		for (OrderItem oi : obj.getItems()) {
			if (!oi.getProduct().getActive() || !oi.getActive())
				continue;
			quantity += 1;
		}
		if (quantity < 1)
			throw new InvalidArgumentException("Order can only be completed if it has at least 1 active Item");

		// Confere se todos os items pedem menos que o do estoque + Refatorar para
		// devolver todos os OrderItem que não estão condizentes
		for (OrderItem oi : obj.getItems()) {
			if (!oi.getProduct().getActive() || !oi.getActive())
				continue;
			if (oi.getQuantity() > oi.getProduct().getStorage_quantity() || oi.getProduct().getStorage_quantity() == 0)
				throw new InvalidArgumentException(
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

	// Conferir -> Order não pode ser atualizada caso já esteja fechada ou paga
	@CacheEvict(value = "orders", allEntries = true)
	public Order addOrderItem(Long userId, Long orderId, Integer quantity, Long productId) {
		Order order = findById(orderId);

		if (order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.COMPLETE
				|| order.getStatus() == OrderStatus.CANCELLED)
			throw new InvalidArgumentException("Order is already " + order.getStatus());

		User newUser = userService.findById(userId);
		if (!newUser.getOrders().contains(order))
			throw new IllegalArgumentException("Users can only modify their own orders");
		Product product = productService.findById(productId);

		//Analisar
		OrderItem oi = new OrderItem(product, quantity);
		for (OrderItem i : order.getItems()) {
			if (i.getProduct() == oi.getProduct() && i.getActive()) {
				i.setQuantity(i.getQuantity() + quantity);
				return insert(order);
			}
		}
		
		 OrderItem newOi = orderItemService.insert(oi);
		
		order.getItems().add(newOi);
		return insert(order);
	}

	// OK + Revisar mais tarde, Método Update do OrderItemService está indiscriminadamente inativando o OrderItem, ajeitar isso mais tarde
	@CacheEvict(value = "orders", allEntries = true)
	public Order inactiveOrderItem(Long userId, Long orderId, Long orderItemId) {

		List<Order> clientOrders = findByClientId(userId);
		Order order = findById(orderId);
		if (!clientOrders.contains(order))
			throw new InvalidArgumentException("Client can update only their own orders");

		for (OrderItem oi : order.getItems()) {
			if (oi.getId() == orderItemId)
				orderItemService.update(orderItemId);
		}
		return findById(orderId);
	}

}
