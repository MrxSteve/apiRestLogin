package com.login.backend.models.mappers;

import com.login.backend.models.dtos.RoleDto;
import com.login.backend.models.entities.Role;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RoleMapper {
    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    RoleDto toDto(Role role);
}
