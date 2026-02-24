package com.arthuurdp.e_commerce.entities.dtos.email;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ChangeEmailRequest(
        @NotBlank(message = "E-mail is required")
        @Email(message = "Please enter a valid e-mail")
        String newEmail
) {
}
