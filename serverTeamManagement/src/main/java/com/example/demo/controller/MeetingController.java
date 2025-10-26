package com.example.demo.controller;

import com.example.demo.dto.MeetingDTO;
import com.example.demo.model.Meeting;
import com.example.demo.service.MeetingMapper;
import com.example.demo.service.MeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/meetings")
@CrossOrigin
public class MeetingController {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private MeetingMapper meetingMapper;

    @GetMapping
    public List<MeetingDTO> getAllMeetings() {
        return meetingRepository.findAll()
                .stream()
                .map(meetingMapper::meetingToMeetingDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public MeetingDTO getMeetingById(@PathVariable Long id) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meeting not found"));
        return meetingMapper.meetingToMeetingDTO(meeting);
    }
}
