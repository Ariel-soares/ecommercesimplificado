package com.arielsoares.ecommercesimplificado.controllers.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arielsoares.ecommercesimplificado.controllers.auth.DTO.LoginRequestDTO;
import com.arielsoares.ecommercesimplificado.controllers.auth.DTO.RegisterRequestDTO;
import com.arielsoares.ecommercesimplificado.controllers.auth.DTO.ResponseDTO;
import com.arielsoares.ecommercesimplificado.entities.User;
import com.arielsoares.ecommercesimplificado.infra.security.TokenService;
import com.arielsoares.ecommercesimplificado.services.UserService;

@RestController
@RequestMapping(value = "/auth")
public class AuthenticationController {

	private final AuthenticationManager authenticationManager;
	private UserService userService;
	private TokenService tokenService;
	private final PasswordEncoder passwordEncoder;

	public AuthenticationController(UserService userService, TokenService tokenService, PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager) {
		this.userService = userService;
		this.tokenService = tokenService;
		this.authenticationManager = authenticationManager;
		this.passwordEncoder = passwordEncoder;
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody RegisterRequestDTO body) {

		User newUser = new User();
		newUser.setEmail(body.email());
		newUser.setUsername(body.username());
		newUser.setPassword(passwordEncoder.encode(body.password()));

		userService.registerUser(newUser);

		String token = tokenService.generateToken(newUser.getEmail());

		return ResponseEntity.ok(new ResponseDTO(newUser.getUsername(), token));
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequestDTO body) {
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(body.email(), body.password()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		String jwt = tokenService.generateToken(body.email());

		return ResponseEntity.ok(new ResponseDTO(body.email(), jwt));
	}
}
