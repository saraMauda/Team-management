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

        // ✅ הוספת role
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            String rolesAsString = user.getRoles()
                    .stream()
                    .map(r -> r.getName().name())  // ממיר את enum ל־String
                    .reduce((r1, r2) -> r1 + ", " + r2)
                    .orElse(null);
            usersDTO.setRole(rolesAsString);
        }

        if (user.getImagePath() != null) {
            try {
                String base64 = ImageUtils.getImage(user.getImagePath());
                usersDTO.setImage("data:image/png;base64," + base64);
            } catch (IOException e) {
                e.printStackTrace();
                usersDTO.setImage(null);
            }
        } else {
            usersDTO.setImage(null);
        }

        return usersDTO;
    }


}
