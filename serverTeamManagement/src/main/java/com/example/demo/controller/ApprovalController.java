package com.example.demo.controller;

import com.example.demo.model.Approval;
import com.example.demo.model.EmployeeInProject;
import com.example.demo.model.Meeting;
import com.example.demo.service.ApprovalRepository;
import com.example.demo.service.EmployeeInProjectRepository;
import com.example.demo.service.MeetingRepository;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/approvals")
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalRepository approvalRepository;
    private final MeetingRepository meetingRepository;
    private final EmployeeInProjectRepository employeeInProjectRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('TEAMLEADER','ADMIN')")
    public ResponseEntity<Approval> create(@Valid @RequestBody Approval approval) {

        if (approval.getMeeting() == null || approval.getMeeting().getMeetingId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meeting ID is required");
        }

        if (approval.getApprovalEmployeeInProject() == null ||
                approval.getApprovalEmployeeInProject().getEmployeeProjectId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "EmployeeInProject ID is required");
        }

        Meeting meeting = meetingRepository.findById(approval.getMeeting().getMeetingId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Meeting not found"));

        EmployeeInProject eip = employeeInProjectRepository
                .findById(approval.getApprovalEmployeeInProject().getEmployeeProjectId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "EmployeeInProject not found"));

        approval.setMeeting(meeting);
        approval.setApprovalEmployeeInProject(eip);

        if (approval.getApprovalDate() == null) {
            approval.setApprovalDate(LocalDate.now());
        }

        Approval saved = approvalRepository.save(approval);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/meeting/{meetingId}")
    @PreAuthorize("hasAnyRole('TEAMLEADER','ADMIN')")
    public List<Approval> getByMeeting(@PathVariable Long meetingId) {
        return approvalRepository.findAll().stream()
                .filter(a -> a.getMeeting() != null && a.getMeeting().getMeetingId().equals(meetingId))
                .toList();
    }
}
