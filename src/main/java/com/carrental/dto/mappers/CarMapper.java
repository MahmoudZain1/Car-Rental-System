package com.carrental.dto.mappers;
import com.carrental.entity.Cars;
import com.carrental.dto.CarDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CarMapper {

    CarDTO toDTO(Cars car);

    Cars toEntity(CarDTO carDTO);

}
