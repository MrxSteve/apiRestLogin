package com.login.backend.services.verification;

import com.login.backend.models.entities.User;
import com.login.backend.models.entities.VerificationToken;

import java.util.Optional;

public interface IVerificationTokenService {
    VerificationToken save(VerificationToken verificationToken);

    Optional<VerificationToken> findByToken(String token);

    void createToken(User user, String token);

    boolean activateUser(String token);

    Optional<User> validateTokenAndGetUser(String token);

    void deleteToken(String token);
}
