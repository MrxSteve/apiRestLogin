package com.login.backend.services.password;

import com.login.backend.models.entities.PasswordResetToken;
import com.login.backend.models.entities.User;

import java.util.Optional;

public interface IPasswordTokenService {
    PasswordResetToken save(PasswordResetToken passwordResetToken);
    Optional<PasswordResetToken> findByToken(String token);
    void createToken(User user, String token);
    void deleteToken(String token);
    Optional<User> validateTokenAndGetUser(String token);
}
