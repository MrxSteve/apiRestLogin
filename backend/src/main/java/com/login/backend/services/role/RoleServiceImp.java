package com.login.backend.services.role;

import com.login.backend.models.entities.Role;
import com.login.backend.models.repositories.RoleRepository;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Data
public class RoleServiceImp implements IRoleService{
    private final RoleRepository roleRepository;

    @Override
    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }
}
