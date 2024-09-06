package com.arielsoares.ecommercesimplificado.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class CacheInitializer implements CommandLineRunner {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Override
	public void run(String... args) throws Exception {
		// Usa o execute para enviar o comando FLUSHDB diretamente para o Redis
		redisTemplate.execute((connection) -> {
			connection.serverCommands().flushDb();
			return "OK";
		});
		System.out.println("Todos os caches no Redis foram limpos.");
	}
}