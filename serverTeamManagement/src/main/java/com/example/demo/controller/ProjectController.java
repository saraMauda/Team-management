package com.example.demo.controller;

import com.example.demo.dto.ProjectDTO;
import com.example.demo.model.Project;
import com.example.demo.model.Users;
import com.example.demo.model.EmployeeInProject;
import com.example.demo.service.*;
import com.example.demo.model.Meeting;
import com.example.demo.model.Approval;
import com.example.demo.model.Report;
import com.example.demo.service.MeetingRepository;
import com.example.demo.service.ApprovalRepository;
import com.example.demo.service.ReportRepository;

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
    private ApprovalRepository approvalRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private ReportRepository reportRepository;

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
        // 🔥 טיפול בעובדים בפרויקט
        // ----------------------------------------------------------
        if (dto.getEmployeeIds() != null) {

            // כל העובדים הקיימים בפרויקט
            List<EmployeeInProject> existingLinks =
                    employeeInProjectRepository.findByProject_ProjectId(id);

            // 🔥 קודם מוחקים את כל ה-Approvals של כל עובד בפרויקט
            for (EmployeeInProject link : existingLinks) {
                approvalRepository.deleteByApprovalEmployeeInProject_EmployeeProjectId(
                        link.getEmployeeProjectId()
                );
            }

            // עכשיו מחיקת ה־EmployeeInProject
            employeeInProjectRepository.deleteAll(existingLinks);

            // הוספת העובדים החדשים בלבד
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

        // ❗ אם employeeIds == null → לא נוגעים בעובדים בכלל

        Project updated = projectRepository.save(project);
        return projectMapper.projectToProjectDTO(updated);
    }



    // ---------------------------------------
    // 🔹 5. מחיקת פרויקט
    // ---------------------------------------
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProject(@PathVariable Long id) {

        // ========== שלב 1: מחיקת פגישות ==========
        List<Meeting> meetings = meetingRepository.findByProject_ProjectId(id);

        // לכל פגישה יש approvals — צריכים למחוק קודם
        for (Meeting meeting : meetings) {
            for (Approval approval : meeting.getApprovals()) {
                approvalRepository.deleteById(approval.getApprovalId());
            }
        }

        meetingRepository.deleteAll(meetings);


        // ========== שלב 2: מחיקת עובדים בפרויקט ==========
        List<EmployeeInProject> employees =
                employeeInProjectRepository.findByProject_ProjectId(id);

        for (EmployeeInProject eip : employees) {

            // קודם מחיקת הדוחות של העובד
            List<Report> reports =
                    reportRepository.findByReportEmployeeInProject_EmployeeProjectId(
                            eip.getEmployeeProjectId()
                    );
            reportRepository.deleteAll(reports);

            // מחיקת ה-Approvals של העובד
            for (Approval approval : eip.getApprovals()) {
                approvalRepository.deleteById(approval.getApprovalId());
            }
        }

        // עכשיו מחיקת הרשומות EmployeeInProject
        employeeInProjectRepository.deleteAll(employees);


        // ========== שלב 3: מחיקת הפרויקט ==========
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
