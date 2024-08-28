package com.arielsoares.ecommercesimplificado.controllers;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import com.arielsoares.ecommercesimplificado.services.OrderService;
import com.arielsoares.ecommercesimplificado.services.UserService;

@RestController
@RequestMapping(value = "/orders")
public class OrderController {

	@Autowired
	private OrderService service;
	
	@Autowired
	private UserService userService;

	@GetMapping
	public ResponseEntity<List<Order>> findAll() {
		List<Order> list = service.findAll();
		return ResponseEntity.ok().body(list);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<Order> findById(@PathVariable Long id) {
		Order obj = service.findById(id);
		return ResponseEntity.ok().body(obj);
	}
	
	@GetMapping(value = "/client/{id}")
	public ResponseEntity<List<Order>> findByClientId(@PathVariable Long id){
		return ResponseEntity.ok().body(service.findByClientId(id));
	}
	
	//Refatorar futuramente para não utilizar um User Service nesta classe
	@PostMapping
	public ResponseEntity<Order> insert(@RequestBody Map<String, Long> request) {
		Order newOrder = service.insert(new Order(userService.findById(request.get("id"))));
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newOrder.getId()).toUri();
		return ResponseEntity.created(uri).body(newOrder);
	}
	
	@PostMapping(value = "/{orderId}/items")
	public ResponseEntity<Order> addOrderItem(@RequestBody Map<String, Long> request, @PathVariable Long orderId ){
		
		Order order = service.addOrderItem(request.get("userId"), orderId, request.get("quantity").intValue(), request.get("productId"));
		
		return ResponseEntity.ok().body(order);
	}
	
	@PutMapping(value = "/{orderId}/items/{orderItemId}")
	public ResponseEntity<Order> inactiveOrderItem(@PathVariable Long orderId, @PathVariable Long orderItemId){
		Order order = service.inactiveOrderItem(orderId, orderItemId);
		return ResponseEntity.ok().body(order);
	}
	
	@PutMapping(value = "/{orderId}/{orderStatus}")
	public ResponseEntity<Order> updateOrderStatus(@PathVariable Long orderId, @PathVariable String orderStatus){
		Order order = service.update(orderId, orderStatus);
		return ResponseEntity.ok().body(order);
	}
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

}
