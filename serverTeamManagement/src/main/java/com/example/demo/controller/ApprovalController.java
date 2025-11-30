package com.example.demo.controller;

import com.example.demo.model.Approval;
import com.example.demo.model.EmployeeInProject;
import com.example.demo.model.Meeting;
import com.example.demo.service.ApprovalRepository;
import com.example.demo.service.EmployeeInProjectRepository;
import com.example.demo.service.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    public Approval create(@RequestBody Approval approval) {

        // שליפת Meeting אמיתי מה-ID שנשלח
        if (approval.getMeeting() != null && approval.getMeeting().getMeetingId() != null) {
            Meeting m = meetingRepository.findById(
                    approval.getMeeting().getMeetingId()
            ).orElseThrow(() -> new RuntimeException("Meeting not found"));
            approval.setMeeting(m);
        }

        // שליפת EmployeeInProject אמיתי מה-ID שנשלח
        if (approval.getApprovalEmployeeInProject() != null &&
                approval.getApprovalEmployeeInProject().getEmployeeProjectId() != null) {

            EmployeeInProject e = employeeInProjectRepository.findById(
                    approval.getApprovalEmployeeInProject().getEmployeeProjectId()
            ).orElseThrow(() -> new RuntimeException("EmployeeInProject not found"));

            approval.setApprovalEmployeeInProject(e);
        }

        // הגדרת תאריך אוטומטי
        if (approval.getApprovalDate() == null) {
            approval.setApprovalDate(LocalDate.now());
        }

        return approvalRepository.save(approval);
    }


    @GetMapping("/meeting/{meetingId}")
    public List<Approval> getByMeeting(@PathVariable Long meetingId) {
        return approvalRepository.findAll().stream()
                .filter(a -> a.getMeeting().getMeetingId().equals(meetingId))
                .toList();
    }
}
