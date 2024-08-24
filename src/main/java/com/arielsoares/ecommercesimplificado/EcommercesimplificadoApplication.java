package com.arielsoares.ecommercesimplificado;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class EcommercesimplificadoApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommercesimplificadoApplication.class, args);
	}

}
