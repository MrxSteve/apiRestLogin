package com.login.backend.services.verification;

import com.login.backend.models.entities.User;
import com.login.backend.models.entities.VerificationToken;
import com.login.backend.models.repositories.UserRepository;
import com.login.backend.models.repositories.VerificationTokenRepository;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Data
public class VerificationTokenServiceImp implements IVerificationTokenService {
    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;

    // Guardar el token
    @Override
    public VerificationToken save(VerificationToken verificationToken) {
        return verificationTokenRepository.save(verificationToken);
    }

    // Buscar un token por su valor
    @Override
    public Optional<VerificationToken> findByToken(String token) {
        return verificationTokenRepository.findByToken(token);
    }

    // Crear y guardar un token para un usuario
    @Override
    public void createToken(User user, String token) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpirationDate(LocalDateTime.now().plusMinutes(20)); // Token válido por 20 minutos
        save(verificationToken);
    }

    // Activar al usuario asociado al token
    @Override
    public boolean activateUser(String token) {
        Optional<VerificationToken> optionalToken = findByToken(token);

        if (optionalToken.isEmpty()) {
            return false; // Token inválido
        }

        VerificationToken verificationToken = optionalToken.get();

        // Verificar si el token ha expirado
        if (verificationToken.getExpirationDate().isBefore(LocalDateTime.now())) {
            return false; // Token expirado
        }

        // Activar al usuario
        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user); // Guarda el cambio en la base de datos
        verificationTokenRepository.delete(verificationToken); // Opcional: eliminar el token después de activación
        return true;
    }

    // Validar un token y devolver el usuario asociado
    @Override
    public Optional<User> validateTokenAndGetUser(String token) {
        // Buscar el token en la base de datos
        Optional<VerificationToken> optionalToken = findByToken(token);

        if (optionalToken.isEmpty()) {
            return Optional.empty(); // Token inválido
        }

        VerificationToken verificationToken = optionalToken.get();

        // Verificar si el token ha expirado
        if (verificationToken.getExpirationDate().isBefore(LocalDateTime.now())) {
            // Si el token ha expirado, puedes opcionalmente eliminarlo
            verificationTokenRepository.delete(verificationToken);
            return Optional.empty(); // Token expirado
        }

        // Devolver el usuario asociado al token
        return Optional.of(verificationToken.getUser());
    }

    // Eliminar un token (opcional)
    @Override
    public void deleteToken(String token) {
        verificationTokenRepository.findByToken(token).ifPresent(verificationTokenRepository::delete);
    }
}
