package com.example.demo.controller;

import com.example.demo.dto.ProjectDTO;
import com.example.demo.model.Project;
import com.example.demo.model.Users;
import com.example.demo.model.EmployeeInProject;
import com.example.demo.model.Meeting;
import com.example.demo.model.Report;
import com.example.demo.service.ProjectRepository;
import com.example.demo.service.UsersRepository;
import com.example.demo.service.EmployeeInProjectRepository;
import com.example.demo.service.ProjectMapper;
import com.example.demo.service.MeetingRepository;
import com.example.demo.service.ReportRepository;
import javax.validation.Valid;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private EmployeeInProjectRepository employeeInProjectRepository;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private ReportRepository reportRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEAMLEADER')")
    public List<ProjectDTO> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(projectMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEAMLEADER','EMPLOYEE')")
    public ProjectDTO getProject(@PathVariable Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        return projectMapper.toDTO(project);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ProjectDTO createProject(@Valid @RequestBody ProjectDTO dto) {

        Project project = new Project();
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setStatus(dto.getStatus());
        project.setProgressPercentage(dto.getProgressPercentage() != null ? dto.getProgressPercentage() : 0);

        if (dto.getLeaderId() != null) {
            Users leader = usersRepository.findById(dto.getLeaderId())
                    .orElseThrow(() -> new RuntimeException("Leader not found"));
            project.setLeader(leader);
        }

        Project savedProject = projectRepository.save(project);

        if (dto.getEmployeeIds() != null) {
            for (Long empId : dto.getEmployeeIds()) {
                Users user = usersRepository.findById(empId)
                        .orElseThrow(() -> new RuntimeException("Employee not found"));

                EmployeeInProject link = new EmployeeInProject();
                link.setProject(savedProject);
                link.setUser(user);
                link.setAssignedDate(LocalDate.now());
                link.setStatus("ACTIVE");

                employeeInProjectRepository.save(link);
            }
        }

        return projectMapper.toDTO(savedProject);
    }

    @Transactional
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProjectDTO updateProject(@PathVariable Long id, @Valid @RequestBody ProjectDTO dto) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setStatus(dto.getStatus());
        project.setProgressPercentage(dto.getProgressPercentage() != null ? dto.getProgressPercentage() : 0);

        Users leader = usersRepository.findById(dto.getLeaderId())
                .orElseThrow(() -> new RuntimeException("Leader not found"));
        project.setLeader(leader);

        projectRepository.save(project);

        List<Long> newEmpIds = dto.getEmployeeIds() != null ? dto.getEmployeeIds() : List.of();

        List<EmployeeInProject> oldLinks = employeeInProjectRepository.findByProject_ProjectId(id);

        for (EmployeeInProject eip : oldLinks) {
            eip.setStatus("INACTIVE");
            employeeInProjectRepository.save(eip);
        }

        for (Long uid : newEmpIds) {

            EmployeeInProject existing =
                    oldLinks.stream()
                            .filter(e -> e.getUser().getId().equals(uid))
                            .findFirst()
                            .orElse(null);

            if (existing != null) {
                existing.setStatus("ACTIVE");
                employeeInProjectRepository.save(existing);
            } else {
                Users user = usersRepository.findById(uid)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                EmployeeInProject newEip = new EmployeeInProject();
                newEip.setProject(project);
                newEip.setUser(user);
                newEip.setAssignedDate(LocalDate.now());
                newEip.setStatus("ACTIVE");

                employeeInProjectRepository.save(newEip);
            }
        }

        return projectMapper.toDTO(project);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProject(@PathVariable Long id) {

        List<Meeting> meetings = meetingRepository.findByProject_ProjectId(id);
        meetingRepository.deleteAll(meetings);

        List<Report> reports =
                reportRepository.findByReportEmployeeInProject_Project_ProjectId(id);
        reportRepository.deleteAll(reports);

        List<EmployeeInProject> employees =
                employeeInProjectRepository.findByProject_ProjectId(id);

        employeeInProjectRepository.deleteAll(employees);

        projectRepository.deleteById(id);
    }

    @GetMapping("/byEmployee")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public List<ProjectDTO> getProjectsForLoggedEmployee(Authentication authentication) {

        String email = authentication.getName();

        return projectRepository.findByEmployeeProjects_User_Email(email)
                .stream()
                .map(projectMapper::toDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/{projectId}/addEmployee/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','TEAMLEADER')")
    public String addEmployeeToProject(@PathVariable Long projectId, @PathVariable Long userId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean exists =
                employeeInProjectRepository.existsByUser_IdAndProject_ProjectId(userId, projectId);

        if (exists) return "Employee already assigned";

        EmployeeInProject link = new EmployeeInProject();
        link.setProject(project);
        link.setUser(user);
        link.setAssignedDate(LocalDate.now());
        link.setStatus("ACTIVE");

        employeeInProjectRepository.save(link);

        return "Employee added successfully";
    }

    @GetMapping("/byLeader/{leaderId}")
    @PreAuthorize("hasRole('TEAMLEADER')")
    public List<ProjectDTO> getProjectsByLeader(@PathVariable Long leaderId) {

        return projectRepository.findByLeader_Id(leaderId)
                .stream()
                .map(projectMapper::toDTO)
                .toList();
    }
}
