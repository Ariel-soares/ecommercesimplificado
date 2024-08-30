package com.arielsoares.ecommercesimplificado.controllers.auth;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.arielsoares.ecommercesimplificado.controllers.auth.DTO.LoginRequestDTO;
import com.arielsoares.ecommercesimplificado.controllers.auth.DTO.RegisterRequestDTO;
import com.arielsoares.ecommercesimplificado.controllers.auth.DTO.ResponseDTO;
import com.arielsoares.ecommercesimplificado.entities.User;
import com.arielsoares.ecommercesimplificado.infra.security.TokenService;
import com.arielsoares.ecommercesimplificado.services.UserService;

@RestController
@RequestMapping(value = "/auth")
public class AuthenticationController {

	private UserService userService;
	private TokenService tokenService;
	private final PasswordEncoder passwordEncoder;

	public AuthenticationController(UserService userService, TokenService tokenService,
			PasswordEncoder passwordEncoder) {
		this.userService = userService;
		this.tokenService = tokenService;
		this.passwordEncoder = passwordEncoder;
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody RegisterRequestDTO body) {
		
		System.out.println(body.email() + ", " + body.username());
		
		User newUser = new User();
		newUser.setEmail(body.email());
		newUser.setUsername(body.username());
		newUser.setPassword(passwordEncoder.encode(body.password()));
		
		userService.registerUser(newUser);
		
		String token = tokenService.generateToken(newUser);
		
		return ResponseEntity.ok(new ResponseDTO(newUser.getUsername(), token));
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequestDTO body) {
		/*
		 * try { Authentication authentication = authenticationManager .authenticate(new
		 * UsernamePasswordAuthenticationToken(username, password));
		 * 
		 * String token = jwtTokenProvider.generateToken(authentication); return
		 * ResponseEntity.ok(new AuthResponse(token)); } catch (AuthenticationException
		 * e) { return
		 * ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: "
		 * + e.getMessage()); }
		 */

		User user = userService.findByEmail(body.email());

		if (passwordEncoder.matches(user.getPassword(), body.password())) {
			String token = tokenService.generateToken(user);
			return ResponseEntity.ok(new ResponseDTO(user.getUsername(), token));
		}

		return ResponseEntity.badRequest().build();
	}
}
