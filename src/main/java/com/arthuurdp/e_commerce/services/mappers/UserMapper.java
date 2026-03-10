package com.arthuurdp.e_commerce.services.mappers;

import com.arthuurdp.e_commerce.domain.dtos.user.UserResponse;
import com.arthuurdp.e_commerce.domain.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse(User user);
}
