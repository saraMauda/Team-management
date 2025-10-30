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
    public ResponseEntity<Users> getUsers(@PathVariable("id") long id) {
        Users user = usersRepository.findById(id).orElse(null);
        if(user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
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
    @PostMapping("/upload/{id}")
    public ResponseEntity<String> uploadImage(@PathVariable Long id,
                                              @RequestParam("image") MultipartFile file) throws IOException {
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ImageUtils.uploadImage(file); // שומר את התמונה פיזית
        user.setImagePath(file.getOriginalFilename()); // שומר רק את השם בטבלה
        usersRepository.save(user);

        return ResponseEntity.ok("Image uploaded successfully");
    }

    @GetMapping
    public List<UsersDTO> getAllUsers() {
        return usersRepository.findAll()
                .stream()
                .map(usersMapper::userToUsersDTO)
                .collect(Collectors.toList());
    }


}
