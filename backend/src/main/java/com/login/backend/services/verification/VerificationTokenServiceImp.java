package com.login.backend.services.verification;

import com.login.backend.models.entities.VerificationToken;
import com.login.backend.models.repositories.VerificationTokenRepository;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Data
public class VerificationTokenServiceImp implements IVerificationTokenService {
    private final VerificationTokenRepository verificationTokenRepository;

    @Override
    public VerificationToken save(VerificationToken verificationToken) {
        return verificationTokenRepository.save(verificationToken);
    }

    @Override
    public Optional<VerificationToken> findByToken(String token) {
        return verificationTokenRepository.findByToken(token);
    }
}
