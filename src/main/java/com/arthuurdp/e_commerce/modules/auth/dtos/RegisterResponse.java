package com.arthuurdp.e_commerce.modules.auth.dtos;

public record RegisterResponse(
        Long id,
        String firstName,
        String lastName,
        String email
) {}

