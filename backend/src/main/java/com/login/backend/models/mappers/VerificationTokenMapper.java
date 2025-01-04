package com.login.backend.models.mappers;

import com.login.backend.models.dtos.VerificationTokenDto;
import com.login.backend.models.entities.User;
import com.login.backend.models.entities.VerificationToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface VerificationTokenMapper {
    VerificationTokenMapper INSTANCE = Mappers.getMapper(VerificationTokenMapper.class);

    @Mapping(source = "user", target = "userId", qualifiedByName = "mapUserToUserId")
    VerificationTokenDto toDto(VerificationToken verificationToken);

    @Named("mapUserToUserId")
    default Long mapUserToUserId(User user) {
        return user != null ? user.getId() : null;
    }
}
