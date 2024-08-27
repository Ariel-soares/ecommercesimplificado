package com.arielsoares.ecommercesimplificado.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.arielsoares.ecommercesimplificado.entities.Order;
import com.arielsoares.ecommercesimplificado.entities.OrderItem;
import com.arielsoares.ecommercesimplificado.entities.Product;
import com.arielsoares.ecommercesimplificado.repositories.OrderRepository;

@Service
public class OrderService {

	@Autowired
	private OrderRepository repository;

	@Autowired
	private OrderItemService orderItemService;

	@Autowired
	private ProductService productService;

	@Cacheable(value = "allOrders")
	public List<Order> findAll() {
		return repository.findAll();
	}

	@Cacheable(value = "orders", key = "#id")
	public Order findById(Long id) {
		Optional<Order> obj = repository.findById(id);
		return obj.orElseThrow();
	}

	public Order insert(Order Order) {
		return repository.save(Order);
	}

	public void delete(Long id) {
		repository.deleteById(id);
	}

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

	public Order update(Long id, Order order) {
		Order obj = findById(id);
		obj.setStatus(order.getStatus());
		return insert(obj);
	}
	
	public List<Order> findByClientId(Long clientId){
		return repository.findByClientId(clientId);
	}

}
