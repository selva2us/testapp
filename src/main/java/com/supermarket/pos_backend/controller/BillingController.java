package com.supermarket.pos_backend.controller;

import com.supermarket.pos_backend.annotations.CurrentAdmin;
import com.supermarket.pos_backend.annotations.CurrentStaff;
import com.supermarket.pos_backend.dto.BillDTO;
import com.supermarket.pos_backend.dto.ReturnBillDTO;
import com.supermarket.pos_backend.model.AdminUser;
import com.supermarket.pos_backend.model.Bill;
import com.supermarket.pos_backend.model.StaffUser;
import com.supermarket.pos_backend.security.SecurityUtils;
import com.supermarket.pos_backend.service.BillingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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

        Map<String, Long> ids = SecurityUtils.getCurrentUserId(admin, staff);
        Long adminId = ids.get("adminId");
        Long staffId = ids.get("staffId");

        Bill savedBill = billingService.createBill(adminId, staffId, billDTO);
        return ResponseEntity.ok(savedBill);
    }

    @GetMapping
    public ResponseEntity<List<BillDTO>> getAllBills(
            @CurrentAdmin(required = false) AdminUser admin,
            @CurrentStaff(required = false) StaffUser staff) {
        Map<String, Long> ids = SecurityUtils.getCurrentUserId(admin, staff);
        Long adminId = ids.get("adminId");
        Long staffId = ids.get("staffId");

        List<Bill> bills = billingService.getBills(adminId, staffId);
        return ResponseEntity.ok(convertToDTO(bills));
    }

    @PostMapping("/return")
    public ResponseEntity<Bill> returnBill(
            @CurrentAdmin(required = false) AdminUser admin,
            @CurrentStaff(required = false) StaffUser staff,
            @RequestBody ReturnBillDTO returnBillDTO) {

        Map<String, Long> ids = SecurityUtils.getCurrentUserId(admin, staff);
        Long adminId = ids.get("adminId");
        Long staffId = ids.get("staffId");

        Bill updatedBill = billingService.processReturn(adminId, staffId, returnBillDTO);
        return ResponseEntity.ok(updatedBill);
    }

    private List<BillDTO> convertToDTO(List<Bill> bills){
        return bills.stream()
                .map(BillDTO::fromEntity)
                .toList();
    }
}
