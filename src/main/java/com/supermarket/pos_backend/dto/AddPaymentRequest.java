package com.supermarket.pos_backend.dto;

import lombok.Data;

@Data
public class AddPaymentRequest {
    private Double amount;
    private String paymentMode; // CASH, UPI, CARD
}