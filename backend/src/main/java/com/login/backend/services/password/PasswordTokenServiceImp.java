package com.login.backend.services.password;

import com.login.backend.models.entities.PasswordResetToken;
import com.login.backend.models.repositories.PasswordResetTokenRepository;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Data
public class PasswordTokenServiceImp implements IPasswordTokenService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Override
    public PasswordResetToken save(PasswordResetToken passwordResetToken) {
        return passwordResetTokenRepository.save(passwordResetToken);
    }

    @Override
    public Optional<PasswordResetToken> findByToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }
}
