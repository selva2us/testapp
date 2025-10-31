package com.supermarket.pos_backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "credit_payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_id")
    @JsonBackReference("credit-payments") // ✅ prevents infinite loop with CustomerCredit
    private CustomerCredit credit;

    private Double amount;

    private String paymentMode; // CASH, UPI, CARD, etc.

    private LocalDateTime paidAt = LocalDateTime.now();
}
