package com.supermarket.pos_backend.controller;

import com.supermarket.pos_backend.annotations.CurrentAdmin;
import com.supermarket.pos_backend.annotations.CurrentStaff;
import com.supermarket.pos_backend.dto.StaffUserRequest;
import com.supermarket.pos_backend.model.AdminUser;
import com.supermarket.pos_backend.model.StaffUser;
import com.supermarket.pos_backend.repository.AdminUserRepository;
import com.supermarket.pos_backend.service.AdminUserService;
import com.supermarket.pos_backend.service.StaffUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admins")
@Tag(name = "AdminUsers",description = "Shop management APIs")
public class AdminUserController {

    private final AdminUserService adminService;
    private final StaffUserService staffService;
    private final AdminUserRepository adminRepo;

    public AdminUserController(AdminUserService adminService, StaffUserService staffService, AdminUserRepository adminRepo) {
        this.adminService = adminService;
        this.staffService = staffService;
        this.adminRepo = adminRepo;
    }

    @PostMapping("/register")
    public AdminUser createAdmin(@Valid @RequestBody AdminUser admin) {
        return adminService.createAdmin(admin);
    }

    @GetMapping
    public List<AdminUser> getAllAdmins() {
        return adminService.getAllAdmins();
    }

    @PutMapping("/{id}/mark-paid")
    public AdminUser markAdminAsPaid(@PathVariable Long id) {
        return adminService.markAsPaid(id);
    }

    // ✅ Add new staff under a specific admin
    @PostMapping("/add")
    public ResponseEntity<?> addStaff(@CurrentAdmin AdminUser admin, @Valid @RequestBody StaffUserRequest staffRequest){
             // Create staff entity
        StaffUser staff = new StaffUser();
        staff.setName(staffRequest.getName());
        staff.setEmail(staffRequest.getEmail());
        staff.setMobile(staffRequest.getMobile());
        staff.setPassword(staffRequest.getPassword());
        staff.setRole(staffRequest.getRole());
        staff.setAddress(staffRequest.getAddress());
        staff.setIdDetails(staffRequest.getIdDetails());
        staff.setAccountNumber(staffRequest.getAccountNumber());
        staff.setImageUrl(staffRequest.getImageUrl());

        StaffUser savedStaff = staffService.addStaff(admin.getId(), staff);
        return ResponseEntity.ok(savedStaff);
    }

    @PutMapping("/staff/{id}")
    public ResponseEntity<?> updateStaff(
            @CurrentAdmin AdminUser admin,
            @PathVariable Long id,
            @Valid @RequestBody StaffUserRequest staffRequest) {

        StaffUser updatedStaff = staffService.updateStaff(admin.getId(), id, staffRequest);
        return ResponseEntity.ok(updatedStaff);
    }

    // ✅ Delete a staff user
    @DeleteMapping("/staff/{id}")
    public ResponseEntity<Map<String, String>> deleteStaff(@PathVariable Long id) {
        staffService.deleteStaff(id);
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Staff deleted successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getLoggedInAdmin(@CurrentAdmin AdminUser admin){
        return ResponseEntity.ok(admin);
    }

    @GetMapping("/my-staff")
    public ResponseEntity<?> getStaffForLoggedInAdmin(@CurrentAdmin AdminUser admin) {
        return ResponseEntity.ok(staffService.getStaffByAdmin(admin.getId()));
    }

    @GetMapping("/staff/me")
    public ResponseEntity<StaffUser> getMyProfile(@CurrentStaff StaffUser staff) {
        return ResponseEntity.ok(staff);
    }
}
