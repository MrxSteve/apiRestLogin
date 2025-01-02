package com.login.backend.services.user;

import com.login.backend.models.dtos.UserDto;
import com.login.backend.models.entities.Role;
import com.login.backend.models.entities.User;
import com.login.backend.models.repositories.RoleRepository;
import com.login.backend.models.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements IUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(User user) {
        // Encriptar la contraseña antes de guardar
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Asignar un rol predeterminado si no viene en el usuario
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Role defaultRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new RuntimeException("Rol USER no encontrado"));
            user.setRoles(Set.of(defaultRole));
        }

        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> findAll() {
        // Devolver todos los usuarios
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        // Buscar usuario por ID
        return userRepository.findById(id);
    }

    @Override
    public User updateUser(Long id, UserDto userDto) {
        // Buscar el usuario existente
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizar los campos permitidos
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setEnabled(userDto.isEnabled());

        // Actualizar los roles si vienen en el DTO
        if (userDto.getRoles() != null) {
            Set<Role> roles = userDto.getRoles().stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + roleName)))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }

        return userRepository.save(user); // Guardar los cambios
    }

    @Override
    public void deleteUser(Long id) {
        // Verificar si el usuario existe
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        // Eliminar el usuario
        userRepository.deleteById(id);
    }
}
