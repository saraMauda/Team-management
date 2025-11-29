package com.example.demo.service;

import com.example.demo.model.EmployeeInProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeInProjectRepository extends JpaRepository<EmployeeInProject, Long> {

    // עובד נמצא בפרויקט
    boolean existsByUser_IdAndProject_ProjectId(Long userId, Long projectId);

    // עובדים של פרויקט
    List<EmployeeInProject> findByProject_ProjectId(Long projectId);

    // כל העובדים של כל הפרויקטים שמנהל מסוים מנהל
    // ❗ תיקון: project.projectLeader → project.leader
    List<EmployeeInProject> findByProject_Leader_Id(Long leaderId);

    // כל הפרויקטים שעובד נמצא בהם
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
