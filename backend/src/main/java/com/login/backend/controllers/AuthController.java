package com.login.backend.controllers;

import com.login.backend.models.dtos.AuthRequest;
import com.login.backend.models.dtos.AuthResponse;
import com.login.backend.models.dtos.RefreshTokenRequest;
import com.login.backend.models.dtos.UserDto;
import com.login.backend.models.entities.RefreshToken;
import com.login.backend.models.entities.Role;
import com.login.backend.models.entities.User;
import com.login.backend.models.mappers.UserMapper;
import com.login.backend.services.mail.EmailService;
import com.login.backend.services.refresh.IRefreshTokenService;
import com.login.backend.services.user.IUserService;
import com.login.backend.services.verification.IVerificationTokenService;
import com.login.backend.utils.JwtUtils;
import jakarta.mail.MessagingException;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@Data
public class AuthController {

    private final IUserService userService;
    private final IVerificationTokenService verificationTokenService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final IRefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDto userDto) {
        if (userService.findByUsername(userDto.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("El nombre de usuario ya existe");
        }
        if (userService.findByEmail(userDto.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("El correo electrónico ya está en uso");
        }

        User user = UserMapper.INSTANCE.toEntity(userDto);
        user.setPassword(userDto.getPassword());
        user.setEnabled(false);
        userService.registerUser(user);

        String token = UUID.randomUUID().toString();
        verificationTokenService.createToken(user, token);

        String activationLink = "http://localhost:8080/api/auth/activate/" + token;
        try {
            emailService.sendHtmlEmail(
                    user.getEmail(),
                    "Activación de Cuenta",
                    "src/main/resources/templates/activation-email.html",
                    activationLink
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al enviar el correo de activación");
        }

        return ResponseEntity.ok("Usuario registrado correctamente. Revise su correo para activar su cuenta.");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        try {
            // Validar existencia del usuario
            User user = userService.findByUsername(authRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Autenticación
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            // Generar tokens
            String accessToken = jwtUtils.generateToken(authRequest.getUsername());
            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setToken(UUID.randomUUID().toString());
            refreshToken.setUser(user);
            refreshToken.setExpirationDate(LocalDateTime.now().plusDays(7));
            refreshTokenService.save(refreshToken);

            // Obtener roles del usuario
            Set<String> roles = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet());

            // Obtener la fecha y hora actuales para el correo
            String loginTime = java.time.LocalTime.now().toString();
            String loginDate = java.time.LocalDate.now().toString();

            // Enviar correo electrónico de bienvenida
            String subject = "Bienvenido " + user.getUsername();
            String templatePath = "src/main/resources/templates/welcome-email.html";
            emailService.sendHtmlEmail(user.getEmail(), subject, templatePath, user.getUsername(), loginTime, loginDate);

            // Respuesta con tokens y roles
            return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken.getToken(), user.getUsername(), roles));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new AuthResponse("Credenciales inválidas", null, null, null));
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshAccessToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        Optional<RefreshToken> optionalToken = refreshTokenService.findByToken(refreshTokenRequest.getRefreshToken());

        if (optionalToken.isEmpty() || optionalToken.get().getExpirationDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(new AuthResponse("Refresh Token inválido o expirado", null, null, null));
        }

        RefreshToken refreshToken = optionalToken.get();
        String newAccessToken = jwtUtils.generateToken(refreshToken.getUser().getUsername());

        return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshToken.getToken(), refreshToken.getUser().getUsername(), null));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        try {
            // Extraer el username del token de acceso
            String username = jwtUtils.extractUsername(token.replace("Bearer ", ""));
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Eliminar todos los tokens asociados al usuario
            refreshTokenService.deleteByUserId(user.getId());
            return ResponseEntity.ok("Sesión cerrada correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al cerrar sesión");
        }
    }

    @GetMapping("/activate/{token}")
    public ResponseEntity<String> activateAccount(@PathVariable String token) {
        boolean isActivated = verificationTokenService.activateUser(token);

        if (isActivated) {
            Optional<User> optionalUser = verificationTokenService.validateTokenAndGetUser(token);

            if (optionalUser.isPresent()) {
                User user = optionalUser.get();

                try {
                    String subject = "¡Tu cuenta ha sido activada!";
                    String templatePath = "src/main/resources/templates/account-activated.html";
                    emailService.sendAccountActivatedEmail(user.getEmail(), subject, templatePath, user.getUsername());
                } catch (IOException | MessagingException e) {
                    e.printStackTrace();
                    return ResponseEntity.internalServerError().body("Cuenta activada, pero hubo un error al enviar el correo.");
                }
            }

            return ResponseEntity.ok("Cuenta activada correctamente. Revisa tu correo para la confirmación.");
        } else {
            return ResponseEntity.badRequest().body("Token inválido o expirado.");
        }
    }
}
