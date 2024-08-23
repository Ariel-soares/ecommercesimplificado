package com.arielsoares.ecommercesimplificado.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.arielsoares.ecommercesimplificado.entities.User;
import com.arielsoares.ecommercesimplificado.repositories.UserRepository;

public class UserService {

	@Autowired
	private UserRepository repository;

	public List<User> findAll() {
		return repository.findAll();
	}

	public User findById(Long id) {
		Optional<User> obj = repository.findById(id);
		return obj.get();
	}

	public User insert(User user) {
		return repository.save(user);
	}

	public void delete(Long id) {
		repository.deleteById(id);
	}
	
	/*
	 * public User update(Long id, User user) { User obj =
	 * repository.getReferenceById(id); updateData(obj, user); return
	 * repository.save(obj); }
	 */

}
