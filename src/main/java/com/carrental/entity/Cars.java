package com.carrental.entity;

import com.carrental.entity.enums.CarStatus;
import com.carrental.entity.enums.TransmissionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "cars")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicUpdate
@ToString
public @Data  class Cars {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String brand;

    @NotNull
    @Column(nullable = false)
    private String model;

    @NotNull
    @Column(nullable = false)
    @Min(1900) @Max(2030)
    private int year;

    @NotNull
    @Column(nullable = false)
    private String color;

    @NotNull
    @Column(nullable = false)
    private double pricePerDay;

    @NotNull
    @Column(nullable = false)
    private String carType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransmissionType transmission;

    @NotNull
    @Column(nullable = false)
    @Min(0)
    private int mileage;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarStatus status = CarStatus.AVAILABLE;

    @Column(name = "image", columnDefinition = "BYTEA")
    @Lob
    private byte[] image;


    @Column(columnDefinition = "TEXT")
    private String description;
}