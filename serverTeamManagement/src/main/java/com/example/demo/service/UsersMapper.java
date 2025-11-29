package com.example.demo.service;

import com.example.demo.dto.UsersDTO;
import com.example.demo.model.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.io.IOException;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UsersMapper {

    List<UsersDTO> usersToUsersDTO(List<Users> users);

    Users usersDTOToUser(UsersDTO dto);

    @Mapping(
            target = "role",
            expression = "java(user.getRoles() != null ? " +
                    "user.getRoles().stream().map(r -> r.getName().name()).reduce((a,b) -> a + \", \" + b).orElse(null) : null)"
    )
    @Mapping(
            target = "image",
            expression = "java(com.example.demo.service.UsersMapper.safeLoadImage(user.getImagePath()))"
    )
    UsersDTO userToUsersDTO(Users user);

    // ⭐ פונקציה בטוחה — MapStruct יקרא אותה, לא זורקת בכלל
    static String safeLoadImage(String path) {
        if (path == null) {
            return null;
        }
        try {
            return "data:image/png;base64," + ImageUtils.getImage(path);
        } catch (IOException e) {
            return null;
        }
    }
}
