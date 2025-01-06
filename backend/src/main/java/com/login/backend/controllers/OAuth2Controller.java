package com.login.backend.controllers;

import com.login.backend.models.dtos.AuthResponse;
import com.login.backend.models.entities.User;
import com.login.backend.services.mail.EmailService;
import com.login.backend.services.oauth.IOAuth2AuthenticationService;
import com.login.backend.services.user.IUserService;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/oauth2")
@Data
public class OAuth2Controller {
    private final IOAuth2AuthenticationService oAuth2AuthenticationService;
    private final IUserService userService;
    private final EmailService emailService;

    @GetMapping("/login")
    public ResponseEntity<AuthResponse> loginWithOAuth2(OAuth2AuthenticationToken authentication) {
        try {
            // Obtener información del usuario y proveedor
            OAuth2User oAuth2User = authentication.getPrincipal();
            String provider = authentication.getAuthorizedClientRegistrationId();

            // Autenticar con OAuth2
            AuthResponse response = oAuth2AuthenticationService.authenticateWithOAuth2(oAuth2User, provider);

            // Obtener detalles del usuario autenticado
            User user = userService.findByUsername(response.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Obtener la fecha y hora actuales para el correo
            String loginTime = java.time.LocalTime.now().toString();
            String loginDate = java.time.LocalDate.now().toString();

            // Enviar correo electrónico de bienvenida
            String subject = "Bienvenido " + user.getUsername();
            String templatePath = "src/main/resources/templates/welcome-email.html";
            emailService.sendHtmlEmail(user.getEmail(), subject, templatePath, user.getUsername(), loginTime, loginDate);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new AuthResponse("Error al autenticar con OAuth2", null, null, null));
        }
    }


    @GetMapping("/oauth2-login")
    public ResponseEntity<String> oauth2LoginPage() {
        return ResponseEntity.ok("Página de inicio de sesión con OAuth2 personalizada");
    }

    @GetMapping("/login-success")
    public ResponseEntity<String> loginSuccess() {
        return ResponseEntity.ok("Inicio de sesión con OAuth2 exitoso.");
    }

    @GetMapping("/login-failure")
    public ResponseEntity<String> loginFailure() {
        return ResponseEntity.status(401).body("Error al iniciar sesión con OAuth2.");
    }

}
