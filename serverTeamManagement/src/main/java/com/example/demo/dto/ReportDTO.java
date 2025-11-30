package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
public class ReportDTO {

    private Long id;
    private String title;
    private String description;
    private String status;
    private LocalDate reportDate;
    private LocalDate lastEdited;
    private String employeeName;
    private String projectName;
    private Long projectId;
    private Long userId;
    private Integer hours;
    private LocalDate date;

    public ReportDTO() {}
}
