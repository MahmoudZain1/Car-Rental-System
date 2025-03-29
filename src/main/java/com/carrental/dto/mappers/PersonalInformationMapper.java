package com.carrental.dto.mappers;

import com.carrental.entity.PersonalInformation;
import com.carrental.dto.PersonalInformationDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PersonalInformationMapper {

    PersonalInformationDTO toDTO(PersonalInformation personalInformation);
    PersonalInformation toEntity(PersonalInformationDTO personalInformationDTO);
}