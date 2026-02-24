package com.arthuurdp.e_commerce.entities.dtos.auth;

import com.arthuurdp.e_commerce.entities.enums.Gender;
import com.arthuurdp.e_commerce.infrastructure.security.annotations.ValidCpf;
import com.arthuurdp.e_commerce.infrastructure.security.annotations.ValidPhone;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record RegisterRequest(
        @NotBlank(message = "First name is required")
        @Size(max = 50, message = "First name must have at most 50 chars")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 50, message = "Last name must have at most 50 chars")
        String lastName,

        @NotBlank(message = "E-mail is required")
        @Email(message = "Please enter a valid e-mail")
        @Size(max = 100, message = "Email must have at most 100 chars")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must have at least 6 chars")
        String password,

        @NotBlank(message = "CPF is required")
        @ValidCpf(message = "Please enter a valid CPF")
        String cpf,

        @NotBlank(message = "Phone is required")
        @ValidPhone(message = "Please enter a valid phone")
        String phone,

        @NotNull(message = "Birth date is required")
        @Past(message = "Birth date must be in the past")
        LocalDate birthDate,

        @NotNull(message = "Gender is required")
        Gender gender
) {}

