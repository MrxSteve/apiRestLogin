package com.login.backend.models.dtos;

import lombok.Data;

import java.util.Set;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private boolean enabled;
    private Set<String> roles;
}
