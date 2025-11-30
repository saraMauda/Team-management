package com.example.demo.service;

import com.example.demo.dto.MeetingDTO;
import com.example.demo.model.Meeting;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface MeetingMapper {

    @Mapping(target = "projectId", ignore = true)
    MeetingDTO toDTO(Meeting meeting);

    @AfterMapping
    default void fillProjectId(@MappingTarget MeetingDTO dto, Meeting meeting) {
        if (meeting.getProject() != null) {
            dto.setProjectId(meeting.getProject().getProjectId());
        }
    }
}
