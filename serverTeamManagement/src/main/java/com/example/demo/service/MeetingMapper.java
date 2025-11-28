package com.example.demo.service;

import com.example.demo.dto.MeetingDTO;
import com.example.demo.model.Meeting;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MeetingMapper {

     @Mapping(target = "projectId",source = "project.projectId")
     MeetingDTO toDTO(Meeting meeting);

    default Meeting toEntity(MeetingDTO dto) {
        if (dto == null) {
            return null;
        }

        Meeting meeting = new Meeting();
        meeting.setMeetingId(dto.getMeetingId());
        meeting.setTitle(dto.getTitle());
        meeting.setMeetingDate(dto.getMeetingDate());
        meeting.setDescription(dto.getDescription());
        meeting.setMeetingLocation(dto.getMeetingLocation());
        meeting.setStatus(dto.getStatus());
        meeting.setCreatedAt(dto.getCreatedAt());

        // project ו־approvals מנוהלים בבקר (Controller) ולא במיפוי
        return meeting;
    }
}
