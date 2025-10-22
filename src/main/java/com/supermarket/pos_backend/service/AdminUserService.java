package com.supermarket.pos_backend.service;

import com.supermarket.pos_backend.model.AdminUser;
import com.supermarket.pos_backend.repository.AdminUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AdminUserService {

    private final AdminUserRepository adminRepo;
    private final PasswordEncoder passwordEncoder;

    public AdminUserService(AdminUserRepository adminRepo, PasswordEncoder passwordEncoder) {
        this.adminRepo = adminRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public AdminUser createAdmin(AdminUser admin) {
        String encodedPassword = passwordEncoder.encode(admin.getPassword());
        admin.setPassword(encodedPassword);
        return adminRepo.save(admin);
    }

    public List<AdminUser> getAllAdmins() {
        return adminRepo.findAll();
    }

    public AdminUser markAsPaid(Long adminId) {
        AdminUser admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        admin.setIsPaid(true);
        return adminRepo.save(admin);
    }

}
