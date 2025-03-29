package com.carrental.dto;

import com.carrental.entity.enums.CarStatus;
import com.carrental.entity.enums.TransmissionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public @Data  class CarDTO {
    private Long id;
    private String brand;
    private String model;
    private int year;
    private String color;
    private double pricePerDay;
    private String carType;
    private TransmissionType transmission;
    private int mileage;
    private CarStatus status;
    private String description;
}
