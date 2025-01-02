package com.login.backend.services.user;

import com.login.backend.models.dtos.UserDto;
import com.login.backend.models.entities.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    User registerUser(User user);
    List<User> findAll();
    Optional<User> findById(Long id);
    User updateUser(Long id, UserDto userDto);
    void deleteUser(Long id);

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}
