package com.carrental.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@AllArgsConstructor
@NoArgsConstructor
@Builder
public @Data class UsersDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String verifyCode;
    private Boolean isVerified;
    private LocalDateTime createdAt;
    private Set<RoleDTO> roles = new HashSet<>();
}