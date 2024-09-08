package com.arielsoares.ecommercesimplificado.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.arielsoares.ecommercesimplificado.controllers.DTO.OrderDTOWithoutClient;
import com.arielsoares.ecommercesimplificado.controllers.DTO.OrderItemDTO;
import com.arielsoares.ecommercesimplificado.entities.Order;
import com.arielsoares.ecommercesimplificado.entities.User;
import com.arielsoares.ecommercesimplificado.services.OrderService;

import jakarta.validation.Valid;

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

	@GetMapping(value = "/myOrders")
	public ResponseEntity<List<OrderDTOWithoutClient>> clientOrders() {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return ResponseEntity.ok().body(service.findByClient(user.getId()));
	}

	@GetMapping(value = "/userOrders/{userId}")
	public ResponseEntity<List<OrderDTOWithoutClient>> findByClientId(@PathVariable Long userId) {
		return ResponseEntity.ok().body(service.findByClient(userId));
	}

	@PostMapping(value = "/newOrder")
	public ResponseEntity<OrderDTOWithoutClient> insert() {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		OrderDTOWithoutClient newOrder = service.insert(new Order(user));
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newOrder.id()).toUri();
		return ResponseEntity.created(uri).body(newOrder);
	}

	@PostMapping(value = "/{orderId}/item")
	public ResponseEntity<OrderDTOWithoutClient> addOrderItem(@Valid @RequestBody OrderItemDTO body,
			@PathVariable Long orderId) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		OrderDTOWithoutClient orderDTO = service.addOrderItem(user.getId(), orderId, body.quantity(), body.productId());
		return ResponseEntity.ok().body(orderDTO);
	}

	@PutMapping(value = "/{orderId}/inactivateOrderItem/{orderItemId}")
	public ResponseEntity<OrderDTOWithoutClient> inactiveOrderItem(@PathVariable Long orderId,
			@PathVariable Long orderItemId) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		OrderDTOWithoutClient order = service.inactiveOrderItem(user.getId(), orderId, orderItemId);
		return ResponseEntity.ok().body(order);
	}

	@PutMapping(value = "/{orderId}/orderStatus/{orderStatus}")
	public ResponseEntity<OrderDTOWithoutClient> updateOrderStatus(@PathVariable Long orderId,
			@PathVariable String orderStatus) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		OrderDTOWithoutClient order = service.update(user.getId(), orderId, orderStatus);
		return ResponseEntity.ok().body(order);
	}
}
