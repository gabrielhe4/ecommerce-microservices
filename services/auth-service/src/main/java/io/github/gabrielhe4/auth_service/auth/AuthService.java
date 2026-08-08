package io.github.gabrielhe4.auth_service.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.github.gabrielhe4.auth_service.auth.dto.AuthResponse;
import io.github.gabrielhe4.auth_service.auth.dto.LoginRequest;
import io.github.gabrielhe4.auth_service.auth.dto.RegisterRequest;
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

    public AuthResponse register(RegisterRequest request) { 
        if (userRepository.existsByUsername(request.username()))
            throw new IllegalArgumentException("Username already exists");
        
        User user = new User(
           request.username(),
           passwordEncoder.encode(request.password()),  // hash before storing
           Role.USER                                    // new registrations are USERs
        );

        userRepository.save(user);

        return new AuthResponse(jwtService.generateAccessToken(user));
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
            .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(),  user.getPassword()))
            throw new IllegalArgumentException("Invalid credentials");

        return new AuthResponse(jwtService.generateAccessToken(user));
    
    }
}
