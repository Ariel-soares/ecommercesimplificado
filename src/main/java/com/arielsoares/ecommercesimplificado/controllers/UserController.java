package com.arielsoares.ecommercesimplificado.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arielsoares.ecommercesimplificado.entities.User;
import com.arielsoares.ecommercesimplificado.services.UserService;

@RestController
@RequestMapping(value = "/users")
public class UserController {

	@Autowired
	private UserService service;

	@GetMapping
	public ResponseEntity<List<User>> findAll() {
		List<User> list = service.findAll();
		return ResponseEntity.ok().body(list);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		User obj = service.findById(id);
		return ResponseEntity.ok().body(obj);
	}

	@PutMapping(value = "/updateUserRole/{id}/role/{userRole}")
	public ResponseEntity<User> updateRole(@PathVariable Long id, @PathVariable String userRole) {
		User operatorUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User user = service.updateRole(id, userRole, operatorUser);
		return ResponseEntity.ok().body(user);
	}

	@PutMapping(value = "/inactiveUser/{id}")
	public ResponseEntity<User> inactivateUser(@PathVariable Long id) {
		User operatorUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User user = service.inactivateUser(id, operatorUser);
		return ResponseEntity.ok().body(user);
	}
	

}
