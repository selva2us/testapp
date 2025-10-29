package com.supermarket.pos_backend.service;

import com.supermarket.pos_backend.dto.StaffUserRequest;
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

    public StaffUser updateStaff(Long adminId, Long staffId, StaffUserRequest staffRequest) {
        // Find the existing staff
        StaffUser staff = staffRepo.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        // Ensure the staff belongs to the same admin
        if (!staff.getAdmin().getId().equals(adminId)) {
            throw new RuntimeException("Access denied: You can update only your staff");
        }

        // Update only allowed fields
        staff.setName(staffRequest.getName());
        staff.setEmail(staffRequest.getEmail());
        staff.setMobile(staffRequest.getMobile());
        staff.setAddress(staffRequest.getAddress());
        staff.setRole(staffRequest.getRole());
        staff.setActive(staffRequest.getActive());
        staff.setImageUrl(staffRequest.getImageUrl());
        staff.setIdDetails(staffRequest.getIdDetails());
        staff.setAccountNumber(staffRequest.getAccountNumber());

        // Optional: update password if provided
        if (staffRequest.getPassword() != null && !staffRequest.getPassword().isBlank()) {
            staff.setPassword(staffRequest.getPassword());
        }

        return staffRepo.save(staff);
    }

}