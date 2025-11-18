package com.example.demo.service;

import com.example.demo.dto.ReportDTO;
import com.example.demo.model.Report;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReportMapper {

    default ReportDTO reportToReportDTO(Report report) {
        ReportDTO dto = new ReportDTO();

        dto.setId(report.getReportId());
        dto.setTitle(report.getReportTitle());
        dto.setDescription(report.getReportDescription());
        dto.setStatus(report.getReportStatus());
        dto.setReportDate(report.getReportDate());
        dto.setLastEdited(report.getLastEdited());

        // ⭐ שם העובד ושם הפרויקט
        if (report.getReportEmployeeInProject() != null) {

            if (report.getReportEmployeeInProject().getUser() != null)
                dto.setEmployeeName(report.getReportEmployeeInProject().getUser().getName());

            if (report.getReportEmployeeInProject().getProject() != null) {
                dto.setProjectName(report.getReportEmployeeInProject().getProject().getProjectName());

                // ⭐ projectId
                dto.setProjectId(report.getReportEmployeeInProject().getProject().getProjectId());
            }

            // ⭐ userId
            dto.setUserId(report.getReportEmployeeInProject().getUser().getId());
        }

        // ⭐ שעות ודייט חדשים
        dto.setDate(report.getReportDate());

        return dto;
    }

    default Report reportDTOToReport(ReportDTO dto) {
        Report report = new Report();

        report.setReportId(dto.getId());
        report.setReportTitle(dto.getTitle());
        report.setReportDescription(dto.getDescription());
        report.setReportStatus(dto.getStatus());

        report.setReportDate(dto.getDate());
        report.setLastEdited(dto.getLastEdited());

        return report;
    }
}
