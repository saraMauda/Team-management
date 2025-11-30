package com.example.demo.service;

import com.example.demo.dto.UsersDTO;
import com.example.demo.model.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsersMapper {

    @Mapping(target = "role", expression = "java(user.getRoleString())")
    @Mapping(target = "image", source = "imagePath")
    UsersDTO toDTO(Users user);
}
