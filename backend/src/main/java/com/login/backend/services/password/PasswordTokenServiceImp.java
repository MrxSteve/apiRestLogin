package com.login.backend.services.password;

import com.login.backend.models.entities.PasswordResetToken;
import com.login.backend.models.entities.User;
import com.login.backend.models.repositories.PasswordResetTokenRepository;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

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

    @Override
    public void createToken(User user, String token) {
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(token);
        passwordResetToken.setUser(user);
        passwordResetToken.setExpirationDate(LocalDateTime.now().plusMinutes(10)); // Expiración de 10 minutos
        save(passwordResetToken);
    }

    @Override
    public Optional<User> validateTokenAndGetUser(String token) {
        Optional<PasswordResetToken> optionalToken = findByToken(token);

        if (optionalToken.isEmpty()) {
            return Optional.empty(); // Token inválido
        }

        PasswordResetToken passwordResetToken = optionalToken.get();

        // Verificar si el token ha expirado
        if (passwordResetToken.getExpirationDate().isBefore(LocalDateTime.now())) {
            passwordResetTokenRepository.delete(passwordResetToken);
            return Optional.empty(); // Token expirado
        }

        // Devolver el usuario asociado
        return Optional.of(passwordResetToken.getUser());
    }

    @Override
    public void deleteToken(String token) {
        Optional<PasswordResetToken> optionalToken = passwordResetTokenRepository.findByToken(token);
        optionalToken.ifPresent(passwordResetTokenRepository::delete);
    }

}
