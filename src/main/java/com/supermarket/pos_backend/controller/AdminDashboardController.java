package com.supermarket.pos_backend.controller;

import com.supermarket.pos_backend.annotations.CurrentAdmin;
import com.supermarket.pos_backend.annotations.CurrentStaff;
import com.supermarket.pos_backend.dto.DashboardStatsDTO;
import com.supermarket.pos_backend.model.AdminUser;
import com.supermarket.pos_backend.model.StaffUser;
import com.supermarket.pos_backend.security.SecurityUtils;
import com.supermarket.pos_backend.service.AdminDashboardService;
import com.supermarket.pos_backend.service.ImageCleanupService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "AdminDashboard", description = "Shop management APIs")
@CrossOrigin(origins = "*")
public class AdminDashboardController {

    @Autowired
    private AdminDashboardService dashboardService;

    @Autowired
    private  ImageCleanupService cleanupService;


    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats( @CurrentAdmin(required = false) AdminUser admin,
                                                                @CurrentStaff(required = false) StaffUser staff) {
        if (admin != null && staff != null)  {
            DashboardStatsDTO stats = dashboardService.getDashboardStats(staff.getAdmin(), staff);
            return ResponseEntity.ok(stats);
        } else if(admin != null) {
            DashboardStatsDTO stats = dashboardService.getDashboardStats(admin,null);
            return ResponseEntity.ok(stats);
        } else if (staff != null) {
            DashboardStatsDTO stats = dashboardService.getDashboardStats(staff.getAdmin(), staff);
            return ResponseEntity.ok(stats);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        }
    }

    @PostMapping("/cleanup")
    public String runCleanup() {
        return cleanupService.deleteAllFiles();
    }
}
