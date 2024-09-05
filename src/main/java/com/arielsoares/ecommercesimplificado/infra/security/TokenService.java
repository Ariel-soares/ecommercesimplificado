package com.arielsoares.ecommercesimplificado.infra.security;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.arielsoares.ecommercesimplificado.entities.User;
import com.arielsoares.ecommercesimplificado.exception.InvalidTokenException;
import com.arielsoares.ecommercesimplificado.exception.ResourceNotFoundException;
import com.arielsoares.ecommercesimplificado.services.UserService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;

@Service
public class TokenService {

	@Value("${jwt.secret}")
	private String jwtSecret;
	@Value("${jwt.expiration-ms}")
	private long jwtExpirationMs;
	@Autowired
	UserService userService;

	public String generateToken(String email) {

		try {
			User user = userService.findByEmail(email).orElseThrow();
			user.setLastPasswordChangeDate(LocalDateTime.now());
			userService.insert(user);

			return JWT.create().withIssuer("ecommercesimplificado").withSubject(email)
					.withIssuedAt(user.getLastGeneratedToken().toInstant(ZoneOffset.UTC))
					.withExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationMs))
					.sign(Algorithm.HMAC256(jwtSecret.getBytes()));
		} catch (JWTCreationException e) {
			throw new JWTCreationException("Error while authenticating", e);
		}
	}

	public String validateToken(String token) {
		try {

			Algorithm algorithm = Algorithm.HMAC256(jwtSecret.getBytes());

			Date issuedAt = JWT.require(algorithm).withIssuer("ecommercesimplificado").build().verify(token)
					.getIssuedAt();

			LocalDateTime tokenIssueDate = issuedAt.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime();

			String email = JWT.require(algorithm).withIssuer("ecommercesimplificado").build().verify(token)
					.getSubject();

			User user = userService.findByEmail(email)
					.orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

			LocalDateTime userTime = user.getLastGeneratedToken().truncatedTo(ChronoUnit.SECONDS);

			if (user.getLastGeneratedToken() != null && !tokenIssueDate.equals(userTime)) {
				throw new InvalidTokenException("Token invalid due to password change.");
			}

			return JWT.require(algorithm).withIssuer("ecommercesimplificado").build().verify(token).getSubject();
		} catch (JWTVerificationException exception) {
			return "";
		}
	}

}
