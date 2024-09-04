package com.arielsoares.ecommercesimplificado.services.utils;

import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.arielsoares.ecommercesimplificado.entities.User;
import com.arielsoares.ecommercesimplificado.entities.utils.PasswordResetToken;
import com.arielsoares.ecommercesimplificado.infra.security.TokenService;
import com.arielsoares.ecommercesimplificado.repositories.utils.PasswordResetTokenRepository;
import com.arielsoares.ecommercesimplificado.services.UserService;
import com.arielsoares.ecommercesimplificado.services.mail.EmailService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AuthenticationService {

	private UserService userService;
	private PasswordResetTokenRepository tokenRepository;
	private PasswordEncoder passwordEncoder;
	private EmailService mailService;
	private TokenService tokenService;
	private AuthenticationManager authenticationManager;

	public AuthenticationService(UserService userService, PasswordResetTokenRepository tokenRepository,
			PasswordEncoder passwordEncoder, EmailService mailService, TokenService tokenService,
			AuthenticationManager authenticationManager) {
		this.userService = userService;
		this.tokenRepository = tokenRepository;
		this.passwordEncoder = passwordEncoder;
		this.mailService = mailService;
		this.authenticationManager = authenticationManager;
		this.tokenService = tokenService;
	}

	public String register(String email, String username, String password) {

		User newUser = new User();
		newUser.setEmail(email);
		newUser.setUsername(username);
		newUser.setPassword(passwordEncoder.encode(password));

		userService.registerUser(newUser);
		String token = tokenService.generateToken(newUser.getEmail());

		return token;
	}

	public String login(String email, String password) {
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(email, password));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		String token = tokenService.generateToken(email);
		return token;
	}

	public Boolean resetPassword(String email) {
		String resetToken = createPasswordResetToken(email);

		if (resetToken != null) {
			String subject = "Password Reset Request";
			String body = "Use the following token to reset your password on the endpoint: /reset-password/confirm\n"
					+ resetToken;

			mailService.sendSimpleEmail(email, subject, body);
			return true;
		} else {
			return false;
		}
	}

	public String confirmResetPassword(String token, String newPassword) {
		PasswordResetToken resetToken = tokenRepository.findByToken(token);

		if (resetToken == null || resetToken.isExpired()) {
			throw new RuntimeException("INVALID TOKEN");
		}

		User user = resetToken.getUser();
		user.setPassword(passwordEncoder.encode(newPassword));
		// Adicionar método update do UserService e então atualizar aqui a referência de
		// método, para parar de usar o insert
		userService.insert(user);
		tokenRepository.delete(resetToken);

		String newToken = tokenService.generateToken(user.getEmail());

		mailService.sendSimpleEmail(user.getEmail(), "Password Reset Successful",
				"Your password has been reset successfully.");
		return newToken;
	}
	
	public String createPasswordResetToken(String email) {
		User user = userService.findByEmail(email)
				.orElseThrow(() -> new EntityNotFoundException("User not found for email: " + email));
		if (user == null) {
			return null;
		}

		String token = UUID.randomUUID().toString();
		PasswordResetToken resetToken = new PasswordResetToken(token, user);
		tokenRepository.save(resetToken);

		return token;
	}

}
