package com.login.backend.controllers;

import com.login.backend.models.dtos.ForgotPasswordRequest;
import com.login.backend.models.dtos.ResetPasswordRequest;
import com.login.backend.models.entities.User;
import com.login.backend.services.mail.EmailService;
import com.login.backend.services.password.IPasswordTokenService;
import com.login.backend.services.user.IUserService;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/password")
@Data
public class PasswordResetController {
    private final IUserService userService;
    private final IPasswordTokenService passwordTokenService;
    private final EmailService emailService;

    // **1. Solicitar recuperación de contraseña**
    @PostMapping("/forgot")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        String email = request.getEmail().trim();
        Optional<User> optionalUser = userService.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.ok("Si el correo existe, revisa tu bandeja de entrada.");
        }

        User user = optionalUser.get();
        String token = UUID.randomUUID().toString();
        passwordTokenService.createToken(user, token);

        String resetLink = "http://localhost:8080/api/password/reset/" + token;
        try {
            emailService.sendHtmlEmail(
                    user.getEmail(),
                    "Recuperación de Contraseña",
                    "src/main/resources/templates/reset-password.html",
                    resetLink
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al enviar el correo de recuperación");
        }

        return ResponseEntity.ok("Si el correo existe, revisa tu bandeja de entrada.");
    }

    // **2. Restablecer contraseña**
    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        // Validar el token y obtener el usuario asociado
        User user = passwordTokenService.validateTokenAndGetUser(request.getToken())
                .orElseThrow(() -> new RuntimeException("Token inválido o expirado"));

        // Actualizar la contraseña
        userService.updatePassword(user, request.getNewPassword());

        // Eliminar el token usado
        passwordTokenService.deleteToken(request.getToken());

        // Enviar correo de confirmación de restablecimiento
        try {
            String subject = "Confirmación de Restablecimiento de Contraseña";
            String templatePath = "src/main/resources/templates/changed-password.html";
            emailService.sendHtmlEmail(
                    user.getEmail(),
                    subject,
                    templatePath,
                    user.getUsername()
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("La contraseña se restableció, pero hubo un error al enviar el correo de confirmación.");
        }

        return ResponseEntity.ok("Contraseña restablecida correctamente. Se ha enviado un correo de confirmación.");
    }
}
