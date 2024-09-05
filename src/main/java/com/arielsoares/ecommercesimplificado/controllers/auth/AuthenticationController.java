package com.arielsoares.ecommercesimplificado.controllers.auth;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arielsoares.ecommercesimplificado.controllers.DTO.ResetPasswordDTO;
import com.arielsoares.ecommercesimplificado.controllers.DTO.confirmResetResponseDTO;
import com.arielsoares.ecommercesimplificado.controllers.auth.DTO.LoginRequestDTO;
import com.arielsoares.ecommercesimplificado.controllers.auth.DTO.RegisterRequestDTO;
import com.arielsoares.ecommercesimplificado.controllers.auth.DTO.ResponseDTO;
import com.arielsoares.ecommercesimplificado.services.utils.AuthenticationService;

@RestController
@RequestMapping(value = "/auth")
public class AuthenticationController {

	@Autowired
	private AuthenticationService authenticationService;

	//CLASSE TOTALMENTE TESTADA
	
	@PostMapping("/register")
	public ResponseEntity<ResponseDTO> register(@RequestBody RegisterRequestDTO body) {
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

		if (authenticationService.resetPassword(email))
			return ResponseEntity.ok().build();
		return ResponseEntity.badRequest().build();
	}

	@PostMapping("/reset-password/confirm")
	public ResponseEntity<confirmResetResponseDTO> confirmResetPassword(@RequestBody ResetPasswordDTO body) {
		String newToken = authenticationService.confirmResetPassword(body.token(), body.newPassword());
		return ResponseEntity.ok(new confirmResetResponseDTO("Password reseted succesfully", newToken));
	}

}
