package com.arielsoares.ecommercesimplificado.infra.security;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.arielsoares.ecommercesimplificado.entities.User;
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
					.withIssuedAt(user.getLastPasswordChangeDate().toInstant(ZoneOffset.UTC))
					.withExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationMs))
					.sign(Algorithm.HMAC256(jwtSecret.getBytes()));
		} catch (JWTCreationException e) {
			// Personalizar a excessão futuramente
			throw new RuntimeException("Error while authenticating");
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

			System.out.println(email);

			User user = userService.findByEmail(email).orElseThrow();

			System.out.println(tokenIssueDate);
			System.out.println(user.getLastPasswordChangeDate().truncatedTo(ChronoUnit.SECONDS));
			LocalDateTime userTime = user.getLastPasswordChangeDate().truncatedTo(ChronoUnit.SECONDS);

			if (user.getLastPasswordChangeDate() != null && !tokenIssueDate.equals(userTime)) {
				throw new JWTVerificationException("Token invalid due to password change.");
				//System.out.println("TOKEN EXPIRADO");
			}

			return JWT.require(algorithm).withIssuer("ecommercesimplificado").build().verify(token).getSubject();
			/*
			 * Algorithm algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
			 * 
			 * var verifier =
			 * JWT.require(algorithm).withIssuer("ecommercesimplificado").build();
			 * 
			 * var decodedJWT = verifier.verify(token);
			 * 
			 * // Obter a data de emissão do token Instant issuedAt =
			 * decodedJWT.getIssuedAt().toInstant(); // LocalDateTime tokenIssueDate = //
			 * issuedAt;//.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
			 * 
			 * // Obter o email do usuário a partir do token String email =
			 * decodedJWT.getSubject();
			 * 
			 * // Buscar o usuário pelo email User user =
			 * userService.findByEmail(email).orElseThrow(); // Implementar este método no
			 * seu UserService
			 * 
			 * // Comparar a data de emissão do token com a última troca de senha do usuário
			 * if (user.getLastPasswordChangeDate() != null &&
			 * !issuedAt.equals(user.getLastPasswordChangeDate())) { throw new
			 * JWTVerificationException("Token invalid due to password change."); }
			 * 
			 * return
			 * JWT.require(algorithm).withIssuer("ecommercesimplificado").build().verify(
			 * token).getSubject();
			 */
		} catch (JWTVerificationException exception) {
			return "";
		}
	}

}
