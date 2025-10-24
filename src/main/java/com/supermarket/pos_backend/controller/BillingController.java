package com.supermarket.pos_backend.controller;

import com.supermarket.pos_backend.annotations.CurrentAdmin;
import com.supermarket.pos_backend.dto.BillDTO;
import com.supermarket.pos_backend.dto.ReturnBillDTO;
import com.supermarket.pos_backend.model.AdminUser;
import com.supermarket.pos_backend.model.Bill;
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
    public ResponseEntity<Bill> createBill(@CurrentAdmin AdminUser admin,@RequestBody BillDTO billDTO) {
        Bill savedBill = billingService.createBill(admin,billDTO);
        return ResponseEntity.ok(savedBill);
    }

    @GetMapping
    public ResponseEntity<List<Bill>> getAllBills(@CurrentAdmin AdminUser admin) {
        List<Bill> bills = billingService.getAllBills(admin);
        return ResponseEntity.ok(bills);
    }
    @PostMapping("/return")
    public ResponseEntity<Bill> returnBill(@CurrentAdmin AdminUser admin,@RequestBody ReturnBillDTO returnBillDTO) {
        Bill updatedBill = billingService.processReturn(admin,returnBillDTO);
        return ResponseEntity.ok(updatedBill);
    }

}
