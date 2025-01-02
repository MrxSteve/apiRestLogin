package com.login.backend.models.mappers;

import com.login.backend.models.dtos.UserDto;
import com.login.backend.models.entities.Role;
import com.login.backend.models.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "roles", target = "roles")
    UserDto toDto(User user);

    // Metodo auxiliar para convertir Set<Role> a Set<String>
    default Set<String> mapRoles(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return Set.of(); // Si no hay roles, retorna un Set vacío
        }
        return roles.stream()
                .map(Role::getName) // Extrae el campo 'name' de cada Role
                .collect(Collectors.toSet());
    }
}
