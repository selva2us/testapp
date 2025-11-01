package com.supermarket.pos_backend.controller;

import com.supermarket.pos_backend.annotations.CurrentAdmin;
import com.supermarket.pos_backend.annotations.CurrentStaff;
import com.supermarket.pos_backend.dto.AddPaymentRequest;
import com.supermarket.pos_backend.dto.CreateCreditRequest;
import com.supermarket.pos_backend.model.*;
import com.supermarket.pos_backend.security.SecurityUtils;
import com.supermarket.pos_backend.service.CreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CreditService creditService;

    @PostMapping("/create/{billId}")
    public ResponseEntity<CustomerCredit> createCredit(
            @PathVariable Long billId,
            @RequestBody CreateCreditRequest req
    ) {
        CustomerCredit created = creditService.createCreditFromBill(billId, req);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/{creditId}/pay")
    public ResponseEntity<CustomerCredit> payCredit(
            @PathVariable Long creditId,
            @RequestBody AddPaymentRequest req
    ) {
        CustomerCredit updated = creditService.addPayment(creditId, req);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/credits")
    public ResponseEntity<List<CustomerCredit>> getAllCredits(
            @RequestParam(value = "status", required = false) CreditStatus status
    ) {
        if (status == null) {
            return ResponseEntity.ok(creditService.getAllCredits());
        } else {
            return ResponseEntity.ok(creditService.getCreditsByStatus(status));
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<CustomerCredit>> getCreditsForCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(creditService.getCreditsByCustomer(customerId));
    }

    @GetMapping("/summary/outstanding")
    public ResponseEntity<List<Object[]>> outstandingSummary() {
        return ResponseEntity.ok(creditService.getOutstandingSummary());
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getCustomers(
            @CurrentAdmin(required = false) AdminUser admin,
            @CurrentStaff(required = false) StaffUser staff
    ) {
        Map<String, Long> ids = SecurityUtils.getCurrentUserId(admin, staff);
        Long adminId = ids.get("adminId");
        Long staffId = ids.get("staffId");

        return ResponseEntity.ok(creditService.getAllCustomers(adminId,staffId));
    }

    @GetMapping("/{customerId}/ledger")
    public ResponseEntity<?> getCustomerLedger(
            @PathVariable Long customerId
    ) {
        Map<String, Object> response = creditService.getCustomerLedger(customerId);
        if (response == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(response);
    }

}
