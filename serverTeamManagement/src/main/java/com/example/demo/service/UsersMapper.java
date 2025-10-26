package com.example.demo.service;

import com.example.demo.dto.UsersDTO;
import com.example.demo.model.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.io.IOException;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UsersMapper {

    List<UsersDTO> usersToUsersDTO(List<Users> users);
    Users usersDTOToUser(UsersDTO usersDTO);

    // השם שונה לקטן והוסר ה-throws
    default UsersDTO userToUsersDTO(Users user) {
        UsersDTO usersDTO = new UsersDTO();
        usersDTO.setId(user.getId());
        usersDTO.setActive(user.isActive());
        usersDTO.setName(user.getName());
        usersDTO.setEmail(user.getEmail());
        usersDTO.setRole(user.getRole());

        if (user.getImagePath() != null) {
            try {
                usersDTO.setImage(ImageUtils.getImage(user.getImagePath()));
            } catch (IOException e) {
                e.printStackTrace();
                usersDTO.setImage(null);
            }
        }
        return usersDTO;
    }
}
