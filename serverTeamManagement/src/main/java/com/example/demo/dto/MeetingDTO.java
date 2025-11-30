package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Getter
@Setter
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
}
