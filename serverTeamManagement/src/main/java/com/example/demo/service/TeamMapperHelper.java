package com.example.demo.service;

import com.example.demo.dto.UsersDTO;
import com.example.demo.model.Team;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TeamMapperHelper {

    @Named("mapMembers")
    public static List<UsersDTO> mapMembers(Team team) {

        if (team.getMembers() == null)
            return Collections.emptyList();

        return team.getMembers().stream().map(member -> {
            var user = member.getUser();
            var dto = new UsersDTO();
            dto.setId(user.getId());
            dto.setName(user.getName());
            dto.setEmail(user.getEmail());
            dto.setImage(user.getImagePath());
            return dto;
        }).collect(Collectors.toList());
    }
}
