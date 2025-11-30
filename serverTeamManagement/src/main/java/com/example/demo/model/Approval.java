package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
public class Approval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long approvalId;

    private boolean approved;
    private LocalDate approvalDate;

    @ManyToOne
    @JoinColumn(name = "employeeInProjectId")
    @JsonIgnore
    private EmployeeInProject approvalEmployeeInProject;

    @ManyToOne
    @JoinColumn(name = "meetingId")
    @JsonIgnore
    private Meeting meeting;

    public Approval(Long approvalId, boolean approved, LocalDate approvalDate, EmployeeInProject approvalEmployeeInProject, Meeting meeting) {
        this.approvalId = approvalId;
        this.approved = approved;
        this.approvalDate = approvalDate;
        this.approvalEmployeeInProject = approvalEmployeeInProject;
        this.meeting = meeting;
    }

    public Approval() {
    }

    public Long getApprovalId() {
        return approvalId;
    }

    public void setApprovalId(Long approvalId) {
        this.approvalId = approvalId;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public LocalDate getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(LocalDate approvalDate) {
        this.approvalDate = approvalDate;
    }

    public EmployeeInProject getApprovalEmployeeInProject() {
        return approvalEmployeeInProject;
    }

    public void setApprovalEmployeeInProject(EmployeeInProject approvalEmployeeInProject) {
        this.approvalEmployeeInProject = approvalEmployeeInProject;
    }

    public Meeting getMeeting() {
        return meeting;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }
}
