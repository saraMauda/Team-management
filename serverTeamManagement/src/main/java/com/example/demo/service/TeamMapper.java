package com.example.demo.service;

import com.example.demo.dto.TeamDTO;
import com.example.demo.model.Team;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { UsersMapper.class })
public interface TeamMapper {

    TeamDTO toDTO(Team team);

}
