package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.beans.ConstructorProperties;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
public class EmployeeInProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeProjectId;
    private LocalDate assignedDate;
    private String status;
    @ManyToOne
    @JoinColumn(name="projectId")
    private Project project;
    @ManyToOne
    @JoinColumn(name = "userId")
    private Users user;
    private String roleDescription;
    @OneToMany(mappedBy = "reportEmployeeInProject")
    private List<Report> reports;
    @OneToMany(mappedBy = "approvalEmployeeInProject")
    private List<Approval> approvals;

    public EmployeeInProject() {
    }

    public EmployeeInProject(Long employeeProjectId, Project project, Users user, String roleDescription, List<Report> reports, List<Approval> approvals, LocalDate assignedDate, String status) {
        this.employeeProjectId = employeeProjectId;
        this.project = project;
        this.user = user;
        this.roleDescription = roleDescription;
        this.reports = reports;
        this.approvals = approvals;
        this.assignedDate = assignedDate;
        this.status = status;
    }
}
