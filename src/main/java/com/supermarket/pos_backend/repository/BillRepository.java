package com.supermarket.pos_backend.repository;

import com.supermarket.pos_backend.model.AdminUser;
import com.supermarket.pos_backend.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {
    // 1️⃣ Today's revenue
    @Query(value = "SELECT COALESCE(SUM(final_amount), 0) FROM bills WHERE DATE(bill_date) = CURRENT_DATE", nativeQuery = true)
    Double findTodayRevenue();

    // 2️⃣ Last 7 days' total revenue (per day)
    @Query(
            value = """
            SELECT COALESCE(SUM(final_amount), 0)
            FROM bills
            WHERE bill_date >= :startDate
            GROUP BY DATE(bill_date)
            ORDER BY DATE(bill_date)
        """,
            nativeQuery = true
    )
    List<Double> getLast7DaysSales(LocalDateTime startDate);

    // 3️⃣ Total sales overall
    @Query(value = "SELECT COALESCE(SUM(final_amount), 0) FROM bills", nativeQuery = true)
    Double getTotalSales();

    List<Bill> findByAdmin(AdminUser admin);
    Optional<Bill> findByIdAndAdmin(Long id, AdminUser admin);

    @Query(value = "SELECT COALESCE(SUM(final_amount), 0) FROM bills WHERE DATE(bill_date) = CURRENT_DATE AND admin_id = :adminId", nativeQuery = true)
    Double findTodayRevenueByAdmin(@Param("adminId") Long adminId);

    // 2️⃣ Last 7 days' total revenue (per day) for a specific admin
    @Query(
            value = """
        SELECT COALESCE(SUM(final_amount), 0)
        FROM bills
        WHERE bill_date >= :startDate AND admin_id = :adminId
        GROUP BY DATE(bill_date)
        ORDER BY DATE(bill_date)
        """,
            nativeQuery = true
    )
    List<Double> getLast7DaysSalesByAdmin(@Param("adminId") Long adminId, @Param("startDate") LocalDateTime startDate);

    // 3️⃣ Total sales overall for a specific admin
    @Query(value = "SELECT COALESCE(SUM(final_amount), 0) FROM bills WHERE admin_id = :adminId", nativeQuery = true)
    Double getTotalSalesByAdmin(@Param("adminId") Long adminId);
}