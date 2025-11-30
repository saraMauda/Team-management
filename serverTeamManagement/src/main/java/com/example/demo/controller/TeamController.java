package com.example.demo.controller;

import com.example.demo.dto.TeamDTO;
import com.example.demo.model.Team;
import com.example.demo.model.TeamMember;
import com.example.demo.model.Users;
import com.example.demo.service.TeamMapper;
import com.example.demo.service.TeamMemberRepository;
import com.example.demo.service.TeamRepository;
import com.example.demo.service.UsersRepository;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private TeamMapper teamMapper;

    @PostMapping("/create/{leaderId}")
    @PreAuthorize("hasAnyRole('TEAMLEADER','ADMIN')")
    public ResponseEntity<TeamDTO> createTeam(
            @PathVariable Long leaderId,
            @RequestBody List<Long> memberIds) {

        Users leader = usersRepository.findById(leaderId)
                .orElseThrow(() -> new RuntimeException("Leader not found"));

        Team team = new Team();
        team.setLeader(leader);
        team = teamRepository.save(team);

        for (Long id : memberIds) {
            Users user = usersRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            TeamMember tm = new TeamMember();
            tm.setTeam(team);
            tm.setUser(user);
            teamMemberRepository.save(tm);
        }

        return new ResponseEntity<>(teamMapper.toDTO(team), HttpStatus.CREATED);
    }

    @GetMapping("/byLeader/{leaderId}")
    @PreAuthorize("hasAnyRole('TEAMLEADER','ADMIN')")
    public List<TeamDTO> getTeamsByLeader(@PathVariable Long leaderId) {
        usersRepository.findById(leaderId)
                .orElseThrow(() -> new RuntimeException("Leader not found"));

        List<Team> teams = teamRepository.findByLeaderId(leaderId);

        return teams.stream()
                .map(teamMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN','TEAMLEADER')")
    public List<TeamDTO> getAllTeams() {
        List<TeamDTO> dtos = new ArrayList<>();

        for (Team t : teamRepository.findAll()) {
            dtos.add(teamMapper.toDTO(t));
        }

        return dtos;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEAMLEADER')")
    public ResponseEntity<TeamDTO> getTeam(@PathVariable Long id) {
        return teamRepository.findById(id)
                .map(team -> ResponseEntity.ok(teamMapper.toDTO(team)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{teamId}/add/{userId}")
    @PreAuthorize("hasAnyRole('TEAMLEADER','ADMIN')")
    public ResponseEntity<TeamDTO> addMember(
            @PathVariable Long teamId,
            @PathVariable Long userId) {

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TeamMember member = new TeamMember();
        member.setTeam(team);
        member.setUser(user);
        teamMemberRepository.save(member);

        return ResponseEntity.ok(teamMapper.toDTO(team));
    }

    @DeleteMapping("/remove/{memberId}")
    @PreAuthorize("hasAnyRole('TEAMLEADER','ADMIN')")
    public ResponseEntity<TeamDTO> removeMember(@PathVariable Long memberId) {

        TeamMember member = teamMemberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Team member not found"));

        Long teamId = member.getTeam().getId();

        teamMemberRepository.delete(member);

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        return ResponseEntity.ok(teamMapper.toDTO(team));
    }
}
