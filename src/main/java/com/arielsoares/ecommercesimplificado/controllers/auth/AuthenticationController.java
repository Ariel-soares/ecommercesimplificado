package com.arielsoares.ecommercesimplificado.controllers.auth;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arielsoares.ecommercesimplificado.controllers.auth.DTO.LoginRequestDTO;
import com.arielsoares.ecommercesimplificado.controllers.auth.DTO.RegisterRequestDTO;
import com.arielsoares.ecommercesimplificado.controllers.auth.DTO.ResetPasswordDTO;
import com.arielsoares.ecommercesimplificado.controllers.auth.DTO.ResponseDTO;
import com.arielsoares.ecommercesimplificado.services.mail.EmailService;
import com.arielsoares.ecommercesimplificado.services.utils.AuthenticationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/auth")
public class AuthenticationController {

	private EmailService mailService;
	private AuthenticationService authenticationService;

	public AuthenticationController(EmailService mailService, AuthenticationService authenticationService) {
		this.mailService = mailService;
		this.authenticationService = authenticationService;
	}

	@PostMapping("/register")
	public ResponseEntity<ResponseDTO> register(@Valid @RequestBody RegisterRequestDTO body) {
		String token = authenticationService.register(body.email(), body.username(), body.password());
		return ResponseEntity.ok(new ResponseDTO(body.email(), token));
	}

	@PostMapping("/login")
	public ResponseEntity<ResponseDTO> login(@RequestBody LoginRequestDTO body) {
		String token = authenticationService.login(body.email(), body.password());
		return ResponseEntity.ok(new ResponseDTO(body.email(), token));
	}

	@PostMapping("/reset-password")
	public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
		String email = request.get("email");
		String resetToken = authenticationService.createPasswordResetToken(email);

		if (resetToken != null) {
			String subject = "Password Reset Request";
			String body = "Use the following token to reset your password on the endpoint: /reset-password/confirm\n"
					+ resetToken;

			mailService.sendSimpleEmail(email, subject, body);
			return ResponseEntity.ok("Password reset email sent.");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email address not found.");
		}
	}

	// Apagar os Sysout depois
	@PostMapping("/reset-password/confirm")
	public ResponseEntity<ResponseDTO> confirmResetPassword(@RequestBody ResetPasswordDTO body) {

		System.out.println("token: " + body.token() + " Password: " + body.newPassword());

		String newToken = authenticationService.confirmResetPassword(body.token(), body.newPassword());

		return ResponseEntity.ok(new ResponseDTO("ariel.sfranco@protonmail.com", newToken));
	}

}
