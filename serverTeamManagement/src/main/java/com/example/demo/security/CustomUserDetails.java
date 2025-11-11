package com.example.demo.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Collection;

public class CustomUserDetails extends User {
    private String roleString; // נוסיף משתנה לשמירת התפקיד הראשי

    public CustomUserDetails(String email, String password, Collection<? extends GrantedAuthority> authorities) {
        super(email, password, authorities);
        // נגדיר את התפקיד הראשון (אם יש) כבר כאן
        if (authorities != null && !authorities.isEmpty()) {
            this.roleString = authorities.iterator().next().getAuthority();
        }
    }

    // ✅ נוסיף getter
    public String getRoleString() {
        return this.roleString;
    }

    // אופציונלי - אם נרצה לשנות אותו מבחוץ
    public void setRoleString(String roleString) {
        this.roleString = roleString;
    }
}
