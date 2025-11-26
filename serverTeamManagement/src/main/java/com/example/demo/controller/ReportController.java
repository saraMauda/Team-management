package com.example.demo.controller;

import com.example.demo.dto.ReportDTO;
import com.example.demo.model.EmployeeInProject;
import com.example.demo.model.Report;
import com.example.demo.service.EmployeeInProjectRepository;
import com.example.demo.service.ReportMapper;
import com.example.demo.service.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private EmployeeInProjectRepository employeeInProjectRepository;
    @GetMapping
    public List<ReportDTO> getAllReports() {
        return reportRepository.findAll()
                .stream()
                .map(reportMapper::reportToReportDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ReportDTO getReportById(@PathVariable Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        return reportMapper.reportToReportDTO(report);
    }

    @PostMapping
    public ReportDTO createReport(@RequestBody ReportDTO dto) {
        if (dto.getProjectId() == null || dto.getUserId() == null)
            throw new RuntimeException("projectId and userId are required");

        // מוצאים את המשבצת של המשתמש בפרויקט
        EmployeeInProject link = employeeInProjectRepository
                .findByUser_IdAndProject_ProjectId(dto.getUserId(), dto.getProjectId())
                .orElseThrow(() -> new RuntimeException("User is not assigned to this project"));

        Report report = new Report();
        report.setReportTitle(dto.getTitle());
        report.setReportDescription(dto.getDescription());
        report.setReportStatus(dto.getStatus());

        report.setReportDate(dto.getDate());
        report.setLastEdited(LocalDate.now());

        // שייכות הדוח
        report.setReportEmployeeInProject(link);

        Report saved = reportRepository.save(report);

        return reportMapper.reportToReportDTO(saved);
    }


    @PutMapping("/{id}")
    public ReportDTO updateReport(@PathVariable Long id, @RequestBody ReportDTO reportDTO) {
        Report existing = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        existing.setReportTitle(reportDTO.getTitle());
        existing.setReportDescription(reportDTO.getDescription());
        existing.setReportDate(reportDTO.getReportDate());
        existing.setLastEdited(reportDTO.getLastEdited());

        Report updated = reportRepository.save(existing);
        return reportMapper.reportToReportDTO(updated);
    }

    @PutMapping("/{id}/approve")
    public ReportDTO approvalReport(@PathVariable Long id){
        Report report = reportRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Report not found"));

        report.setReportStatus("APPROVED");
        Report update = reportRepository.save(report);
        return reportMapper.reportToReportDTO(update);
    }

    @GetMapping("/byEmployee/{employeeId}")
    public List<ReportDTO>getReportsByEmployee(@PathVariable Long employeeId){
        return reportRepository.findByReportEmployeeInProject_User_Id(employeeId)
                .stream()
                .map(reportMapper::reportToReportDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/byProject/{projectId}")
    public List<ReportDTO>getReportsByProject(@PathVariable Long projectId){
        return reportRepository.findByReportEmployeeInProject_Project_ProjectId(projectId)
                .stream()
                .map(reportMapper::reportToReportDTO)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public void deleteReport(@PathVariable Long id){
        reportRepository.deleteById(id);
    }

    // ✔ דוחות לעובד מחובר
    @GetMapping("/byEmployee")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public List<ReportDTO> getReportsForLoggedEmployee(Authentication authentication) {
        String email = authentication.getName();
        return reportRepository.findByReportEmployeeInProject_User_Email(email)
                .stream()
                .map(reportMapper::reportToReportDTO)
                .collect(Collectors.toList());
    }
    @GetMapping("/byLeader/{leaderId}")
    public List<ReportDTO> getReportsForLeader(@PathVariable Long leaderId) {

        // 1. מוצאים את כל העובדים בצוות של המנהל
        List<EmployeeInProject> teamMembers =
                employeeInProjectRepository.findByProject_ProjectLeader_Id(leaderId);

        // 2. מוצאים עבור כל אחד את כל הדוחות
        List<Report> reports = teamMembers.stream()
                .flatMap(member -> reportRepository.findByReportEmployeeInProject_EmployeeProjectId(member.getEmployeeProjectId())
                        .stream())
                .collect(Collectors.toList());
        // 3. ממפים ל-DTO
        return reports.stream()
                .map(reportMapper::reportToReportDTO)
                .collect(Collectors.toList());
    }
    @PutMapping("/update-status/{reportId}")
    public ReportDTO updateReportStatus(
            @PathVariable Long reportId,
            @RequestBody ReportDTO dto
    ) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        if (dto.getStatus() == null)
            throw new RuntimeException("Status is required");

        report.setReportStatus(dto.getStatus());
        report.setLastEdited(LocalDate.now());

        Report updated = reportRepository.save(report);

        return reportMapper.reportToReportDTO(updated);
    }

}
