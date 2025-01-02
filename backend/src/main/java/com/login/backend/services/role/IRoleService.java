package com.login.backend.services.role;

import com.login.backend.models.entities.Role;

import java.util.Optional;

public interface IRoleService {
    Optional<Role> findByName(String name);
}
