package io.github.gabrielhe4.auth_service.auth;

import java.security.PrivateKey;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.github.gabrielhe4.auth_service.user.User;
import io.jsonwebtoken.Jwts;

@Service
public class JwtService {

    private final PrivateKey privateKey;
    private final long accesTokenExpireMs;

    JwtService(PrivateKey privateKey, 
        @Value("${jwt.access-token-expiry-ms}")long accessTokenExpireMs) { 
        this.privateKey = privateKey;
        this.accesTokenExpireMs = accessTokenExpireMs; 
    }

    public String generateAccessToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accesTokenExpireMs);

        return Jwts.builder()
            .subject(user.getId().toString())               // this is what services will read
            .claim("username", user.getUsername())
            .claim("role", user.getRole().name())       // USER or ADMIN
            .issuedAt(now)
            .expiration(expiry)
            .signWith(privateKey)                           // signs with RSA private key -> RS256
            .compact();
    }
}
