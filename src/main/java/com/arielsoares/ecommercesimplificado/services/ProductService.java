package com.arielsoares.ecommercesimplificado.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.arielsoares.ecommercesimplificado.entities.Product;
import com.arielsoares.ecommercesimplificado.exception.ResourceNotFoundException;
import com.arielsoares.ecommercesimplificado.repositories.ProductRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository repository;

	@Cacheable(value = "products")
	public List<Product> findAll() {
		return repository.findAll();
	}

	@CacheEvict(value = "products", key = "'allProducts'")
	public Product findById(Long id) {
		return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));
	}

	@CachePut(value = "products", key = "'allProducts'")
	public Product insert(Product Product) {
		return repository.save(Product);
	}

	@CacheEvict(value = "products", key = "'allProducts'")
	public void delete(Long id) {
		repository.deleteById(id);
	}

	@CachePut(value = "products", key = "'allProducts'")
	public Product update(Long id, Product newProduct) {

		Optional<Product> optionalProduct = repository.findById(id);

		if (optionalProduct.isPresent()) {

			Product product = optionalProduct.get();
			product.setName(newProduct.getName());
			product.setDescription(newProduct.getDescription());
			product.setPrice(newProduct.getPrice());
			product.setStorage_quantity(newProduct.getStorage_quantity());
			product.setActive(newProduct.getActive());

			return repository.save(product);
		} else {
			throw new EntityNotFoundException("Product not found");
		}
	}
	
}
