package com.supermarket.pos_backend.controller;

import com.supermarket.pos_backend.dto.AddPaymentRequest;
import com.supermarket.pos_backend.dto.CreateCreditRequest;
import com.supermarket.pos_backend.model.CustomerCredit;
import com.supermarket.pos_backend.model.CreditStatus;
import com.supermarket.pos_backend.service.CreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/credits")
@RequiredArgsConstructor
public class CreditController {

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

    @GetMapping
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
}
