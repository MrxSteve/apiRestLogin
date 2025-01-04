package com.login.backend.controllers;

import com.login.backend.models.dtos.AuthRequest;
import com.login.backend.models.dtos.AuthResponse;
import com.login.backend.models.dtos.UserDto;
import com.login.backend.models.entities.Role;
import com.login.backend.models.entities.User;
import com.login.backend.models.mappers.UserMapper;
import com.login.backend.services.mail.EmailService;
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

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDto userDto) {
        if (userService.findByUsername(userDto.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("El nombre de usuario ya existe");
        }
        if (userService.findByEmail(userDto.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("El correo electrónico ya está en uso");
        }

        // Crear usuario
        User user = UserMapper.INSTANCE.toEntity(userDto);
        user.setPassword(userDto.getPassword()); // Dejar la contraseña en texto plano aquí
        user.setEnabled(false); // Deshabilitar hasta activación
        userService.registerUser(user); // La codificación se maneja en el servicio

        // Generar token de activación y enviar correo
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
            User user = userService.findByUsername(authRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            System.out.println("¿Coincide el password?: " + passwordEncoder.matches(authRequest.getPassword(), user.getPassword()));

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            String token = jwtUtils.generateToken(authRequest.getUsername());
            Set<String> roles = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet());

            return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), roles));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new AuthResponse("Credenciales inválidas", null, null));
        }
    }

    // **3. Activación de Cuenta**
    @GetMapping("/activate/{token}")
    public ResponseEntity<String> activateAccount(@PathVariable String token) {
        boolean isActivated = verificationTokenService.activateUser(token);
        if (isActivated) {
            return ResponseEntity.ok("Cuenta activada correctamente.");
        } else {
            return ResponseEntity.badRequest().body("Token inválido o expirado.");
        }
    }

    // **4. Recuperación de Contraseña**
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody String email) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Correo no registrado"));

        String token = UUID.randomUUID().toString();
        verificationTokenService.createToken(user, token);

        String resetLink = "http://localhost:8080/api/auth/reset-password/" + token;
        try {
            emailService.sendHtmlEmail(
                    user.getEmail(),
                    "Recuperación de Contraseña",
                    "src/main/resources/templates/reset-password.html", // Plantilla HTML
                    resetLink
            );
        } catch (MessagingException | IOException e) {
            return ResponseEntity.internalServerError().body("Error al enviar el correo de recuperación");
        }

        return ResponseEntity.ok("Correo enviado correctamente. Revisa tu bandeja de entrada.");
    }

    // **5. Restablecer Contraseña**
    @PostMapping("/reset-password/{token}")
    public ResponseEntity<String> resetPassword(@PathVariable String token, @RequestBody String newPassword) {
        User user = verificationTokenService.validateTokenAndGetUser(token)
                .orElseThrow(() -> new RuntimeException("Token inválido o expirado"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userService.registerUser(user); // Actualizar la contraseña
        verificationTokenService.deleteToken(token); // Eliminar el token usado

        return ResponseEntity.ok("Contraseña restablecida correctamente.");
    }
}
