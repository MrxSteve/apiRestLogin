package com.login.backend.services.refresh;

import com.login.backend.models.entities.RefreshToken;

import java.util.Optional;

public interface IRefreshTokenService {
    RefreshToken save(RefreshToken refreshToken);
    Optional<RefreshToken> findByToken(String token);
    void deleteByUserId(Long userId);
}
