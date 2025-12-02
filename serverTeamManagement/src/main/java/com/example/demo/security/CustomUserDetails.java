package com.example.demo.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Collection;

public class CustomUserDetails extends User {
    private String roleString;

    public CustomUserDetails(String email, String password, Collection<? extends GrantedAuthority> authorities) {
        super(email, password, authorities);
        if (authorities != null && !authorities.isEmpty()) {
            this.roleString = authorities.iterator().next().getAuthority();
        }
    }

    public String getRoleString() {
        return this.roleString;
    }

    public void setRoleString(String roleString) {
        this.roleString = roleString;
    }

}
