package com.supermarket.pos_backend.service;

import com.supermarket.pos_backend.dto.AddPaymentRequest;
import com.supermarket.pos_backend.dto.CreateCreditRequest;
import com.supermarket.pos_backend.model.*;
import com.supermarket.pos_backend.repository.*;
import com.supermarket.pos_backend.service.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreditService {

    private final CustomerCreditRepository creditRepo;
    private final CreditPaymentRepository paymentRepo;
    private final BillRepository billRepo;
    private final CustomerRepository customerRepo;

    @Transactional
    public CustomerCredit createCreditFromBill(Long billId, CreateCreditRequest req) {
        Bill bill = billRepo.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found: " + billId));

        Customer customer = customerRepo.findByPhone(req.getCustomerPhone())
                .orElseGet(() -> {
                    Customer c = new Customer();
                    c.setName(req.getCustomerName());
                    c.setPhone(req.getCustomerPhone());
                    return customerRepo.save(c);
                });

        // attach customer to bill (optional — useful for querying)
        bill.setCustomer(customer);
        billRepo.save(bill);

        // If a credit already exists for this bill, return it
        List<CustomerCredit> existing = creditRepo.findAll()
                .stream()
                .filter(cc -> cc.getBill() != null && cc.getBill().getId().equals(billId))
                .collect(Collectors.toList());

        if (!existing.isEmpty()) return existing.get(0);

        CustomerCredit credit = new CustomerCredit();
        credit.setBill(bill);
        credit.setCustomer(customer);
        credit.setTotalAmount(bill.getFinalAmount());
        credit.setPaidAmount(0.0);
        credit.setBalanceAmount(bill.getFinalAmount());
        credit.setStatus(CreditStatus.ACTIVE);

        return creditRepo.save(credit);
    }

    @Transactional
    public CustomerCredit addPayment(Long creditId, AddPaymentRequest req) {
        CustomerCredit credit = creditRepo.findById(creditId)
                .orElseThrow(() -> new ResourceNotFoundException("Credit not found: " + creditId));

        CreditPayment payment = new CreditPayment();
        payment.setCredit(credit);
        payment.setAmount(req.getAmount());
        payment.setPaymentMode(req.getPaymentMode());
        paymentRepo.save(payment);

        // update aggregate
        double newPaid = (credit.getPaidAmount() == null ? 0.0 : credit.getPaidAmount()) + req.getAmount();
        credit.setPaidAmount(newPaid);
        credit.setBalanceAmount(credit.getTotalAmount() - newPaid);

        if (credit.getBalanceAmount() <= 0) {
            credit.setStatus(CreditStatus.PAID);
            credit.setBalanceAmount(0.0);
        }

        return creditRepo.save(credit);
    }

    @Transactional(readOnly = true)
    public List<CustomerCredit> getAllCredits() {
        return creditRepo.findAll();
    }

    @Transactional(readOnly = true)
    public List<CustomerCredit> getCreditsByStatus(CreditStatus status) {
        return creditRepo.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<CustomerCredit> getCreditsByCustomer(Long customerId) {
        return creditRepo.findByCustomerId(customerId);
    }

    // optional: summary per customer
    @Transactional(readOnly = true)
    public List<Object[]> getOutstandingSummary() {
        // return name, phone, sum(balance), count
        return creditRepo.findAll().stream()
                .filter(cc -> cc.getStatus() == CreditStatus.ACTIVE)
                .collect(Collectors.groupingBy(cc -> cc.getCustomer()))
                .entrySet()
                .stream()
                .map(e -> {
                    Customer c = e.getKey();
                    double totalDue = e.getValue().stream().mapToDouble(CustomerCredit::getBalanceAmount).sum();
                    int count = e.getValue().size();
                    return new Object[]{c.getName(), c.getPhone(), totalDue, count};
                })
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getAllCustomers(Long adminId, Long staffId) {

        // Get all bills under this admin (and staff if applicable)
        List<Bill> bills = (staffId != null)
                ? billRepo.findByAdminIdAndStaffId(adminId, staffId)
                : billRepo.findByAdminId(adminId);

        // Group bills by customer
        Map<Customer, List<Bill>> grouped = bills.stream()
                .filter(b -> b.getCustomer() != null)
                .collect(Collectors.groupingBy(Bill::getCustomer));

        List<Map<String, Object>> list = new ArrayList<>();

        for (Map.Entry<Customer, List<Bill>> entry : grouped.entrySet()) {
            Customer customer = entry.getKey();
            List<Bill> customerBills = entry.getValue();

            // 🔹 Filter only "Pay Later" or "Credit" bills
            List<Bill> creditBills = customerBills.stream()
                    .filter(b -> "CREDIT".equalsIgnoreCase(b.getPaymentMode().toString()))
                    .toList();

            // 🔹 If customer has no credit bills, skip
            if (creditBills.isEmpty()) continue;

            // 🔹 Total bill amount for credit bills
            double totalSpent = creditBills.stream()
                    .mapToDouble(b -> b.getFinalAmount() != null ? b.getFinalAmount() : 0)
                    .sum();

            // 🔹 Total paid = all CreditPayment entries linked to customer's credits
            double totalPaid = customer.getCredits().stream()
                    .flatMap(c -> c.getPayments().stream())
                    .mapToDouble(CreditPayment::getAmount)
                    .sum();

            // 🔹 Remaining balance
            double balance = totalSpent - totalPaid;

            // 🔹 Get latest bill date among credit bills
            LocalDateTime lastBillDate = creditBills.stream()
                    .map(Bill::getDate)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);

            Map<String, Object> dto = new LinkedHashMap<>();
            dto.put("id", customer.getId());
            dto.put("name", customer.getName());
            dto.put("phone", customer.getPhone());
            dto.put("totalSpent", totalSpent);
            dto.put("lastBillDate", lastBillDate);
            dto.put("balance", balance);

            list.add(dto);
        }

        return list;
    }

    public Map<String, Object> getCustomerLedger(Long customerId) {
        Optional<Customer> optionalCustomer = customerRepo.findById(customerId);
        if (optionalCustomer.isEmpty()) return null;

        Customer customer = optionalCustomer.get();
        List<Map<String, Object>> ledgerList = new ArrayList<>();

        double totalOutstanding = 0.0;

        for (CustomerCredit credit : customer.getCredits()) {
            Bill bill = credit.getBill();
            if (bill == null) continue;

            double billAmount = bill.getFinalAmount() != null ? bill.getFinalAmount() : 0.0;
            double totalPaid = 0.0;
            List<Map<String, Object>> payments = new ArrayList<>();

            // Add payments under this credit
            for (CreditPayment payment : credit.getPayments()) {
                totalPaid += payment.getAmount();
                payments.add(Map.of(
                        "date", payment.getPaidAt(),
                        "amount", payment.getAmount()
                ));
            }

            double balance = billAmount - totalPaid;
            totalOutstanding += balance;

            Map<String, Object> ledgerEntry = new LinkedHashMap<>();
            ledgerEntry.put("billNumber", bill.getBillNumber());
            ledgerEntry.put("billDate", bill.getBillDate());
            ledgerEntry.put("billAmount", billAmount);
            ledgerEntry.put("payments", payments);
            ledgerEntry.put("totalPaid", totalPaid);
            ledgerEntry.put("balance", balance);

            ledgerList.add(ledgerEntry);
        }

        return Map.of(
                "customer", Map.of(
                        "id", customer.getId(),
                        "name", customer.getName(),
                        "phone", customer.getPhone()
                ),
                "ledger", ledgerList,
                "totalOutstanding", totalOutstanding
        );
    }
}