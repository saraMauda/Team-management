package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
@Getter
@Setter
public class ProjectDTO {

    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private Integer progress;
    private String location;
    private Long leaderId;
    private String leaderName;
    private List<Long> employeeIds;

    public ProjectDTO() {}
}
