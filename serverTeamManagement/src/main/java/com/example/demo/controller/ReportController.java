package com.example.demo.controller;

import com.example.demo.dto.ReportDTO;
import com.example.demo.model.Report;
import com.example.demo.service.ReportMapper;
import com.example.demo.service.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin
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
}
