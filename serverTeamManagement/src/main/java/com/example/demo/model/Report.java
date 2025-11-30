package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long reportId;
    private String reportTitle;
    private String reportDescription;
    private LocalDate reportDate;
    private String reportStatus;
    private LocalDate lastEdited;
    @ManyToOne
    @JoinColumn(name = "employeeProjectId")
    private EmployeeInProject reportEmployeeInProject;
    @OneToMany(mappedBy = "report")
    @JsonIgnore
    private List<ReportComment> reportComments;

    public Report() {
    }

    public Report(long reportId, String reportTitle, String reportDescription, LocalDate reportDate, String reportStatus, LocalDate lastEdited, EmployeeInProject reportEmployeeInProject, List<ReportComment> reportComments) {
        this.reportId = reportId;
        this.reportTitle = reportTitle;
        this.reportDescription = reportDescription;
        this.reportDate = reportDate;
        this.reportStatus = reportStatus;
        this.lastEdited = lastEdited;
        this.reportEmployeeInProject = reportEmployeeInProject;
        this.reportComments = reportComments;
    }
}
