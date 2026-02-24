package com.arthuurdp.e_commerce.entities.dtos.user;

import com.arthuurdp.e_commerce.entities.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateUserRequest(
        @Size(max = 50, message = "First name must have at most 50 chars")
        String firstName,

        @Size(max = 50, message = "Last name must have at most 50 chars")
        String lastName,

        @Email(message = "Please enter a valid e-mail")
        @Size(max = 100, message = "Email must have at most 100 chars")
        String email,

        @Pattern(regexp = "\\d{11}", message = "Please enter a valid cpf")
        String cpf,

        @Pattern(regexp = "\\d{10,11}", message = "Please enter a valid phone number")
        String phone,

        LocalDate birthDate,

        Gender gender) {
}
