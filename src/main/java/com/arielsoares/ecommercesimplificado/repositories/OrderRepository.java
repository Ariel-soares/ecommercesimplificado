package com.arielsoares.ecommercesimplificado.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arielsoares.ecommercesimplificado.entities.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{

}
