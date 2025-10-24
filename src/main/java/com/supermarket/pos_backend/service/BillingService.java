package com.supermarket.pos_backend.service;

import com.supermarket.pos_backend.dto.BillDTO;
import com.supermarket.pos_backend.dto.BillItemDTO;
import com.supermarket.pos_backend.dto.ReturnBillDTO;
import com.supermarket.pos_backend.model.*;
import com.supermarket.pos_backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BillingService {

    private final BillRepository billRepository;
    private final ProductRepository productRepository;
    private final AdminUserRepository adminRepository;
    private final StaffUserRepository staffRepository;

    public BillingService(
            BillRepository billRepository,
            ProductRepository productRepository,
            AdminUserRepository adminRepository,
            StaffUserRepository staffRepository
    ) {
        this.billRepository = billRepository;
        this.productRepository = productRepository;
        this.adminRepository = adminRepository;
        this.staffRepository = staffRepository;
    }

    // ✅ Create bill for either Admin or Staff
    @Transactional
    public Bill createBill(Long adminId, Long staffId, BillDTO billDTO) {
        AdminUser admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        Bill bill = getBill(billDTO);
        bill.setAdmin(admin);

        if (staffId != null) {
            StaffUser staff = staffRepository.findById(staffId)
                    .orElseThrow(() -> new RuntimeException("Staff not found"));
            bill.setStaff(staff);
        }

        for (BillItemDTO itemDTO : billDTO.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (product.getStockQuantity() < itemDTO.getQuantity()) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }

            product.setStockQuantity(product.getStockQuantity() - itemDTO.getQuantity());
            productRepository.save(product);

            BillItem item = new BillItem();
            item.setProductId(itemDTO.getProductId());
            item.setProductName(itemDTO.getProductName());
            item.setQuantity(itemDTO.getQuantity());
            item.setPrice(itemDTO.getPrice());
            item.setTotalPrice(itemDTO.getTotalPrice());
            bill.addItem(item);
        }

        return billRepository.save(bill);
    }

    // ✅ Admin sees all bills; Staff sees only their own
    public List<Bill> getBills(Long adminId, Long staffId) {
        if (staffId != null) {
            return billRepository.findByAdminIdAndStaffId(adminId, staffId);
        }
        return billRepository.findByAdminId(adminId);
    }

    // ✅ Return logic for both Admin and Staff
    @Transactional
    public Bill processReturn(Long adminId, Long staffId, ReturnBillDTO returnBillDTO) {
        Bill bill = billRepository.findByIdAndAdminId(returnBillDTO.getBillId(), adminId)
                .orElseThrow(() -> new RuntimeException("Bill not found for this admin"));

        // Optional: if staff, ensure the bill belongs to that staff
        if (staffId != null && bill.getStaff() != null && !bill.getStaff().getId().equals(staffId)) {
            throw new RuntimeException("This bill doesn't belong to the logged-in staff");
        }

        for (var returnItem : returnBillDTO.getItems()) {
            BillItem billItem = bill.getItems().stream()
                    .filter(i -> i.getProductId().equals(returnItem.getProductId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Item not found in bill"));

            int returnQty = returnItem.getQuantity();
            if (billItem.getReturnedQuantity() + returnQty > billItem.getQuantity()) {
                throw new RuntimeException("Return quantity exceeds purchased quantity");
            }

            // Update returned quantity
            billItem.setReturnedQuantity(billItem.getReturnedQuantity() + returnQty);

            // Restore stock
            Product product = productRepository.findById(billItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            product.setStockQuantity(product.getStockQuantity() + returnQty);
            productRepository.save(product);
        }

        return billRepository.save(bill);
    }

    // ✅ Common bill setup
    private static Bill getBill(BillDTO billDTO) {
        Bill bill = new Bill();
        bill.setCustomerName(billDTO.getCustomerName());
        bill.setCustomerPhone(billDTO.getCustomerPhone());
        bill.setTotalAmount(billDTO.getTotalAmount());
        bill.setDiscountAmount(billDTO.getDiscountAmount());
        bill.setFinalAmount(billDTO.getFinalAmount());
        bill.setPaymentMode(billDTO.getPaymentMode());
        bill.setBillNumber(billDTO.getBillNumber());
        bill.setTransactionId(billDTO.getTransactionId());
        return bill;
    }
}
