package com.arielsoares.ecommercesimplificado.infra.security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.arielsoares.ecommercesimplificado.entities.User;
import com.arielsoares.ecommercesimplificado.entities.enums.UserRole;
import com.arielsoares.ecommercesimplificado.repositories.UserRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail("admin@example.com").isEmpty()) {
            User admin = new User();
            admin.setUsername("adminMaster");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(UserRole.ADMIN);
            admin.setIs_active(true);
            userRepository.save(admin);

            System.out.println("Admin user created with email: admin@example.com");
        }
    }
}
