package com.login.backend.models.mappers;

import com.login.backend.models.dtos.PasswordResetTokenDto;
import com.login.backend.models.entities.PasswordResetToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PasswordResetTokenMapper {
    PasswordResetTokenMapper INSTANCE = Mappers.getMapper(PasswordResetTokenMapper.class);

    @Mapping(source = "user.id", target = "userId")
    PasswordResetTokenDto toDto(PasswordResetToken passwordResetToken);
}
