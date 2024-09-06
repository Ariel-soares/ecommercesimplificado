package com.arielsoares.ecommercesimplificado.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arielsoares.ecommercesimplificado.entities.OrderItem;
import com.arielsoares.ecommercesimplificado.exception.ResourceNotFoundException;
import com.arielsoares.ecommercesimplificado.repositories.OrderItemRepository;

@Service
public class OrderItemService {

	@Autowired
	private OrderItemRepository repository;

	public List<OrderItem> findAll() {
		return repository.findAll();
	}

	public OrderItem findById(Long id) {
		return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("OrderItem not found with id " + id));
	}

	public OrderItem insert(OrderItem OrderItem) {
		return repository.save(OrderItem);
	}

	public void delete(Long id) {
		repository.deleteById(id);
	}

	public OrderItem update(OrderItem oi) {
		return repository.save(oi);
	}

}
