package com.arielsoares.ecommercesimplificado.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.arielsoares.ecommercesimplificado.entities.Product;
import com.arielsoares.ecommercesimplificado.repositories.ProductRepository;

@Service
public class ProductService {

	@Autowired
	private ProductRepository repository;

	@Cacheable(value = "allProducts")
	public List<Product> findAll() {
		return repository.findAll();
	}

	@Cacheable(value = "products", key = "#id")
	public Product findById(Long id) {
		Optional<Product> obj = repository.findById(id);
		return obj.orElseThrow();
	}

	public Product insert(Product Product) {
		return repository.save(Product);
	}

	public void delete(Long id) {
		repository.deleteById(id);
	}
	
	/*
	 * public Product update(Long id, Product Product) { Product obj =
	 * repository.getReferenceById(id); updateData(obj, Product); return
	 * repository.save(obj); }
	 */

}
