package com.supermarket.pos_backend.service;

import com.supermarket.pos_backend.dto.BillDTO;
import com.supermarket.pos_backend.dto.BillItemDTO;
import com.supermarket.pos_backend.dto.ReturnBillDTO;
import com.supermarket.pos_backend.model.Bill;
import com.supermarket.pos_backend.model.BillItem;
import com.supermarket.pos_backend.model.Product;
import com.supermarket.pos_backend.repository.BillRepository;
import com.supermarket.pos_backend.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BillingService {

    private final BillRepository billRepository;
    private final ProductRepository productRepository;

    public BillingService(BillRepository billRepository, ProductRepository productRepository) {
        this.billRepository = billRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Bill createBill(BillDTO billDTO) {
        Bill bill = getBill(billDTO);

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

    @Transactional
    public Bill processReturn(ReturnBillDTO returnBillDTO) {
        Bill bill = billRepository.findById(returnBillDTO.getBillId())
                .orElseThrow(() -> new RuntimeException("Original bill not found"));

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

        // Optionally: adjust finalAmount of bill or create separate return record
        return billRepository.save(bill);
    }

    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }

    private static Bill getBill(BillDTO billDTO) {
        Bill bill = new Bill();
        bill.setStaffId(billDTO.getStaffId());
        bill.setCustomerName(billDTO.getCustomerName());
        bill.setCustomerPhone(billDTO.getCustomerPhone());
        bill.setTotalAmount(billDTO.getTotalAmount());
        bill.setDiscountAmount(billDTO.getDiscountAmount());
        bill.setFinalAmount(billDTO.getFinalAmount());
        bill.setPaymentMode(billDTO.getPaymentMode()); // set payment mode
        bill.setBillNumber(billDTO.getBillNumber());
        bill.setTransactionId(billDTO.getTransactionId());
        return bill;
    }
}
