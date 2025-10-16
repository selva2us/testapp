package com.supermarket.pos_backend.repository;

import com.supermarket.pos_backend.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillRepository extends JpaRepository<Bill, Long> {
}