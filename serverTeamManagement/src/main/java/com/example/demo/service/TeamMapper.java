package com.example.demo.service;

import com.example.demo.dto.TeamDTO;
import com.example.demo.model.Team;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {UsersMapper.class, TeamMapperHelper.class})
public interface TeamMapper {

    @Mapping(target = "leaderId", source = "leader.id")
    @Mapping(target = "leaderName", source = "leader.name")
    @Mapping(target = "leaderEmail", source = "leader.email")
    @Mapping(target = "leaderImage", source = "leader.imagePath")
    @Mapping(target = "members", source = ".", qualifiedByName = "mapMembers")
    TeamDTO toDTO(Team team);
}
