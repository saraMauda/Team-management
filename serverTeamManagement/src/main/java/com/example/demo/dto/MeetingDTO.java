package com.example.demo.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MeetingDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDate meetingDate;
    private String meetingLocation;
    private String status;
    private LocalDateTime createdAt;
    private String projectName;

    public MeetingDTO() {
    }

    public MeetingDTO(Long id, String title, String description, LocalDate meetingDate, String meetingLocation, String status, LocalDateTime createdAt, String projectName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.meetingDate = meetingDate;
        this.meetingLocation = meetingLocation;
        this.status = status;
        this.createdAt = createdAt;
        this.projectName = projectName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getMeetingDate() {
        return meetingDate;
    }

    public void setMeetingDate(LocalDate meetingDate) {
        this.meetingDate = meetingDate;
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

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
