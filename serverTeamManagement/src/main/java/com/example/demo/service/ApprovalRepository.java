package com.example.demo.service;

import com.example.demo.model.Approval;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalRepository extends JpaRepository<Approval, Long> {

    // מחיקת כל האישורִים של פגישה מסוימת
    void deleteByMeeting_MeetingId(Long meetingId);
}
