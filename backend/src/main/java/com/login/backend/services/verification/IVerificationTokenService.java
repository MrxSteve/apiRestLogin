package com.login.backend.services.verification;

import com.login.backend.models.entities.VerificationToken;

import java.util.Optional;

public interface IVerificationTokenService {
    VerificationToken save(VerificationToken verificationToken);
    Optional<VerificationToken> findByToken(String token);
}
