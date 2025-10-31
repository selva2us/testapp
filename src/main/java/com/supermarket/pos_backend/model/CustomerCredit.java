package com.supermarket.pos_backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customer_credits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCredit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Each credit is linked to exactly one bill
    @OneToOne
    @JoinColumn(name = "bill_id", unique = true)
    private Bill bill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    @JsonBackReference("customer-credits") // Matches Customer.credits
    private Customer customer;

    private Double totalAmount;
    private Double paidAmount = 0.0;
    private Double balanceAmount;

    @Enumerated(EnumType.STRING)
    private CreditStatus status = CreditStatus.ACTIVE;

    private LocalDate createdAt = LocalDate.now();

    @OneToMany(mappedBy = "credit", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("credit-payments") // Matches CreditPayment.credit
    private List<CreditPayment> payments = new ArrayList<>();

    public void addPayment(CreditPayment p) {
        payments.add(p);
        p.setCredit(this);
    }
}
