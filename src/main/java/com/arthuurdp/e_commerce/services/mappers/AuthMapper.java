package com.arthuurdp.e_commerce.services.mappers;

import com.arthuurdp.e_commerce.domain.dtos.auth.RegisterResponse;
import com.arthuurdp.e_commerce.domain.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthMapper {
    RegisterResponse toRegisterResponse(User user);
}
