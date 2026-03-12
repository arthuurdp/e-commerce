package com.arthuurdp.e_commerce.modules.user.dtos;

import com.arthuurdp.e_commerce.modules.user.enums.Gender;

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
