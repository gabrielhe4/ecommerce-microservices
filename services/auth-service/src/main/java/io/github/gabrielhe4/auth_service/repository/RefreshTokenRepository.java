package io.github.gabrielhe4.auth_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import io.github.gabrielhe4.auth_service.model.RefreshToken;


public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Transactional
    void deleteByToken(String token);


    @Modifying
    @Transactional
    void deleteByUserId(Long userId);

}
