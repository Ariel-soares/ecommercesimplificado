package com.arielsoares.ecommercesimplificado.infra.security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.arielsoares.ecommercesimplificado.entities.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.JWTVerifier;

@Service
public class TokenService {

	@Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms}")
    private long jwtExpirationMs;

    public String generateToken(User user) {
    	
    	try {
    		return JWT.create()
    			.withIssuer("ecommercesimplificado")
                .withSubject(user.getEmail())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date((new Date()).getTime() + jwtExpirationMs))
                .sign(Algorithm.HMAC256(jwtSecret));
        }catch(JWTCreationException e) {
        	//Personalizar a excessão futuramente
        	throw new RuntimeException("Error while authenticating");
        }
    }

    public String getUsernameFromToken(String token) {
        return JWT.decode(token).getSubject();
    }

    public String validateToken(String token) {
        try{
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
            return JWT.require(algorithm)
                    .withIssuer("ecommercesimplificado")
                    .build()
                    .verify(token)
                    .getSubject();
        }catch(JWTVerificationException exception){
            return "";
        }
    }
    
}
