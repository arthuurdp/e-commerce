package com.arthuurdp.e_commerce.domain.dtos.auth;

public record RegisterResponse(
        Long id,
        String firstName,
        String lastName,
        String email
) {}

