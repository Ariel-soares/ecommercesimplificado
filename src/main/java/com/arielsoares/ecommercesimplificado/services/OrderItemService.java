package com.arielsoares.ecommercesimplificado.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arielsoares.ecommercesimplificado.entities.OrderItem;
import com.arielsoares.ecommercesimplificado.repositories.OrderItemRepository;

@Service
public class OrderItemService {

	@Autowired
	private OrderItemRepository repository;

	public List<OrderItem> findAll() {
		return repository.findAll();
	}

	public OrderItem findById(Long id) {
		Optional<OrderItem> obj = repository.findById(id);
		return obj.orElseThrow();
	}

	public OrderItem insert(OrderItem OrderItem) {
		return repository.save(OrderItem);
	}

	public void delete(Long id) {
		repository.deleteById(id);
	}

	public OrderItem update(Long id) {
		OrderItem obj = findById(id);
		obj.setActive(false);
		return repository.save(obj);
	}

}
