package com.login.backend.config;

import com.login.backend.models.entities.Role;
import com.login.backend.models.entities.User;
import com.login.backend.models.repositories.RoleRepository;
import com.login.backend.models.repositories.UserRepository;
import lombok.Data;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@Data
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Verificar si ya existen roles en la base de datos
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado"));

        // Verificar Administrador
        Optional<User> adminUser = userRepository.findByUsername("admin");
        if (adminUser.isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@google.com");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setEnabled(true);
            admin.setRoles(Set.of(adminRole));
            userRepository.save(admin);
            System.out.println("Usuario admin creado con exito");
        } else {
            // Actualizar datos si ya existe
            User existingAdmin = adminUser.get();
            existingAdmin.setEmail("admin@google.com");
            existingAdmin.setPassword(passwordEncoder.encode("admin"));
            userRepository.save(existingAdmin);
            System.out.println("Usuario admin actualizado con exito");
        }
    }
}
