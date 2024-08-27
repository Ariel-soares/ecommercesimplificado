package com.arielsoares.ecommercesimplificado.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arielsoares.ecommercesimplificado.entities.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long>{

}
