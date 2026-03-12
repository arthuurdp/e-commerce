package com.arthuurdp.e_commerce.modules.auth.mapper;

import com.arthuurdp.e_commerce.modules.auth.dtos.RegisterResponse;
import com.arthuurdp.e_commerce.modules.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthMapper {
    RegisterResponse toRegisterResponse(User user);
}
