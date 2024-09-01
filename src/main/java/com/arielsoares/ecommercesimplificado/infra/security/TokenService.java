package com.arielsoares.ecommercesimplificado.infra.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    public String generateToken(String email) {
    	
    	try {
    		return JWT.create()
    			.withIssuer("ecommercesimplificado")
                .withSubject(email)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .sign(Algorithm.HMAC256(jwtSecret.getBytes()));
        }catch(JWTCreationException e) {
        	//Personalizar a excessão futuramente
        	throw new RuntimeException("Error while authenticating");
        }
    }

    public String validateToken(String token) {
        try{
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
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
