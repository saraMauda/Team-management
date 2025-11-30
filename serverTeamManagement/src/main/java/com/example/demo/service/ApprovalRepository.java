package com.example.demo.service;

import com.example.demo.model.Approval;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalRepository extends JpaRepository<Approval, Long> {

    void deleteByMeeting_MeetingId(Long meetingId);
}
