package com.supermarket.pos_backend.security;

import com.supermarket.pos_backend.model.AdminUser;

public class AdminContext {
    private static final ThreadLocal<AdminUser> currentUser= new ThreadLocal<>();

    public static void setCurrentUser(AdminUser adminUser) {
        currentUser.set(adminUser);
    }

    public static AdminUser getCurrentUser() {
        return currentUser.get();
    }

    public static void clear() {
        currentUser.remove();
    }
}
