package com.arielsoares.ecommercesimplificado.controllers;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.arielsoares.ecommercesimplificado.entities.Order;
import com.arielsoares.ecommercesimplificado.entities.User;
import com.arielsoares.ecommercesimplificado.services.OrderService;

@RestController
@RequestMapping(value = "/orders")
public class OrderController {

	@Autowired
	private OrderService service;

	@GetMapping(value = "/all")
	public ResponseEntity<List<Order>> findAll() {
		List<Order> list = service.findAll();
		return ResponseEntity.ok().body(list);
	}

	//OK
	@GetMapping(value = "/orders")
	public ResponseEntity<List<Order>> clientOrders() {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return ResponseEntity.ok().body(service.findByClientId(user.getId()));
	}

	//OK
	@GetMapping(value = "/userOrders")
	public ResponseEntity<List<Order>> findByClientId() {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return ResponseEntity.ok().body(service.findByClientId(user.getId()));
	}
	//OK
	@PostMapping(value = "/newOrder")
	public ResponseEntity<Order> insert() {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Order newOrder = service.insert(new Order(user));
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newOrder.getId())
				.toUri();
		return ResponseEntity.created(uri).body(newOrder);
	}

	// Refatorar futuramente
	@PostMapping(value = "/{orderId}/item")
	public ResponseEntity<Order> addOrderItem(@RequestBody Map<String, Long> request, @PathVariable Long orderId) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Order order = service.addOrderItem(user.getId(), orderId, request.get("quantity").intValue(),
				request.get("productId"));

		return ResponseEntity.ok().body(order);
	}
	
	//OK
	@PutMapping(value = "/{orderId}/inactiveOrderItem/{orderItemId}")
	public ResponseEntity<Order> inactiveOrderItem(@PathVariable Long orderId, @PathVariable Long orderItemId) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Order order = service.inactiveOrderItem(orderId, orderItemId);
		return ResponseEntity.ok().body(order);
	}

	
	@PutMapping(value = "/{orderId}/orderStatus/{orderStatus}")
	public ResponseEntity<Order> updateOrderStatus(@PathVariable Long orderId, @PathVariable String orderStatus) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Order order = service.update(user, orderId, orderStatus);
		return ResponseEntity.ok().body(order);
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

}
