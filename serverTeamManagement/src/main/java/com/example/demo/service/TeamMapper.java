package com.example.demo.service;

import com.example.demo.dto.TeamDTO;
import com.example.demo.dto.UsersDTO;
import com.example.demo.model.Team;
import com.example.demo.model.TeamMember;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TeamMapper {

    private final UsersMapper usersMapper;

    public TeamMapper(UsersMapper usersMapper) {
        this.usersMapper = usersMapper;
    }

    public TeamDTO toDTO(Team team, List<TeamMember> members) {
        TeamDTO dto = new TeamDTO();

        dto.setId(team.getId());
        dto.setLeaderId(team.getLeader().getId());
        dto.setLeaderName(team.getLeader().getName());
        dto.setLeaderEmail(team.getLeader().getEmail());
        dto.setLeaderImage(team.getLeader().getImagePath());

        // ❗ משתמש ב־UsersMapper — לא יוצרים UsersDTO לבד
        List<UsersDTO> memberList = members.stream()
                .map(tm -> usersMapper.userToUsersDTO(tm.getUser()))
                .collect(Collectors.toList());

        dto.setMembers(memberList);

        return dto;
    }
}
