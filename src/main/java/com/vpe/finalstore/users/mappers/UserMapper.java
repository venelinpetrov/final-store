package com.vpe.finalstore.users.mappers;

import com.vpe.finalstore.users.dtos.UserCreateDto;
import com.vpe.finalstore.users.dtos.UserDto;
import com.vpe.finalstore.users.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source="customer.name", target = "name")
    UserDto toDto(User user);

    User toEntity(UserCreateDto user);
}
