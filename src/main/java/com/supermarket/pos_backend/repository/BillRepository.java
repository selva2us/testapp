package com.supermarket.pos_backend.repository;

import com.supermarket.pos_backend.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Long> {
    // 1️⃣ Today's revenue
    @Query(value = "SELECT COALESCE(SUM(final_amount), 0) FROM bills WHERE DATE(billDate) = CURDATE()", nativeQuery = true)
    Double findTodayRevenue();

    // 2️⃣ Last 7 days' total revenue (per day)
    @Query(
            value = """
            SELECT COALESCE(SUM(final_amount), 0)
            FROM bills
            WHERE billDate >= :startDate
            GROUP BY DATE(billDate)
            ORDER BY DATE(billDate)
        """,
            nativeQuery = true
    )
    List<Double> getLast7DaysSales(LocalDateTime startDate);

    // 3️⃣ Total sales overall
    @Query(value = "SELECT COALESCE(SUM(final_amount), 0) FROM bills", nativeQuery = true)
    Double getTotalSales();
}