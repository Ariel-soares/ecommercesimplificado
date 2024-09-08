package com.arielsoares.ecommercesimplificado.repositories;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arielsoares.ecommercesimplificado.entities.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{

	List<Order> findByClientId(Long clientId);
	
	List<Order> findByMomentBetween(Instant startDate, Instant endDate);

}
