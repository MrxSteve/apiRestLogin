package com.login.backend.services.password;

import com.login.backend.models.entities.PasswordResetToken;

import java.util.Optional;

public interface IPasswordTokenService {
    PasswordResetToken save(PasswordResetToken passwordResetToken);
    Optional<PasswordResetToken> findByToken(String token);
}
