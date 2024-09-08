package com.arielsoares.ecommercesimplificado.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arielsoares.ecommercesimplificado.entities.User;
import com.arielsoares.ecommercesimplificado.entities.enums.UserRole;
import com.arielsoares.ecommercesimplificado.exception.InvalidArgumentException;
import com.arielsoares.ecommercesimplificado.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {

	@Autowired
	private UserRepository repository;

	public List<User> findAll() {
		return repository.findAll();
	}

	public User insert(User user) {
		return repository.save(user);
	}

	public User findById(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Product not found with Id: " + id));
	}

	public User registerUser(User user) {
		if (this.repository.findByEmail(user.getEmail()).isPresent())
			throw new InvalidArgumentException("Email already being used");
		return repository.save(user);
	}

	public void delete(Long id) {
		repository.deleteById(id);
	}

	public Optional<User> findByEmail(String email) {
		Optional<User> user = repository.findByEmail(email);
		return user;
	}

	public User updateRole(Long id, String role, User operatorUser) {

		User operator = findById(operatorUser.getId());
		User newUser = findById(id);

		if (newUser.getRole() == UserRole.ADMIN && operator.getRole() != UserRole.ADMIN)
			throw new InvalidArgumentException("ADMIN users can only be modified by other ADMIN users");

		newUser.setRole(UserRole.valueOf(role.toUpperCase()));
		return updateUser(id, newUser);
	}

	public User inactivateUser(Long id, User operatorUser) {
		User operator = findById(operatorUser.getId());
		User newUser = findById(id);

		if (newUser.getRole() == UserRole.ADMIN && operator.getRole() != UserRole.ADMIN)
			throw new InvalidArgumentException("ADMIN users can only be modified by other ADMIN users");

		newUser.setIs_active(false);
		return updateUser(id, newUser);
	}

	public User updateUser(Long Id, User user) {
		User updatedUser = findById(Id);
		updatedUser.setIs_active(user.getIs_active());
		updatedUser.setPassword(user.getPassword());
		updatedUser.setUsername(user.getUsername());
		updatedUser.setRole(user.getRole());

		return repository.save(updatedUser);
	}

}
