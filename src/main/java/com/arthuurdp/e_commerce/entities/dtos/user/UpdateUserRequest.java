package com.arthuurdp.e_commerce.entities.dtos.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Size(max = 50, message = "First name must have at most 50 chars")
        String firstName,

        @Size(max = 50, message = "Last name must have at most 50 chars")
        String lastName,

        @Email(message = "Please enter a valid e-mail")
        @Size(max = 100, message = "Email must have at most 100 chars")
        String email) {
}
