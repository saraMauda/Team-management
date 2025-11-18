package com.example.demo.controller;

import com.example.demo.dto.MeetingDTO;
import com.example.demo.model.EmployeeInProject;
import com.example.demo.model.Meeting;
import com.example.demo.model.Project;
import com.example.demo.model.Users;
import com.example.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/meetings")
public class MeetingController {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EmployeeInProjectRepository employeeInProjectRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private MeetingMapper meetingMapper;

    // =====================================
    // 1. עובד רגיל – רואה meetings של עצמו
    // =====================================
    @GetMapping("/my")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('TEAMLEADER')")
    public List<MeetingDTO> getMyMeetings(Authentication auth) {

        Users user = usersRepository.findByEmail(auth.getName());

        List<EmployeeInProject> links =
                employeeInProjectRepository.findByUser_Id(user.getId());

        List<Long> projectIds = links.stream()
                .map(ep -> ep.getProject().getProjectId())
                .toList();

        List<Meeting> meetings =
                meetingRepository.findByProject_ProjectIdIn(projectIds);

        return meetings.stream().map(meetingMapper::toDTO).toList();
    }

    // ==========================================
    // 2. ראש צוות – רואה פגישות של פרויקט שלו
    // ==========================================
    @GetMapping("/team/{projectId}")
    @PreAuthorize("hasRole('TEAMLEADER')")
    public List<MeetingDTO> getTeamMeetings(@PathVariable Long projectId,
                                            Authentication auth) {

        Users leader = usersRepository.findByEmail(auth.getName());

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // בדיקה נכונה — מנהל הפרויקט
        if (project.getProjectLeader() == null ||
                !project.getProjectLeader().getId().equals(leader.getId())) {

            throw new RuntimeException("You are not the team leader of this project");
        }

        List<Meeting> meetings =
                meetingRepository.findByProject_ProjectId(projectId);

        return meetings.stream().map(meetingMapper::toDTO).toList();
    }

    // ============================
    // 3. יצירת פגישת צוות — מנהל בלבד
    // ============================
    @PostMapping("/create")
    @PreAuthorize("hasRole('TEAMLEADER')")
    public MeetingDTO createMeeting(@RequestBody MeetingDTO dto,
                                    Authentication auth) {

        Users leader = usersRepository.findByEmail(auth.getName());

        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // בדיקה נכונה — מנהל הפרויקט
        if (project.getProjectLeader() == null ||
                !project.getProjectLeader().getId().equals(leader.getId())) {

            throw new RuntimeException("You are not the team leader of this project");
        }

        Meeting meeting = meetingMapper.toEntity(dto);
        meeting.setCreatedAt(LocalDateTime.now());
        meeting.setProject(project);

        meetingRepository.save(meeting);

        return meetingMapper.toDTO(meeting);
    }

    // ============================
    // 4. אדמין רואה הכול
    // ============================
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<MeetingDTO> getAll() {
        return meetingRepository.findAll()
                .stream().map(meetingMapper::toDTO).toList();
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<MeetingDTO> getByProject(@PathVariable Long projectId) {
        return meetingRepository.findByProject_ProjectId(projectId)
                .stream().map(meetingMapper::toDTO).toList();
    }
}
