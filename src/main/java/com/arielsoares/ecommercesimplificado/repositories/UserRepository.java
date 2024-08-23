package com.arielsoares.ecommercesimplificado.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arielsoares.ecommercesimplificado.entities.User;

public interface UserRepository extends JpaRepository<User, Long>{

}
