package com.supermarket.pos_backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bill_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private String productName;

    private Integer quantity;

    private Double price;      // unit price

    private Double totalPrice; // quantity * price

    @ManyToOne
    @JoinColumn(name = "bill_id")
    @JsonBackReference("bill-items") // matches Bill.items
    private Bill bill;

    private Integer returnedQuantity = 0;
}

