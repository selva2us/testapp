package com.supermarket.pos_backend.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CustomerCreditDTO {
    private Long id;
    private Long billId;
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private Double totalAmount;
    private Double paidAmount;
    private Double balanceAmount;
    private String status;
    private LocalDate createdAt;
    private List<Object> payments; // lightweight payment objects or map
}