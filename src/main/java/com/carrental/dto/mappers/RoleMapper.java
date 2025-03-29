package com.carrental.dto.mappers;

import com.carrental.entity.Role;
import com.carrental.dto.RoleDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleDTO toDTO(Role role);
    Role toEntity(RoleDTO roleDTO);
}