package com.supermarket.pos_backend.service;

import com.supermarket.pos_backend.model.AdminUser;
import com.supermarket.pos_backend.model.StaffUser;
import com.supermarket.pos_backend.repository.AdminUserRepository;
import com.supermarket.pos_backend.repository.StaffUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffUserService {

    private final StaffUserRepository staffRepo;
    private final AdminUserRepository adminRepo;
    private final PasswordEncoder passwordEncoder;

    public StaffUserService(StaffUserRepository staffRepo, AdminUserRepository adminRepo, PasswordEncoder passwordEncoder) {
        this.staffRepo = staffRepo;
        this.adminRepo = adminRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public StaffUser addStaff(Long adminId, StaffUser staff) {
        AdminUser admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        // ✅ Check for duplicates
        staffRepo.findByEmail(staff.getEmail()).ifPresent(s -> {
            throw new RuntimeException("Email already in use");
        });
        staffRepo.findByMobile(staff.getMobile()).ifPresent(s -> {
            throw new RuntimeException("Mobile number already in use");
        });
        String encodedPassword = passwordEncoder.encode(staff.getPassword());
        staff.setPassword(encodedPassword);
        staff.setAdmin(admin);
        return staffRepo.save(staff);
    }

    public List<StaffUser> getStaffByAdmin(Long adminId) {
        return staffRepo.findByAdminId(adminId);
    }

    public void deleteStaff(Long id) {
        staffRepo.deleteById(id);
    }
}