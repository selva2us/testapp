package com.supermarket.pos_backend.security;

import com.supermarket.pos_backend.model.AdminUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static String getLoggedInEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AdminUser admin = (AdminUser) auth.getPrincipal();
        if (auth != null) {
            return auth.getName(); // email from JWT
        }
        return null;
    }
}
