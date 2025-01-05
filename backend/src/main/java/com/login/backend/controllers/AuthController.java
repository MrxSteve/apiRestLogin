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
        user.setPassword(userDto.getPassword());
        user.setEnabled(false);
        userService.registerUser(user);

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

            // Obtener la fecha y hora actuales
            String loginTime = java.time.LocalTime.now().toString();
            String loginDate = java.time.LocalDate.now().toString();

            // Enviar correo electrónico de bienvenida
            String subject = "Bienvenido " + user.getUsername();
            String templatePath = "src/main/resources/templates/welcome-email.html";
            emailService.sendHtmlEmail(user.getEmail(), subject, templatePath, user.getUsername(), loginTime, loginDate);

            return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), roles));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new AuthResponse("Credenciales inválidas", null, null));
        }
    }

    @GetMapping("/activate/{token}")
    public ResponseEntity<String> activateAccount(@PathVariable String token) {
        boolean isActivated = verificationTokenService.activateUser(token);

        if (isActivated) {
            // Obtener el usuario asociado al token
            Optional<User> optionalUser = verificationTokenService.validateTokenAndGetUser(token);

            if (optionalUser.isPresent()) {
                User user = optionalUser.get();

                // Enviar correo de confirmación de activación
                try {
                    String subject = "¡Tu cuenta ha sido activada!";
                    String templatePath = "src/main/resources/templates/account-activated.html";
                    emailService.sendAccountActivatedEmail(user.getEmail(), subject, templatePath, user.getUsername());
                } catch (IOException e) {
                    e.printStackTrace();
                    return ResponseEntity.internalServerError().body("Cuenta activada, pero hubo un error al leer la plantilla del correo.");
                } catch (MessagingException e) {
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
