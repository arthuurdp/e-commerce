package com.arthuurdp.e_commerce.modules.user.mapper;

import com.arthuurdp.e_commerce.modules.user.dtos.UserResponse;
import com.arthuurdp.e_commerce.modules.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse(User user);
}
