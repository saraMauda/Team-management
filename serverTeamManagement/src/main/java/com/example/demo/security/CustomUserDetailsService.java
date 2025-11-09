package com.example.demo.security;

import com.example.demo.model.Role;
import com.example.demo.model.Users;
import com.example.demo.service.UsersRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    UsersRepository userRepository;



    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        //לאמת את המשתמש עם המשתמש שנמצא ב-DB
        Users user=userRepository.findByEmail(email);
        if (user==null)
            throw new UsernameNotFoundException("user not found");
        //רשימה של הרשאות
        List<GrantedAuthority> grantedAuthorities=new ArrayList<>();
        for(Role role:user.getRoles())
        {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getName().name()));
        }
        return new CustomUserDetails(email,user.getPassword(),grantedAuthorities);//יוצר משתמש עבור האבטחה
    }
}
