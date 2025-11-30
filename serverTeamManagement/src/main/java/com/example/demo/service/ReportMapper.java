package com.example.demo.service;

import com.example.demo.dto.ReportDTO;
import com.example.demo.model.Report;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReportMapper {

    @Mapping(target = "id", source = "reportId")
    @Mapping(target = "title", source = "reportTitle")
    @Mapping(target = "description", source = "reportDescription")
    @Mapping(target = "status", source = "reportStatus")
    @Mapping(target = "employeeName", source = "reportEmployeeInProject.user.name")
    @Mapping(target = "projectName", source = "reportEmployeeInProject.project.name")
    @Mapping(target = "projectId", source = "reportEmployeeInProject.project.projectId")
    @Mapping(target = "userId", source = "reportEmployeeInProject.user.id")
    @Mapping(target = "commentCount",
            expression = "java(report.getReportComments() == null ? 0 : report.getReportComments().size())")
    ReportDTO toDTO(Report report);
}
