package io.github.gabrielhe4.auth_service.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.github.gabrielhe4.auth_service.auth.dto.AuthResponse;
import io.github.gabrielhe4.auth_service.auth.dto.LoginRequest;
import io.github.gabrielhe4.auth_service.auth.dto.RefreshRequest;
import io.github.gabrielhe4.auth_service.auth.dto.RegisterRequest;
import io.github.gabrielhe4.auth_service.model.RefreshToken;
import io.github.gabrielhe4.auth_service.repository.UserRepository;
import io.github.gabrielhe4.auth_service.user.Role;
import io.github.gabrielhe4.auth_service.user.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username()))
            throw new IllegalArgumentException("Username already exists");

        User user = new User(
           request.username(),
           passwordEncoder.encode(request.password()),  // hash before storing
           Role.USER                                    // new registrations are USERs
        );

        userRepository.save(user);
        return buildTokens(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
            .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(),  user.getPassword()))
            throw new IllegalArgumentException("Invalid credentials");

        return buildTokens(user);

    }

    public AuthResponse refresh(RefreshRequest request) {
        Long userId = refreshTokenService.validateAndConsume(request.refreshToken());
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return buildTokens(user);   // fresh access token + fresh refresh token (rotation)
    }

    public void logout(RefreshRequest request) {
        refreshTokenService.revoke(request.refreshToken());
    }

    private AuthResponse buildTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.create(user.getId());
        return new AuthResponse(accessToken, refreshToken.getToken());
    }
}
