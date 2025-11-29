package com.example.demo.service;

import com.example.demo.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // ❗ שם השדה הנכון הוא leader ולא projectLeader
    List<Project> findByLeader_Id(Long leaderId);

    List<Project> findByEmployeeProjects_User_Email(String email);

    List<Project> findByEmployeeProjects_User_Id(Long userId);
}
