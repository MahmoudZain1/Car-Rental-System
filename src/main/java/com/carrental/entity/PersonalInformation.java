package com.carrental.entity;

import com.carrental.annotation.Age;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;


import java.time.LocalDate;
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name ="personal_information")
@Entity
@DynamicUpdate
public @Data class PersonalInformation {
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "Name should contain only letters")
    @Column(name = "first_name")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "Name should contain only letters")
    @Column(name = "last_name")
    private String lastName;

    @NotBlank(message = "Gender is required")
    @Column(name = "gender")
    private String gender;

    @NotNull(message = "Date of Birth is required")
    @Age
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(name = "email")
    private String email;

    @NotNull(message = "Phone number is required")
    @Pattern(regexp = "\\d{10,15}", message = "Phone number must contain 10-15 digits")
    @Column(name = "phone")
    private String phone;

    @NotBlank(message = "Country is required")
    @Column(name = "country")
    private String country;

    @NotBlank(message = "City is required")
    @Column(name = "city")
    private String city;

    @NotBlank(message = "Postal code is required")
    @Pattern(regexp = "^[a-zA-Z0-9]{5}", message = "Only five Characters/Numbers are allowed")
    @Column(name = "postal")
    private String postal;


}

