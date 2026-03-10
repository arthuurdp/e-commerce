package com.arthuurdp.e_commerce.domain.dtos.carrier;

import com.arthuurdp.e_commerce.domain.enums.Region;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCarrierRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "CNPJ is required")
        String cnpj,

        @NotBlank(message = "E-mail is required")
        @Email(message = "Please enter a valid e-mail")
        String email,

        @NotBlank(message = "Phone is required")
        String phone,

        @NotNull(message = "Region is required")
        Region region
) {}