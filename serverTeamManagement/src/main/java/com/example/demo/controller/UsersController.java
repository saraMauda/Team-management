package com.example.demo.controller;

import com.example.demo.dto.UsersDTO;
import com.example.demo.model.ERole;
import com.example.demo.model.Role;
import com.example.demo.model.Users;
import com.example.demo.service.ImageUtils;
import com.example.demo.service.RoleRepository;
import com.example.demo.service.UsersMapper;
import com.example.demo.service.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private RoleRepository roleRepository;

    @Autowired
    private UsersMapper usersMapper;

    public UsersController(UsersRepository usersRepository, RoleRepository roleRepository) {
        this.usersRepository = usersRepository;
        this.roleRepository = roleRepository;
    }

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
    public ResponseEntity<UsersDTO> updateUser(@PathVariable Long id, @RequestBody UsersDTO userDTO) {
        return usersRepository.findById(id)
                .map(existing -> {
                    existing.setName(userDTO.getName());
                    existing.setEmail(userDTO.getEmail());
//                    existing.setRole(userDTO.getRole());
                    existing.setActive(userDTO.isActive());

                    if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
                        existing.setPassword(userDTO.getPassword());
                    }

                    Users saved = usersRepository.save(existing);
                    return ResponseEntity.ok(usersMapper.userToUsersDTO(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    //מחיקת משתמש (רק Admin רשאי)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void>deleteUser(@PathVariable Long id){
        if(!usersRepository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        usersRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/upload/{id}")
    public ResponseEntity<String> uploadImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile file) throws IOException {

        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ImageUtils.uploadImage(file); // שומר את התמונה
        user.setImagePath(file.getOriginalFilename()); // רק שם הקובץ
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
//    @PostMapping("/signup")
//    public ResponseEntity<Users> signUp(@RequestBody Users user){
//        Users u=usersRepository.findByEmail(user.getEmail());
//        if(u!=null)
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        String pass=user.getPassword();
//        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
//        user.getRoles().add(roleRepository.findById((long)1).get());
//        usersRepository.save(user);
//        return new ResponseEntity<>(user,HttpStatus.CREATED);
//
//    }
    //מה שהמורה עשתה. gpt אמר לעשות עם אחד שמחזיר רק userDTO
    //כי אחרת הוא לא יוכל להסתדר איתו בpostman
    //מחזיר לולאה אינסופית
@PostMapping("/signup")
public ResponseEntity<UsersDTO> signUp(@RequestBody Users user) {
    if (usersRepository.findByEmail(user.getEmail()) != null)
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));

    Role role;

    // 🟢 אם נשלח role פשוט ב־JSON (כמו "ROLE_TEAMLEADER")
    if (user.getRoleString() != null && !user.getRoleString().isEmpty()) {
        role = roleRepository.findByName(ERole.valueOf(user.getRoleString()))
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }
    // 🔵 אם נשלח roles כ־List (כמו [{"name": "ROLE_TEAMLEADER"}])
    else if (user.getRoles() != null && !user.getRoles().isEmpty()) {
        String roleName = user.getRoles().iterator().next().getName().name();
        role = roleRepository.findByName(ERole.valueOf(roleName))
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }
    // ⚪ אחרת — ברירת מחדל
    else {
        role = roleRepository.findByName(ERole.ROLE_EMPLOYEE)
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }

    user.getRoles().clear();
    user.getRoles().add(role);

    Users saved = usersRepository.save(user);
    UsersDTO dto = usersMapper.userToUsersDTO(saved);

    return new ResponseEntity<>(dto, HttpStatus.CREATED);
}






}
