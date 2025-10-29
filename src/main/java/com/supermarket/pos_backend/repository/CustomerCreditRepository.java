package com.supermarket.pos_backend.repository;

import com.supermarket.pos_backend.model.CustomerCredit;
import com.supermarket.pos_backend.model.CreditStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerCreditRepository extends JpaRepository<CustomerCredit, Long> {
    List<CustomerCredit> findByStatus(CreditStatus status);
    List<CustomerCredit> findByCustomerId(Long customerId);
}
