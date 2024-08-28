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

	@Cacheable(value = "orders")
	public List<Order> findAll() {
		return repository.findAll();
	}

	@Cacheable(value = "orders", key = "#orderId")
	public Order findById(Long orderId) {
		Optional<Order> obj = repository.findById(orderId);
		return obj.orElseThrow();
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

	@CachePut(value = "orders", key = "#result.id")
	//@CacheEvict(value = "orders", allEntries = true)
	public Order update(Long id, String status) {
		
		if(!status.toUpperCase().equals("CANCELLED") && !status.toUpperCase().equals("DONE"))throw new IllegalArgumentException("Only CANCELLED status or DONE STATUS ACCEPTED ");
		
		Order obj = findById(id);
		obj.setStatus(OrderStatus.valueOf(status.toUpperCase()));
		return insert(obj);
	}

	@CachePut(value = "orders", key = "#result.id")
	@CacheEvict(value = "orders", allEntries = true)
	public Order addOrderItem(Long userId, Long orderId, Integer quantity, Long productId) {
		Product product = productService.findById(productId);
		Order order = findById(orderId);
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

	@CachePut(value = "orders", key = "#result.id")
	@CacheEvict(value = "orders", allEntries = true)
	public Order inactiveOrderItem(Long orderId, Long orderItemId) {
		Order order = findById(orderId);
		for (OrderItem oi : order.getItems()) {
			if (oi.getId() == orderItemId)
				orderItemService.update(orderItemId);
		}
		return findById(orderId);
	}

}
