package com.arthuurdp.e_commerce.domain.dtos.user;

import com.arthuurdp.e_commerce.domain.enums.Gender;

import java.time.LocalDate;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String cpf,
        String phone,
        LocalDate birthDate,
        Gender gender
) {}
