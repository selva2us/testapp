package com.supermarket.pos_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillDTO {

    private Long id;
    private String billNumber;
    private String transactionId;
    private String customerName;
    private String customerPhone;
    private Double totalAmount;
    private Double discountAmount;
    private Double finalAmount;
    private String staffName;       // staff user name
    private String adminName;       // admin user name
    private List<BillItemDTO> items;
    private String paymentMode;
    private LocalDateTime billDate;

    // helper method to convert from Bill entity
    public static BillDTO fromEntity(com.supermarket.pos_backend.model.Bill bill) {
        BillDTO dto = new BillDTO();
        dto.setId(bill.getId());
        dto.setBillNumber(bill.getBillNumber());
        dto.setTransactionId(bill.getTransactionId());
        dto.setTotalAmount(bill.getTotalAmount());
        dto.setDiscountAmount(bill.getDiscountAmount());
        dto.setFinalAmount(bill.getFinalAmount());
        dto.setPaymentMode(bill.getPaymentMode() != null ? bill.getPaymentMode().name() : null);
        dto.setBillDate(bill.getBillDate() != null ? bill.getBillDate() : bill.getDate());

        // set staff and admin names
        dto.setStaffName(bill.getStaff() != null ? bill.getStaff().getName() : null);
        dto.setAdminName(bill.getAdmin() != null ? bill.getAdmin().getName() : null);

        // map items
        if (bill.getItems() != null) {
            dto.setItems(bill.getItems().stream()
                    .map(BillItemDTO::fromEntity)
                    .toList());
        }

        return dto;
    }
}
