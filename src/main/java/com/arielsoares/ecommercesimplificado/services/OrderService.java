package com.arielsoares.ecommercesimplificado.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arielsoares.ecommercesimplificado.entities.Order;
import com.arielsoares.ecommercesimplificado.repositories.OrderRepository;

@Service
public class OrderService {

	@Autowired
	private OrderRepository repository;

	public List<Order> findAll() {
		return repository.findAll();
	}

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
	
	
	/*
	 * public Order update(Long id, Order Order) { Order obj =
	 * repository.getReferenceById(id); updateData(obj, Order); return
	 * repository.save(obj); }
	 */

}
