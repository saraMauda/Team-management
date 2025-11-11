package com.example.demo.controller;

import com.example.demo.dto.ReportDTO;
import com.example.demo.model.Report;
import com.example.demo.service.ReportMapper;
import com.example.demo.service.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ReportMapper reportMapper;

    // שליפת כל הדוחות
    @GetMapping
    public List<ReportDTO> getAllReports() {
        return reportRepository.findAll()
                .stream()
                .map(reportMapper::reportToReportDTO)
                .collect(Collectors.toList());
    }

    // שליפת דוח לפי מזהה
    @GetMapping("/{id}")
    public ReportDTO getReportById(@PathVariable Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        return reportMapper.reportToReportDTO(report);
    }

    @PostMapping
    public ReportDTO createReport(@RequestBody ReportDTO reportDTO) {
        Report report = reportMapper.reportDTOToReport(reportDTO);
        Report savedReport = reportRepository.save(report);
        return reportMapper.reportToReportDTO(savedReport);
    }
//עדכון הדוח
    @PutMapping("/{id}")
    public ReportDTO updateReport(@PathVariable Long id, @RequestBody ReportDTO reportDTO) {
        Report existing = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        existing.setReportTitle(reportDTO.getTitle());
        existing.setReportDescription(reportDTO.getDescription());
        existing.setReportDate(reportDTO.getReportDate());
        existing.setLastEdited(reportDTO.getLastEdited());

        Report update = reportRepository.save(existing);
        return reportMapper.reportToReportDTO(update);
    }
    //אישור או דחיית דוח
    @PutMapping("/{id}/approve")
    public ReportDTO approvalReport(@PathVariable Long id){
        Report report=reportRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Report not found"));
        report.setReportStatus("APPROVED");
        Report update=reportRepository.save(report);
        return reportMapper.reportToReportDTO(update);
    }
    //דוחות לפי עובד
@GetMapping("/byEmployee/{employeeId}")
    public List<ReportDTO>getReportsByEmployee(@PathVariable Long employeeId){
        return reportRepository.findByReportEmployeeInProject_User_Id(employeeId)
                .stream()
                .map(reportMapper::reportToReportDTO)
                .collect(Collectors.toList());
}
//דוחות לפי פרויקט
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

    @GetMapping("/byEmployee")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public List<ReportDTO> getReportsForLoggedEmployee(Authentication authentication) {
        String email = authentication.getName(); // המייל של המשתמש המחובר
        return reportRepository.findByReportEmployeeInProject_User_Email(email)
                .stream()
                .map(reportMapper::reportToReportDTO)
                .collect(Collectors.toList());
    }

}
