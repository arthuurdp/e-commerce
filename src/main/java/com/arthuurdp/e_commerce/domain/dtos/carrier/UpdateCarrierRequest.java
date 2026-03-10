package com.arthuurdp.e_commerce.domain.dtos.carrier;

import com.arthuurdp.e_commerce.domain.enums.CarrierStatus;
import com.arthuurdp.e_commerce.domain.enums.Region;
import jakarta.validation.constraints.Email;

public record UpdateCarrierRequest(
        String name,

        @Email(message = "Please enter a valid e-mail")
        String email,

        String phone,
        Region region,
        CarrierStatus status
) {}
