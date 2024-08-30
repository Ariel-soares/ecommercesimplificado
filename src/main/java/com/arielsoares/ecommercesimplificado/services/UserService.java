package com.arielsoares.ecommercesimplificado.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arielsoares.ecommercesimplificado.entities.User;
import com.arielsoares.ecommercesimplificado.repositories.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository repository;

	public List<User> findAll() {
		return repository.findAll();
	}
	
	public User findById(Long id) {
		Optional<User> obj = repository.findById(id);
		return obj.orElseThrow(() -> new RuntimeException("User not found"));
	}

	public User registerUser(User user) {
		if(this.repository.findByEmail(user.getEmail()).isPresent())throw new RuntimeException("Email already being used" + user.getEmail());
		return repository.save(user);
    }

	public void delete(Long id) {
		repository.deleteById(id);
	}
	
	public User findByEmail(String email) {
		Optional<User> user = repository.findByEmail(email);
		return user.orElseThrow(() -> new RuntimeException("User not found with Email " + email));
	}

}
