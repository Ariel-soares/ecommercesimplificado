package com.arielsoares.ecommercesimplificado.services.utils;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.arielsoares.ecommercesimplificado.entities.User;
import com.arielsoares.ecommercesimplificado.entities.utils.PasswordResetToken;
import com.arielsoares.ecommercesimplificado.repositories.utils.PasswordResetTokenRepository;
import com.arielsoares.ecommercesimplificado.services.UserService;
import com.arielsoares.ecommercesimplificado.services.mail.EmailService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class PasswordManagerService {

	private UserService userService;
	private PasswordResetTokenRepository tokenRepository;
	private PasswordEncoder passwordEncoder;
	private EmailService mailService;
	
	

	public PasswordManagerService(UserService userService, PasswordResetTokenRepository tokenRepository,
			PasswordEncoder passwordEncoder, EmailService mailService) {
		this.userService = userService;
		this.tokenRepository = tokenRepository;
		this.passwordEncoder = passwordEncoder;
		this.mailService = mailService;
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

	public boolean resetPassword(String token, String newPassword) {
		PasswordResetToken resetToken = tokenRepository.findByToken(token);

		if (resetToken == null || resetToken.isExpired()) {
			return false;
		}

		User user = resetToken.getUser();
		user.setPassword(passwordEncoder.encode(newPassword));
		// Adicionar método update do UserService e então atualizar aqui a referência de
		// método, para parar de usar o insert
		userService.insert(user);

		tokenRepository.delete(resetToken);

		mailService.sendSimpleEmail(user.getEmail(), "Password Reset Successful",
				"Your password has been reset successfully.");

		return true;
	}

}
