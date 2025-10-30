package com.example.demo.controller;

import com.example.demo.dto.UsersDTO;
import com.example.demo.model.Users;
import com.example.demo.service.ImageUtils;
import com.example.demo.service.UsersMapper;
import com.example.demo.service.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UsersController {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private UsersMapper usersMapper;

    @GetMapping("get/{id}")
    public ResponseEntity<UsersDTO> getUsers(@PathVariable("id") long id) {
        Users user = usersRepository.findById(id).get();
        if(user != null) {
            return new ResponseEntity<>(usersMapper.userToUsersDTO(user), HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    //יצירת משתמש חדש (למשל בהרשמה)
    @PostMapping
    public ResponseEntity<UsersDTO>createUser(@RequestBody UsersDTO usersDTO){
        Users users=usersMapper.usersDTOToUser(usersDTO);
        Users savedUser=usersRepository.save(users);
        return ResponseEntity.status(HttpStatus.CREATED).body(usersMapper.userToUsersDTO(savedUser));
    }
    //עדכון משתמש
    @PutMapping("/{id}")
    public ResponseEntity<UsersDTO>updateUser(@PathVariable Long id,@RequestBody UsersDTO userDTO){
        return usersRepository.findById(id)
                .map(existing->{
                    existing.setName(userDTO.getName());
                    existing.setEmail(userDTO.getEmail());
                    usersRepository.save(existing);
                    return ResponseEntity.ok(usersMapper.userToUsersDTO(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    //מחיקת משתמש (רק Admin רשאי)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void>deleteUser(@PathVariable Long id){
        if(!usersRepository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        usersRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/upload")
    public ResponseEntity<Users> uploadUsersWithImage(
            @RequestPart("image") MultipartFile file,
            @RequestPart("users") Users u) {
        try {
            ImageUtils.uploadImage(file);
            u.setImagePath(file.getOriginalFilename());
            Users user = usersRepository.save(u);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (IOException e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @GetMapping
    public List<UsersDTO> getAllUsers() {
        return usersRepository.findAll()
                .stream()
                .map(usersMapper::userToUsersDTO)
                .collect(Collectors.toList());
    }


}
