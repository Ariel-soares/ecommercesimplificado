package com.arielsoares.ecommercesimplificado.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arielsoares.ecommercesimplificado.entities.User;

public interface UserRepository extends JpaRepository<User, Long>{

	Optional<User> findByUsername(String username); 
	
}
