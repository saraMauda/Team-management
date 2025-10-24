package com.example.demo.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
@Entity
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long meetingId;
    private String title;
    private LocalDate meetingDate;
    private String description;
    private String meetingLocation;
    private String status;
    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(name = "projectId")
    private Project project;
    @OneToMany(mappedBy = "meeting")
    private List<Approval> approvals;

    public Meeting() {
    }

    public Meeting(Long meetingId, String title, LocalDate meetingDate, String description, String meetingLocation, String status, LocalDateTime createdAt, Project project, List<Approval> approvals) {
        this.meetingId = meetingId;
        this.title = title;
        this.meetingDate = meetingDate;
        this.description = description;
        this.meetingLocation = meetingLocation;
        this.status = status;
        this.createdAt = createdAt;
        this.project = project;
        this.approvals = approvals;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMeetingLocation() {
        return meetingLocation;
    }

    public void setMeetingLocation(String meetingLocation) {
        this.meetingLocation = meetingLocation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(Long meetingId) {
        this.meetingId = meetingId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getMeetingDate() {
        return meetingDate;
    }

    public void setMeetingDate(LocalDate meetingDate) {
        this.meetingDate = meetingDate;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<Approval> getApprovals() {
        return approvals;
    }

    public void setApprovals(List<Approval> approvals) {
        this.approvals = approvals;
    }
}
