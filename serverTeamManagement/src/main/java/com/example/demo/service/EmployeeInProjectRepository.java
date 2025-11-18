package com.example.demo.service;

import com.example.demo.model.EmployeeInProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeInProjectRepository extends JpaRepository<EmployeeInProject, Long> {

    // לבדיקה אם עובד כבר נמצא בפרויקט
    boolean existsByUser_IdAndProject_ProjectId(Long userId, Long projectId);

    // שליפת כל העובדים של פרויקט
    List<EmployeeInProject> findByProject_ProjectId(Long projectId);

    // שליפת כל העובדים של כל הפרויקטים שמנהל מסוים מנהל
    List<EmployeeInProject> findByProject_ProjectLeader_Id(Long leaderId);

    // שליפת כל הפרויקטים שעובד מסוים נמצא בהם
    List<EmployeeInProject> findByUser_Id(Long userId);

    Optional<EmployeeInProject> findByUser_IdAndProject_ProjectId(Long userId, Long projectId);

    boolean existsByProject_ProjectIdAndUser_IdAndRoleDescription(
            Long projectId,
            Long userId,
            String roleDescription
    );
    void deleteByProject_ProjectId(Long projectId);
    List<EmployeeInProject> findByUser_IdAndRoleDescription(Long userId, String roleDescription);

}
