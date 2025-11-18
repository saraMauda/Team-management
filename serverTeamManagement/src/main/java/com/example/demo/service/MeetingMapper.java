package com.example.demo.service;

import com.example.demo.dto.MeetingDTO;
import com.example.demo.model.Meeting;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MeetingMapper {

    // ========= ENTITY → DTO =========
    default MeetingDTO toDTO(Meeting meeting) {

        MeetingDTO dto = new MeetingDTO();

        dto.setMeetingId(meeting.getMeetingId());
        dto.setTitle(meeting.getTitle());
        dto.setMeetingDate(meeting.getMeetingDate());
        dto.setDescription(meeting.getDescription());
        dto.setMeetingLocation(meeting.getMeetingLocation());
        dto.setStatus(meeting.getStatus());
        dto.setCreatedAt(meeting.getCreatedAt());

        // ⭐ projectId
        if (meeting.getProject() != null) {
            dto.setProjectId(meeting.getProject().getProjectId());
        }

        return dto;
    }

    // ========= DTO → ENTITY =========
    default Meeting toEntity(MeetingDTO dto) {

        Meeting meeting = new Meeting();

        meeting.setMeetingId(dto.getMeetingId());
        meeting.setTitle(dto.getTitle());
        meeting.setMeetingDate(dto.getMeetingDate());
        meeting.setDescription(dto.getDescription());
        meeting.setMeetingLocation(dto.getMeetingLocation());
        meeting.setStatus(dto.getStatus());
        meeting.setCreatedAt(dto.getCreatedAt());

        // ⭐ project יוגדר בקונטרולר
        return meeting;
    }
}
