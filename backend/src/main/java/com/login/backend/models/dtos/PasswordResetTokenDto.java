package com.login.backend.models.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PasswordResetTokenDto {
    private String token;
    private LocalDateTime expirationDate;
    private Long userId;
}
