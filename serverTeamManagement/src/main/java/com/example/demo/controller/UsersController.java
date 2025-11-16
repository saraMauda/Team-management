package com.example.demo.controller;

import com.example.demo.dto.UsersDTO;
import com.example.demo.model.ERole;
import com.example.demo.model.Role;
import com.example.demo.model.Users;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.security.jwt.JwtUtils;
import com.example.demo.service.ImageUtils;
import com.example.demo.service.RoleRepository;
import com.example.demo.service.UsersMapper;
import com.example.demo.service.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    @Autowired
    private UsersRepository usersRepository;
    private RoleRepository roleRepository;
    private AuthenticationManager authenticationManager;
    private JwtUtils jwtUtils;

    @Autowired
    private UsersMapper usersMapper;

    public UsersController(UsersRepository usersRepository, RoleRepository roleRepository,AuthenticationManager authenticationManager,JwtUtils jwtUtils) {
        this.usersRepository = usersRepository;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
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
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody Users u){
        Authentication authentication=authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(u.getEmail(),u.getPassword()));

        //שומר את האימות
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //CustomUserDetails לוקח את פרטי המשתמש ומכניס אותם
        CustomUserDetails userDetails=(CustomUserDetails)authentication.getPrincipal();

        ResponseCookie jwtCookie=jwtUtils.generateJwtCookie(userDetails);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("email", userDetails.getUsername());
        responseBody.put("role", userDetails.getRoleString());
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,jwtCookie.toString())
                .body(responseBody);
    }
    @PostMapping("/signout")
    public ResponseEntity<?> signOut(){
        ResponseCookie cookie=jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,cookie.toString())
                .body("you've been signed out!");
    }
    @GetMapping("/authenticated")
    public ResponseEntity<?> isAuthenticated() {
        // אם הגענו לכאן – המשמעות היא שה־Cookie תקף
        return ResponseEntity.ok(true);
    }
    @GetMapping("/by-email/{email}")
    public UsersDTO getUserByEmail(@PathVariable String email) {
        Users user = usersRepository.findByEmail(email);
        return usersMapper.userToUsersDTO(user);
    }








}
