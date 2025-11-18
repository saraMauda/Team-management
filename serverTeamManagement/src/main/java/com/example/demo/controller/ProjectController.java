package com.example.demo.controller;

import com.example.demo.dto.ProjectDTO;
import com.example.demo.model.Project;
import com.example.demo.model.Users;
import com.example.demo.model.EmployeeInProject;
import com.example.demo.service.ProjectMapper;
import com.example.demo.service.ProjectRepository;
import com.example.demo.service.UsersRepository;
import com.example.demo.service.EmployeeInProjectRepository;

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

    // ---------------------------------------
    // 🔹 1. שליפת כל הפרויקטים (ADMIN בלבד)
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
    // 🔹 3. יצירת פרויקט חדש (יצירה עם מנהל + עובדים)
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

        // 🔹 שיוך מנהל פרויקט
        if (dto.getLeaderId() != null) {
            Users leader = usersRepository.findById(dto.getLeaderId())
                    .orElseThrow(() -> new RuntimeException("Leader not found"));
            project.setProjectLeader(leader);
        }

        Project savedProject = projectRepository.save(project);

        // 🔹 שיוך עובדים לפרויקט (אם נשלח employeeIds)
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
    // 🔹 4. עדכון פרויקט (שם, תאריכים, סטטוס)
    // ---------------------------------------
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEAMLEADER')")
    public ProjectDTO updateProject(@PathVariable Long id, @RequestBody ProjectDTO dto) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // 🔹 עדכון בסיסי
        project.setProjectName(dto.getName());
        project.setProjectDescription(dto.getDescription());
        project.setProjectStartDate(dto.getStartDate());
        project.setProjectEndDate(dto.getEndDate());
        project.setProjectStatus(dto.getStatus());
        project.setProgressPercentage(dto.getProgress());

        // 🔹 שינוי מנהל פרויקט
        if (dto.getLeaderId() != null) {
            Users leader = usersRepository.findById(dto.getLeaderId())
                    .orElseThrow(() -> new RuntimeException("Leader not found"));
            project.setProjectLeader(leader);
        }

        // ----------------------------------------------------------
        // 🔥 תיקון מרכזי: אם לא נשלחו workers → לא מוחקים כלום!
        // ----------------------------------------------------------
        if (dto.getEmployeeIds() != null) {

            // מוחקים רק אם employeeIds != null
            List<EmployeeInProject> existingLinks =
                    employeeInProjectRepository.findByProject_ProjectId(id);

            for (EmployeeInProject link : existingLinks) {
                employeeInProjectRepository.delete(link);
            }

            // מוסיפים את החדשים מתוך employeeIds
            for (Long empId : dto.getEmployeeIds()) {
                Users user = usersRepository.findById(empId)
                        .orElseThrow(() -> new RuntimeException("Employee not found"));

                EmployeeInProject link = new EmployeeInProject();
                link.setProject(project);
                link.setUser(user);
                link.setAssignedDate(LocalDate.now());
                link.setStatus("ACTIVE");

                employeeInProjectRepository.save(link);
            }
        }
        // ----------------------------------------------------------
        // 🔥 אם employeeIds == null → לא נוגעים בקשרים!
        // ----------------------------------------------------------

        Project updated = projectRepository.save(project);
        return projectMapper.projectToProjectDTO(updated);
    }



    // ---------------------------------------
    // 🔹 5. מחיקת פרויקט
    // ---------------------------------------
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProject(@PathVariable Long id) {
        projectRepository.deleteById(id);
    }

    // ---------------------------------------
    // 🔹 6. פרויקטים של מנהל צוות
    // ---------------------------------------
    @GetMapping("/byLeader/{leaderId}")
    @PreAuthorize("hasAnyRole('ADMIN','TEAMLEADER')")
    public List<ProjectDTO> getProjectsByLeader(@PathVariable Long leaderId) {

        List<EmployeeInProject> links =
                employeeInProjectRepository.findByUser_IdAndRoleDescription(leaderId, "Team Leader");

        return links.stream()
                .map(link -> projectMapper.projectToProjectDTO(link.getProject()))
                .collect(Collectors.toList());
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

        boolean alreadyAssigned =
                employeeInProjectRepository.existsByUser_IdAndProject_ProjectId(userId, projectId);

        if (alreadyAssigned) return "Employee already assigned";

        EmployeeInProject link = new EmployeeInProject();
        link.setProject(project);
        link.setUser(user);
        link.setAssignedDate(LocalDate.now());
        link.setStatus("ACTIVE");

        employeeInProjectRepository.save(link);

        return "Employee added successfully";
    }
}
