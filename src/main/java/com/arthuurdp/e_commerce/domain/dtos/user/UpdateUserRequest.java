package com.arthuurdp.e_commerce.domain.dtos.user;

import com.arthuurdp.e_commerce.domain.enums.Gender;
import com.arthuurdp.e_commerce.infrastructure.security.annotations.ValidPhone;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UpdateUserRequest(
        @Size(max = 50, message = "First name must have at most 50 chars")
        String firstName,

        @Size(max = 50, message = "Last name must have at most 50 chars")
        String lastName,

        @ValidPhone(message = "Please enter a valid phone")
        String phone,

        @Past(message = "Please insert a valid date")
        LocalDate birthDate,

        Gender gender
) {}
