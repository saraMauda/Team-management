package com.example.demo.service;

import com.example.demo.dto.TeamDTO;
import com.example.demo.dto.UsersDTO;
import com.example.demo.model.Team;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UsersMapper.class})
public interface TeamMapper {

    @Mapping(target = "leaderId", source = "leader.id")
    @Mapping(target = "leaderName", source = "leader.name")
    @Mapping(target = "leaderEmail", source = "leader.email")
    @Mapping(target = "leaderImage", source = "leader.imagePath")
    @Mapping(target = "members", source = ".", qualifiedByName = "extractMembers")
    TeamDTO toDTO(Team team);

    @Named("extractMembers")
    default List<UsersDTO> extractMembers(Team team) {
        if (team.getMembers() == null) {
            return List.of();
        }

        return team.getMembers()
                .stream()
                .map(m -> {
                    var user = m.getUser();
                    var dto = new UsersDTO();
                    dto.setId(user.getId());
                    dto.setName(user.getName());
                    dto.setEmail(user.getEmail());
                    dto.setImage(user.getImagePath());
                    return dto;
                })
                .toList();
    }
}
