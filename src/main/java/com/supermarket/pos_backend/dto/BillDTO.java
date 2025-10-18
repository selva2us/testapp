package com.supermarket.pos_backend.dto;

import com.supermarket.pos_backend.model.PaymentMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillDTO {
    private Long staffId;
    private String customerName;
    private String customerPhone;
    private Double totalAmount;
    private Double discountAmount;
    private Double finalAmount;
    private PaymentMode paymentMode;
    private List<BillItemDTO> items;
    private String billNumber;
    private String transactionId;

}
