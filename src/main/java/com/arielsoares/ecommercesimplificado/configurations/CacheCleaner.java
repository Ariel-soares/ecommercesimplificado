package com.arielsoares.ecommercesimplificado.configurations;

import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

@Component
public class CacheCleaner implements CommandLineRunner{


	@CacheEvict(value = {"products", "orders"}, allEntries = true)
	@Override
	public void run(String... args) throws Exception {
		System.out.println("Cleaning Cache");
	}
	
	
	
	

}
