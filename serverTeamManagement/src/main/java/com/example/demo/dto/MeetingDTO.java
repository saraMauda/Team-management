package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MeetingDTO {

    private Long meetingId;
    private String title;
    private LocalDate meetingDate;
    private String description;
    private String meetingLocation;
    private String status;
    private LocalDateTime createdAt;
    private Long projectId;

    public MeetingDTO() {
    }

    public MeetingDTO(Long meetingId, String title, LocalDate meetingDate, String description,
                      String meetingLocation, String status, LocalDateTime createdAt, Long projectId) {
        this.meetingId = meetingId;
        this.title = title;
        this.meetingDate = meetingDate;
        this.description = description;
        this.meetingLocation = meetingLocation;
        this.status = status;
        this.createdAt = createdAt;
        this.projectId = projectId;
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

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
