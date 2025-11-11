package com.example.demo.controller;

import com.example.demo.dto.MeetingDTO;
import com.example.demo.model.Meeting;
import com.example.demo.service.MeetingMapper;
import com.example.demo.service.MeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/meetings")
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
    //  יצירת ישיבה חדשה
    @PostMapping
    public MeetingDTO createMeeting(@RequestBody MeetingDTO meetingDTO) {
        Meeting meeting = meetingMapper.meetingDTOToMeeting(meetingDTO);
        Meeting savedMeeting = meetingRepository.save(meeting);
        return meetingMapper.meetingToMeetingDTO(savedMeeting);
    }

    //  עדכון ישיבה קיימת
    @PutMapping("/{id}")
    public MeetingDTO updateMeeting(@PathVariable Long id, @RequestBody MeetingDTO meetingDTO) {
        Meeting existing = meetingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meeting not found"));

        existing.setTitle(meetingDTO.getTitle());
        existing.setDescription(meetingDTO.getDescription());
        existing.setMeetingDate(meetingDTO.getMeetingDate());
        existing.setCreatedAt(meetingDTO.getCreatedAt());
        existing.setMeetingLocation(meetingDTO.getMeetingLocation());
        existing.setStatus(meetingDTO.getStatus());
        Meeting updated = meetingRepository.save(existing);
        return meetingMapper.meetingToMeetingDTO(updated);
    }

    //  מחיקת ישיבה
    @DeleteMapping("/{id}")
    public void deleteMeeting(@PathVariable Long id) {
        meetingRepository.deleteById(id);
    }

    //  ישיבות לפי פרויקט (מומלץ להוסיף גם ב־Repository)
    @GetMapping("/byProject/{projectId}")
    public List<MeetingDTO> getMeetingsByProject(@PathVariable Long projectId) {
        return meetingRepository.findByProject_ProjectId(projectId)
                .stream()
                .map(meetingMapper::meetingToMeetingDTO)
                .collect(Collectors.toList());
    }
    @GetMapping("/byEmployee")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public List<MeetingDTO> getMeetingsForLoggedEmployee(Authentication authentication) {
        String email = authentication.getName(); // המייל של המשתמש המחובר
        return meetingRepository.findByProject_ProjectEmployeeProjects_User_Email(email)
                .stream()
                .map(meetingMapper::meetingToMeetingDTO)
                .collect(Collectors.toList());
    }


}
