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


    // ---------------------------------------
    // 🔹 1. שליפת כל הפרויקטים
    // ---------------------------------------
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEAMLEADER')")
    public List<ProjectDTO> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(projectMapper::projectToProjectDTO)
                .collect(Collectors.toList());
    }

    // ---------------------------------------
    // 🔹 2. שליפה לפי מזהה
    // ---------------------------------------
    @GetMapping("/{id}")
    public ProjectDTO getProject(@PathVariable Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        return projectMapper.projectToProjectDTO(project);
    }

    // ---------------------------------------
    // 🔹 3. יצירת פרויקט חדש
    // ---------------------------------------
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ProjectDTO createProject(@RequestBody ProjectDTO dto) {

        Project project = new Project();
        project.setProjectName(dto.getName());
        project.setProjectDescription(dto.getDescription());
        project.setProjectStartDate(dto.getStartDate());
        project.setProjectEndDate(dto.getEndDate());
        project.setProjectStatus(dto.getStatus());
        project.setProgressPercentage(dto.getProgress() != null ? dto.getProgress() : 0);

        // מנהל פרויקט
        if (dto.getLeaderId() != null) {
            Users leader = usersRepository.findById(dto.getLeaderId())
                    .orElseThrow(() -> new RuntimeException("Leader not found"));
            project.setProjectLeader(leader);
        }

        Project savedProject = projectRepository.save(project);

        // שיוך עובדים לפרויקט
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

        return projectMapper.projectToProjectDTO(savedProject);
    }

    // ---------------------------------------
    // 🔹 4. עדכון פרויקט
    @Transactional
    @PutMapping("/{id}")
    public ProjectDTO updateProject(@PathVariable Long id, @RequestBody ProjectDTO dto) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // עדכון פרויקט
        project.setProjectName(dto.getName());
        project.setProjectDescription(dto.getDescription());
        project.setProjectStartDate(dto.getStartDate());
        project.setProjectEndDate(dto.getEndDate());
        project.setProjectStatus(dto.getStatus());
        project.setProgressPercentage(dto.getProgress());

        Users leader = usersRepository.findById(dto.getLeaderId())
                .orElseThrow(() -> new RuntimeException("Leader not found"));
        project.setProjectLeader(leader);

        projectRepository.save(project);

        List<Long> newEmpIds = dto.getEmployeeIds() != null ? dto.getEmployeeIds() : List.of();

        // 1️⃣ מסמנים כ-INACTIVE את כל הנוכחיים
        List<EmployeeInProject> oldLinks = employeeInProjectRepository.findByProject_ProjectId(id);

        for (EmployeeInProject eip : oldLinks) {
            eip.setStatus("INACTIVE");
            employeeInProjectRepository.save(eip);
        }

        // 2️⃣ מוסיפים מחדש את כל ה-active החדשים
        for (Long uid : newEmpIds) {

            // האם הוא קיים כבר ברשומה ישנה?
            EmployeeInProject existing =
                    oldLinks.stream()
                            .filter(e -> e.getUser().getId().equals(uid))
                            .findFirst()
                            .orElse(null);

            if (existing != null) {
                existing.setStatus("ACTIVE");
                employeeInProjectRepository.save(existing);
            } else {
                // חדש
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

        return projectMapper.projectToProjectDTO(project);
    }



    // ---------------------------------------
    // 🔹 5. מחיקת פרויקט (ללא Approvals)
    // ---------------------------------------
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProject(@PathVariable Long id) {

        // פגישות בפרויקט
        List<Meeting> meetings = meetingRepository.findByProject_ProjectId(id);

        // מחיקת הפגישות
        meetingRepository.deleteAll(meetings);

        // עובדים בפרויקט
        List<EmployeeInProject> employees =
                employeeInProjectRepository.findByProject_ProjectId(id);

        for (EmployeeInProject eip : employees) {

            // מחיקת הדוחות של העובד
            List<Report> reports =
                    reportRepository.findByReportEmployeeInProject_EmployeeProjectId(
                            eip.getEmployeeProjectId()
                    );
            reportRepository.deleteAll(reports);
        }

        // מחיקת הקשרים עובד-פרויקט
        employeeInProjectRepository.deleteAll(employees);

        // מחיקת הפרויקט
        projectRepository.deleteById(id);
    }

    // ---------------------------------------
    // 🔹 7. פרויקטים של עובד מחובר
    // ---------------------------------------
    @GetMapping("/byEmployee")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public List<ProjectDTO> getProjectsForLoggedEmployee(Authentication authentication) {

        String email = authentication.getName();

        return projectRepository.findByProjectEmployeeProjects_User_Email(email)
                .stream()
                .map(projectMapper::projectToProjectDTO)
                .collect(Collectors.toList());
    }

    // ---------------------------------------
    // 🔹 8. הוספת עובד לפרויקט קיים
    // ---------------------------------------
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

        return projectRepository.findByProjectLeader_Id(leaderId)
                .stream()
                .map(projectMapper::projectToProjectDTO)
                .toList();
    }

}
