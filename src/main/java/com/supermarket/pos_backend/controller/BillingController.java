package com.supermarket.pos_backend.controller;

import com.supermarket.pos_backend.annotations.CurrentAdmin;
import com.supermarket.pos_backend.annotations.CurrentStaff;
import com.supermarket.pos_backend.dto.BillDTO;
import com.supermarket.pos_backend.dto.ReturnBillDTO;
import com.supermarket.pos_backend.model.AdminUser;
import com.supermarket.pos_backend.model.Bill;
import com.supermarket.pos_backend.model.StaffUser;
import com.supermarket.pos_backend.service.BillingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/bills")
@Tag(name = "Billing", description = "Shop management APIs")
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @PostMapping
    public ResponseEntity<Bill> createBill(
            @CurrentAdmin(required = false) AdminUser admin,
            @CurrentStaff(required = false) StaffUser staff,
            @RequestBody BillDTO billDTO) {

        Long adminId;
        Long staffId = null;

        if (admin != null) {
            adminId = admin.getId();
        } else if (staff != null) {
            adminId = staff.getAdmin().getId();
            staffId = staff.getId();
        } else {
            throw new RuntimeException("Unauthorized user");
        }

        Bill savedBill = billingService.createBill(adminId, staffId, billDTO);
        return ResponseEntity.ok(savedBill);
    }

    @GetMapping
    public ResponseEntity<List<Bill>> getAllBills(
            @CurrentAdmin(required = false) AdminUser admin,
            @CurrentStaff(required = false) StaffUser staff) {

        Long adminId;
        Long staffId = null;

        if (admin != null) {
            adminId = admin.getId();
        } else if (staff != null) {
            adminId = staff.getAdmin().getId();
            staffId = staff.getId();
        } else {
            throw new RuntimeException("Unauthorized user");
        }

        List<Bill> bills = billingService.getBills(adminId, staffId);
        return ResponseEntity.ok(bills);
    }

    @PostMapping("/return")
    public ResponseEntity<Bill> returnBill(
            @CurrentAdmin(required = false) AdminUser admin,
            @CurrentStaff(required = false) StaffUser staff,
            @RequestBody ReturnBillDTO returnBillDTO) {

        Long adminId;
        Long staffId = null;

        if (admin != null) {
            adminId = admin.getId();
        } else if (staff != null) {
            adminId = staff.getAdmin().getId();
            staffId = staff.getId();
        } else {
            throw new RuntimeException("Unauthorized user");
        }

        Bill updatedBill = billingService.processReturn(adminId, staffId, returnBillDTO);
        return ResponseEntity.ok(updatedBill);
    }
}
