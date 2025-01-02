package com.login.backend.services.refresh;

import com.login.backend.models.entities.RefreshToken;
import com.login.backend.models.repositories.RefreshTokenRepository;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Data
public class RefreshTokenServiceImp implements IRefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public void deleteByUserId(Long userId) {
        refreshTokenRepository.deleteAll(refreshTokenRepository.findAll().stream()
                .filter(refreshToken -> refreshToken.getUser().getId().equals(userId))
                .toList());
    }
}
