package com.example.demo.service;

import com.example.demo.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByLeaderId(Long leaderId);

}
