package com.example.demo.service;

import com.example.demo.dto.ReportDTO;
import com.example.demo.model.Report;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ReportMapper {

    List<ReportDTO> reportsToReportDTOs(List<Report> reports);

    default ReportDTO reportToReportDTO(Report report) {
        ReportDTO dto = new ReportDTO();
        dto.setId(report.getReportId());
        dto.setTitle(report.getReportTitle());
        dto.setDescription(report.getReportDescription());
        dto.setStatus(report.getReportStatus());
        dto.setReportDate(report.getReportDate());
        dto.setLastEdited(report.getLastEdited());

        if (report.getReportEmployeeInProject() != null) {
            if (report.getReportEmployeeInProject().getUser() != null)
                dto.setEmployeeName(report.getReportEmployeeInProject().getUser().getName());

            if (report.getReportEmployeeInProject().getProject() != null)
                dto.setProjectName(report.getReportEmployeeInProject().getProject().getProjectName());
        }

        return dto;
    }
}
