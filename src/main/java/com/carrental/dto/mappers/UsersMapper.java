package com.carrental.dto.mappers;

import com.carrental.entity.Users;
import com.carrental.dto.UsersDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UsersMapper {
    UsersDTO toDTO(Users users);
    Users toEntity(UsersDTO usersDTO);
}