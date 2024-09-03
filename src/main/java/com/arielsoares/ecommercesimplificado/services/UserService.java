package com.arielsoares.ecommercesimplificado.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arielsoares.ecommercesimplificado.entities.User;
import com.arielsoares.ecommercesimplificado.entities.enums.UserRole;
import com.arielsoares.ecommercesimplificado.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {

	@Autowired
	private UserRepository repository;

	public List<User> findAll() {
		return repository.findAll();
	}

	public User findById(Long id) {
		Optional<User> obj = repository.findById(id);
		return obj.orElseThrow(() -> new EntityNotFoundException("Product not found"));
	}

	public User registerUser(User user) {
		if (this.repository.findByEmail(user.getEmail()).isPresent())
			throw new RuntimeException("Email already being used" + user.getEmail());
		return repository.save(user);
	}

	public void delete(Long id) {
		repository.deleteById(id);
	}

	public Optional<User> findByEmail(String email) {
		Optional<User> user = repository.findByEmail(email);
		return user;
	}

	public User update(Long id, String role, User operatorUser) {

		User operator = findById(operatorUser.getId());
		User newUser = findById(id);

		if (newUser.getRole() == UserRole.ADMIN && operator.getRole() != UserRole.ADMIN)
			throw new IllegalArgumentException("ADMIN users can only be modified by other ADMIN users");

		newUser.setRole(UserRole.valueOf(role.toUpperCase()));

		return repository.save(newUser);

	}

}
