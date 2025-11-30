package com.example.demo.service;

import com.example.demo.dto.UsersDTO;
import com.example.demo.model.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsersMapper {

    @Mapping(target = "role", expression = "java(user.getRoleString())")
    @Mapping(target = "image",
            expression = "java(ImageUtils.wrapBase64(user.getImagePath()))")
    UsersDTO toDTO(Users user);
}
