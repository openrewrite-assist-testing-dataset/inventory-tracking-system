package com.example.inventory.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.inventory.core.User;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

import java.util.Optional;

public class JwtAuthenticator implements Authenticator<String, User> {
    
    private final Algorithm algorithm;
    
    public JwtAuthenticator(String secret) {
        this.algorithm = Algorithm.HMAC256(secret);
    }
    
    @Override
    public Optional<User> authenticate(String token) throws AuthenticationException {
        try {
            DecodedJWT jwt = JWT.require(algorithm).build().verify(token);
            
            String username = jwt.getSubject();
            String email = jwt.getClaim("email").asString();
            String role = jwt.getClaim("role").asString();
            Long userId = jwt.getClaim("userId").asLong();
            
            if (username != null && email != null && role != null && userId != null) {
                return Optional.of(new User(userId, username, email, role));
            }
        } catch (JWTVerificationException e) {
            // Invalid token
        }
        
        return Optional.empty();
    }
}