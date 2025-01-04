package com.login.backend.controllers;

import com.login.backend.models.entities.User;
import com.login.backend.services.mail.EmailService;
import com.login.backend.services.user.IUserService;
import com.login.backend.services.verification.IVerificationTokenService;
import jakarta.mail.MessagingException;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/password")
@Data
public class PasswordResetController {
    private final IUserService userService;
    private final IVerificationTokenService verificationTokenService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    // Recuperar contraseña
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody String email) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Generar el token
        String token = UUID.randomUUID().toString();
        verificationTokenService.createToken(user, token);

        // Enlace de restablecimiento
        String resetLink = "http://localhost:8080/api/password/reset-password/" + token;

        try {
            // Enviar correo electrónico
            emailService.sendHtmlEmail(
                    user.getEmail(),
                    "Restablecer contraseña",
                    "src/main/resources/templates/reset-password.html",
                    resetLink
            );
        } catch (MessagingException | IOException e) {
            return ResponseEntity.internalServerError().body("Error al enviar el correo electrónico");
        }

        return ResponseEntity.ok("Correo electrónico enviado, revise tu bandeja de entrada");
    }

    // Restablecer contraseña
    @PostMapping("/reset-password/{token}")
    public ResponseEntity<String> resetPassword(@PathVariable String token , @RequestBody String newPassword) {
        User user = verificationTokenService.validateTokenAndGetUser(token)
                .orElseThrow(() -> new RuntimeException("Token no valido o expirado"));

        // Actualizar la contraseña
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.registerUser(user);

        // Eliminar el token
        verificationTokenService.deleteToken(token);

        return ResponseEntity.ok("Contraseña actualizada correctamente");
    }
}
