package com.example.demo.service;

import com.example.demo.dto.ReportDTO;
import com.example.demo.model.Report;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReportMapper {

    @Mapping(target = "employeeName", source = "reportEmployeeInProject.user.name")
    @Mapping(target = "projectName", source = "reportEmployeeInProject.project.name")
    @Mapping(target = "projectId",   source = "reportEmployeeInProject.project.projectId")
    @Mapping(target = "userId",      source = "reportEmployeeInProject.user.id")
    @Mapping(target = "date",        source = "reportDate")
    ReportDTO reportToReportDTO(Report report);

    Report reportDTOToReport(ReportDTO dto);
}
