package com.supermarket.pos_backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bills")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    @JsonBackReference("staff-bills") // unique reference name
    private StaffUser staff;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    @JsonBackReference("admin-bills") // matches AdminUser.products etc.
    private AdminUser admin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private LocalDateTime date;

    private Double totalAmount;

    private Double discountAmount;

    private Double finalAmount;

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("bill-items") // unique reference name
    private List<BillItem> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode; // new field

    // ✅ Add this new field
    @Column(unique = true, nullable = true)
    private String transactionId;

    // ✅ Optional readable bill number for printed receipts
    @Column(unique = true, nullable = true)
    private String billNumber;

    private LocalDateTime billDate = LocalDateTime.now();

    // convenience method
    public void addItem(BillItem item) {
        items.add(item);
        item.setBill(this);
    }

    public void removeItem(BillItem item) {
        items.remove(item);
        item.setBill(null);
    }
    @PrePersist
    public void prePersist() {
        date = LocalDateTime.now();
    }
}

