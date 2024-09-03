package com.arielsoares.ecommercesimplificado.controllers.auth;

import java.util.Map;

import org.springframework.http.HttpStatus;
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
import com.arielsoares.ecommercesimplificado.services.mail.EmailService;
import com.arielsoares.ecommercesimplificado.services.utils.PasswordManagerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/auth")
public class AuthenticationController {

	private final AuthenticationManager authenticationManager;
	private UserService userService;
	private TokenService tokenService;
	private final PasswordEncoder passwordEncoder;
	private EmailService mailService;
	private PasswordManagerService passwordService;

	public AuthenticationController(UserService userService, TokenService tokenService, PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager, EmailService mailService, PasswordManagerService passwordService) {
		this.userService = userService;
		this.tokenService = tokenService;
		this.authenticationManager = authenticationManager;
		this.passwordEncoder = passwordEncoder;
		this.mailService = mailService;
		this.passwordService = passwordService;
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO body) {

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
	
	@PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String resetToken = passwordService.createPasswordResetToken(email);

        if (resetToken != null) {
            String subject = "Password Reset Request";
            String body = "Use the following token to reset your password on the endpoint: /reset-password/confirm\n" + resetToken;

            mailService.sendSimpleEmail(email, subject, body);
            return ResponseEntity.ok("Password reset email sent.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email address not found.");
        }
    }
	
	@PostMapping("/reset-password/confirm")
	public ResponseEntity<String> confirmResetPassword(@RequestBody Map<String, String> request) {
	    String token = request.get("token");
	    String newPassword = request.get("newPassword");
	    
	    System.out.println("token: " + token + " PAssword: " + newPassword);

	    boolean isReset = passwordService.resetPassword(token, newPassword);

	    if (isReset) {
	        return ResponseEntity.ok("Password reset successfully.");
	    } else {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token or token expired.");
	    }
	}
	
}
