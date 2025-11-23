package com.example.demo.service;

import com.example.demo.model.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    List<Meeting> findByProject_ProjectIdIn(List<Long> projectIds);

    List<Meeting> findByProject_ProjectId(Long projectId);
    // ✔ פגישות לפי העובד המחובר
    List<Meeting> findByProject_ProjectEmployeeProjects_User_Email(String email);
    List<Meeting> findByApprovals_ApprovalEmployeeInProject_User_Id(Long userId);

}
