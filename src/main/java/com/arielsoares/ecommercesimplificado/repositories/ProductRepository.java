package com.arielsoares.ecommercesimplificado.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arielsoares.ecommercesimplificado.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{

}
