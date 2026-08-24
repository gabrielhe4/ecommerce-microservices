package io.github.gabrielhe4.auth_service.auth;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.github.gabrielhe4.auth_service.exception.InvalidRefreshTokenException;
import io.github.gabrielhe4.auth_service.model.RefreshToken;
import io.github.gabrielhe4.auth_service.repository.RefreshTokenRepository;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final long refreshTokenExpiryMs;
    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenService(RefreshTokenRepository repository,
        @Value("${jwt.refresh-token-expiry-ms}") long refreshTokenExpiryMs) {
        this.repository = repository;
        this.refreshTokenExpiryMs = refreshTokenExpiryMs;
    }

    public RefreshToken create(Long userId) {
        String tokenValue = generateSecureToken();
        Instant expiresAt = Instant.now().plusMillis(refreshTokenExpiryMs);
        RefreshToken refreshToken = new RefreshToken(tokenValue, userId, expiresAt);
        return repository.save(refreshToken);
    }

    // validate an incoming refresh token, delete it (rotation), return the userId
    public Long validateAndConsume(String tokenValue) {
        RefreshToken stored = repository.findByToken(tokenValue)
            .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token not found"));

        if (stored.isExpired()) {
            repository.deleteByToken(tokenValue);
            throw new InvalidRefreshTokenException("Refresh token expired");
        }

        Long userId = stored.getUserId();
        repository.deleteByToken(tokenValue);   // rotation: consume it
        return userId;
    }

    public void revoke(String tokenValue) {
        repository.deleteByToken(tokenValue);
    }

    private String generateSecureToken() {
        byte[] bytes = new byte[64];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

}
