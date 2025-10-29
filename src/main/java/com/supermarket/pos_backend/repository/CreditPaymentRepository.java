package com.supermarket.pos_backend.repository;


import com.supermarket.pos_backend.model.CreditPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditPaymentRepository extends JpaRepository<CreditPayment, Long> {}