package com.backend.devflow.mapper;

import com.backend.devflow.dtos.UserDto;
import com.backend.devflow.models.User;
import com.backend.devflow.request.UserCreateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "passwordHash", source = "password")
    @Mapping(target = "username", source = "username")
    User toUser(UserCreateRequest request);

    @Mapping(target = "id", source = "publicId")
    UserDto toUserDto(User user);
}
