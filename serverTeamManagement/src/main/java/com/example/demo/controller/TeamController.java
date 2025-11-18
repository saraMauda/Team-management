package com.example.demo.controller;

import com.example.demo.dto.TeamDTO;
import com.example.demo.dto.UsersDTO;
import com.example.demo.model.Team;
import com.example.demo.model.TeamMember;
import com.example.demo.model.Users;
import com.example.demo.service.TeamMapper;
import com.example.demo.service.TeamMemberRepository;
import com.example.demo.service.TeamRepository;
import com.example.demo.service.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    // 🔹 יצירת צוות חדש
    @PostMapping("/create/{leaderId}")
    public ResponseEntity<TeamDTO> createTeam(
            @PathVariable Long leaderId,
            @RequestBody List<Long> memberIds) {

        Users leader = usersRepository.findById(leaderId)
                .orElseThrow(() -> new RuntimeException("Leader not found"));

        // יצירת צוות
        Team team = new Team();
        team.setLeader(leader);
        team = teamRepository.save(team);

        // הוספת עובדים לצוות
        for (Long id : memberIds) {
            Users user = usersRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            TeamMember tm = new TeamMember();
            tm.setTeam(team);
            tm.setUser(user);
            teamMemberRepository.save(tm);
        }

        // מחזירים DTO
        List<TeamMember> members = teamMemberRepository.findByTeamId(team.getId());
        return new ResponseEntity<>(teamMapper.toDTO(team, members), HttpStatus.CREATED);
    }
    @GetMapping("/byLeader/{leaderId}")
    public List<TeamDTO> getTeamsByLeader(@PathVariable Long leaderId) {
        Users leader = usersRepository.findById(leaderId)
                .orElseThrow(() -> new RuntimeException("Leader not found"));

        List<Team> teams = teamRepository.findByLeaderId(leaderId);

        return teams.stream()
                .map(team -> teamMapper.toDTO(team))  // ✔ תיקון
                .collect(Collectors.toList());
    }

    // 🔹 החזרת כל הצוותים
    @GetMapping("/all")
    public List<TeamDTO> getAllTeams() {
        List<TeamDTO> dtos = new ArrayList<>();

        for (Team t : teamRepository.findAll()) {
            List<TeamMember> members = teamMemberRepository.findByTeamId(t.getId());
            dtos.add(teamMapper.toDTO(t, members));
        }

        return dtos;
    }

    // 🔹 החזרת צוות לפי ID
    @GetMapping("/{id}")
    public ResponseEntity<TeamDTO> getTeam(@PathVariable Long id) {
        return teamRepository.findById(id)
                .map(team -> {
                    List<TeamMember> members = teamMemberRepository.findByTeamId(id);
                    return ResponseEntity.ok(teamMapper.toDTO(team, members));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 הוספת עובד לצוות
    @PostMapping("/{teamId}/add/{userId}")
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

        List<TeamMember> members = teamMemberRepository.findByTeamId(teamId);
        return ResponseEntity.ok(teamMapper.toDTO(team, members));
    }

    // 🔹 מחיקת עובד מהצוות
    @DeleteMapping("/remove/{memberId}")
    public ResponseEntity<TeamDTO> removeMember(@PathVariable Long memberId) {

        TeamMember member = teamMemberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Team member not found"));

        Long teamId = member.getTeam().getId();

        teamMemberRepository.delete(member);

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        List<TeamMember> members = teamMemberRepository.findByTeamId(teamId);

        return ResponseEntity.ok(teamMapper.toDTO(team, members));
    }

}
