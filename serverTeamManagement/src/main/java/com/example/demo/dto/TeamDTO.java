package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

public class TeamDTO {
    private Long id;
    private Long leaderId;
    private String leaderName;
    private String leaderEmail;
    private String leaderImage;
    private List<UsersDTO> members;

    public TeamDTO(Long id, Long leaderId, String leaderName, String leaderEmail, String leaderImage, List<UsersDTO> members) {
        this.id = id;
        this.leaderId = leaderId;
        this.leaderName = leaderName;
        this.leaderEmail = leaderEmail;
        this.leaderImage = leaderImage;
        this.members = members;
    }

    public TeamDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(Long leaderId) {
        this.leaderId = leaderId;
    }

    public String getLeaderName() {
        return leaderName;
    }

    public void setLeaderName(String leaderName) {
        this.leaderName = leaderName;
    }

    public String getLeaderEmail() {
        return leaderEmail;
    }

    public void setLeaderEmail(String leaderEmail) {
        this.leaderEmail = leaderEmail;
    }

    public String getLeaderImage() {
        return leaderImage;
    }

    public void setLeaderImage(String leaderImage) {
        this.leaderImage = leaderImage;
    }

    public List<UsersDTO> getMembers() {
        return members;
    }

    public void setMembers(List<UsersDTO> members) {
        this.members = members;
    }
}
