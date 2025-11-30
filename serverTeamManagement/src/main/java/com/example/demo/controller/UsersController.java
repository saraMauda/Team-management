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
    @PreAuthorize("hasRole('ADMIN')") // אם רק אדמין יכול לעדכן משתמשים
    public ResponseEntity<UsersDTO> updateUser(@PathVariable Long id,
                                               @RequestBody UsersDTO userDTO) {
        return usersRepository.findById(id)
                .map(existing -> {
                    existing.setName(userDTO.getName());
                    existing.setEmail(userDTO.getEmail());
                    existing.setActive(userDTO.isActive());

                    // ✅ עדכון ROLE
                    if (userDTO.getRole() != null && !userDTO.getRole().isEmpty()) {
                        // מצפה ש־userDTO.getRole() יחזיר "ROLE_EMPLOYEE" / "ROLE_TEAMLEADER" / "ROLE_ADMIN"
                        ERole eRole = ERole.valueOf(userDTO.getRole());
                        Role role = roleRepository.findByName(eRole)
                                .orElseThrow(() -> new RuntimeException("Role not found: " + userDTO.getRole()));

                        existing.getRoles().clear();
                        existing.getRoles().add(role);
                    }

                    if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
                        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                        existing.setPassword(encoder.encode(userDTO.getPassword()));
                    }

                    Users saved = usersRepository.save(existing);
                    return ResponseEntity.ok(usersMapper.userToUsersDTO(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }


    //מחיקת משתמש (רק Admin רשאי)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        return usersRepository.findById(id)
                .map(user -> {
                    user.setActive(false);      // soft delete
                    usersRepository.save(user);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().<Void>build());
    }



    @PostMapping("/upload/{id}")
    public ResponseEntity<String> uploadImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile file) throws IOException {

        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ImageUtils.uploadImage(file);
        user.setImagePath(file.getOriginalFilename());
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

        SecurityContextHolder.getContext().setAuthentication(authentication);
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
        return ResponseEntity.ok(true);
    }
    @GetMapping("/by-email/{email}")
    public UsersDTO getUserByEmail(@PathVariable String email) {
        Users user = usersRepository.findByEmail(email);
        return usersMapper.userToUsersDTO(user);
    }
    @PutMapping("/change-password/{id}")
    public ResponseEntity<?> changePassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        Optional<Users> opt = usersRepository.findById(id);
        if (opt.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");

        Users user = opt.get();

        String oldPass = body.get("oldPassword");
        String newPass = body.get("newPassword");

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (!encoder.matches(oldPass, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Old password is incorrect");
        }

        user.setPassword(encoder.encode(newPass));
        usersRepository.save(user);

        ResponseCookie cleared = jwtUtils.getCleanJwtCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cleared.toString())
                .body("Password updated successfully");
    }









}
