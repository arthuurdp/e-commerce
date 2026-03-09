package com.arthuurdp.e_commerce.domain.dtos.carrier;

import com.arthuurdp.e_commerce.domain.enums.Region;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCarrierRequest(
        @NotBlank String name,
        @NotBlank String cnpj,
        @NotBlank @Email String email,
        @NotBlank String phone,
        @NotNull Region region
) {}