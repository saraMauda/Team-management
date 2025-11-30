package com.example.demo.controller;

import com.example.demo.dto.ReportDTO;
import com.example.demo.model.EmployeeInProject;
import com.example.demo.model.Report;
import com.example.demo.service.EmployeeInProjectRepository;
import com.example.demo.service.ReportMapper;
import com.example.demo.service.ReportRepository;
import javax.validation.Valid;
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
    @PreAuthorize("hasAnyRole('ADMIN','TEAMLEADER')")
    public List<ReportDTO> getAllReports() {
        return reportRepository.findAll()
                .stream()
                .map(reportMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEAMLEADER','EMPLOYEE')")
    public ReportDTO getReportById(@PathVariable Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        return reportMapper.toDTO(report);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE','TEAMLEADER')")
    public ReportDTO createReport(@Valid @RequestBody ReportDTO dto) {
        if (dto.getProjectId() == null || dto.getUserId() == null)
            throw new RuntimeException("projectId and userId are required");

        EmployeeInProject link = employeeInProjectRepository
                .findByUser_IdAndProject_ProjectId(dto.getUserId(), dto.getProjectId())
                .orElseThrow(() -> new RuntimeException("User is not assigned to this project"));

        Report report = new Report();
        report.setReportTitle(dto.getTitle());
        report.setReportDescription(dto.getDescription());
        report.setReportStatus(dto.getStatus());
        report.setReportDate(dto.getReportDate());
        report.setLastEdited(LocalDate.now());
        report.setReportEmployeeInProject(link);

        Report saved = reportRepository.save(report);

        return reportMapper.toDTO(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYEE','TEAMLEADER','ADMIN')")
    public ReportDTO updateReport(@PathVariable Long id, @Valid @RequestBody ReportDTO reportDTO) {
        Report existing = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        existing.setReportTitle(reportDTO.getTitle());
        existing.setReportDescription(reportDTO.getDescription());
        existing.setReportDate(reportDTO.getReportDate());
        existing.setLastEdited(reportDTO.getLastEdited());

        Report updated = reportRepository.save(existing);
        return reportMapper.toDTO(updated);
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('TEAMLEADER','ADMIN')")
    public ReportDTO approvalReport(@PathVariable Long id){
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        report.setReportStatus("APPROVED");
        Report update = reportRepository.save(report);
        return reportMapper.toDTO(update);
    }

    @GetMapping("/byEmployee/{employeeId}")
    @PreAuthorize("hasAnyRole('TEAMLEADER','ADMIN')")
    public List<ReportDTO> getReportsByEmployee(@PathVariable Long employeeId){
        return reportRepository.findByReportEmployeeInProject_User_Id(employeeId)
                .stream()
                .map(reportMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/byProject/{projectId}")
    @PreAuthorize("hasAnyRole('TEAMLEADER','ADMIN')")
    public List<ReportDTO> getReportsByProject(@PathVariable Long projectId){
        return reportRepository.findByReportEmployeeInProject_Project_ProjectId(projectId)
                .stream()
                .map(reportMapper::toDTO)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEAMLEADER','ADMIN')")
    public void deleteReport(@PathVariable Long id){
        reportRepository.deleteById(id);
    }

    @GetMapping("/byEmployee")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public List<ReportDTO> getReportsForLoggedEmployee(Authentication authentication) {
        String email = authentication.getName();
        return reportRepository.findByReportEmployeeInProject_User_Email(email)
                .stream()
                .map(reportMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/byLeader/{leaderId}")
    @PreAuthorize("hasRole('TEAMLEADER')")
    public List<ReportDTO> getReportsForLeader(@PathVariable Long leaderId) {

        List<EmployeeInProject> teamMembers =
                employeeInProjectRepository.findByProject_Leader_Id(leaderId);

        List<Report> reports = teamMembers.stream()
                .flatMap(member -> reportRepository
                        .findByReportEmployeeInProject_Project_ProjectId(
                                member.getProject().getProjectId())
                        .stream())
                .collect(Collectors.toList());

        return reports.stream()
                .map(reportMapper::toDTO)
                .collect(Collectors.toList());
    }

    @PutMapping("/update-status/{reportId}")
    @PreAuthorize("hasAnyRole('TEAMLEADER','ADMIN')")
    public ReportDTO updateReportStatus(
            @PathVariable Long reportId,
            @Valid @RequestBody ReportDTO dto
    ) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        if (dto.getStatus() == null)
            throw new RuntimeException("Status is required");

        report.setReportStatus(dto.getStatus());
        report.setLastEdited(LocalDate.now());

        Report updated = reportRepository.save(report);

        return reportMapper.toDTO(updated);
    }
}
