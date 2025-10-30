package com.example.demo.service;

import com.example.demo.dto.MeetingDTO;
import com.example.demo.model.Meeting;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface MeetingMapper {

    List<MeetingDTO> meetingsToMeetingDTOs(List<Meeting> meetings);

    default MeetingDTO meetingToMeetingDTO(Meeting meeting) {
        MeetingDTO dto = new MeetingDTO();
        dto.setId(meeting.getMeetingId());
        dto.setTitle(meeting.getTitle());
        dto.setDescription(meeting.getDescription());
        dto.setMeetingDate(meeting.getMeetingDate());
        dto.setMeetingLocation(meeting.getMeetingLocation());
        dto.setStatus(meeting.getStatus());
        dto.setCreatedAt(meeting.getCreatedAt());

        if (meeting.getProject() != null)
            dto.setProjectName(meeting.getProject().getProjectName());

        return dto;
    }
//    default Meeting meetingDTOToMeeting(MeetingDTO dto){
//        Meeting meeting=new Meeting();
//        meeting.setTitle(dto.getTitle());
//
//    }
default Meeting meetingDTOToMeeting(MeetingDTO dto) {
    Meeting meeting = new Meeting();

    meeting.setMeetingId(dto.getId());
    meeting.setTitle(dto.getTitle());
    meeting.setDescription(dto.getDescription());
    meeting.setMeetingDate(dto.getMeetingDate());
    meeting.setMeetingLocation(dto.getMeetingLocation());
    meeting.setStatus(dto.getStatus());
    meeting.setCreatedAt(dto.getCreatedAt());

    return meeting;
}
}
