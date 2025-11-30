package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Approval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long approvalId;
    private boolean approved;
    private LocalDate approvalDate;
    @ManyToOne
    @JoinColumn(name = "employeeInProjectId")
    private EmployeeInProject approvalEmployeeInProject;
    @ManyToOne
    @JoinColumn(name = "meetingId")
    private Meeting meeting;

    public Approval() {
    }

    public Approval(Long approvalId, EmployeeInProject approvalEmployeeInProject, Meeting meeting, boolean approved, LocalDate approvalDate) {
        this.approvalId = approvalId;
        this.approvalEmployeeInProject = approvalEmployeeInProject;
        this.meeting = meeting;
        this.approved = approved;
        this.approvalDate = approvalDate;
    }
}
