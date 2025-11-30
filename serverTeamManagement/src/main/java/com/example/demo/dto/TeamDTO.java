package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TeamDTO {
    private Long id;
    private Long leaderId;
    private String leaderName;
    private String leaderEmail;
    private String leaderImage;
    private List<UsersDTO> members;

}
