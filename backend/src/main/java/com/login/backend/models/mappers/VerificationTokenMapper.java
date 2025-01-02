package com.login.backend.models.mappers;

import com.login.backend.models.dtos.VerificationTokenDto;
import com.login.backend.models.entities.VerificationToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface VerificationTokenMapper {
    VerificationTokenMapper INSTANCE = Mappers.getMapper(VerificationTokenMapper.class);

    @Mapping(source = "user.id", target = "userId")
    VerificationTokenDto toDto(VerificationToken verificationToken);
}
