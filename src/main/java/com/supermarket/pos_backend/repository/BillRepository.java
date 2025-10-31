package com.supermarket.pos_backend.repository;

import com.supermarket.pos_backend.model.Bill;
import com.supermarket.pos_backend.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByAdminId(Long adminId);
    List<Bill> findByAdminIdAndStaffId(Long adminId, Long staffId);
    Optional<Bill> findByIdAndAdminId(Long id, Long adminId);
    List<Bill> findByCustomer(Customer customer);

    @Query("""
    SELECT COALESCE(SUM(b.totalAmount), 0)
    FROM Bill b
    WHERE b.admin.id = :adminId
    AND (:staffId IS NULL OR b.staff.id = :staffId)
    """)
    double getTotalSalesByStaff(@Param("adminId") Long adminId, @Param("staffId") Long staffId);

    @Query("""
    SELECT COALESCE(SUM(b.totalAmount), 0)
    FROM Bill b
    WHERE b.admin.id = :adminId
      AND(:staffId IS NULL OR b.staff.id = :staffId)
      AND FUNCTION('DATE', b.billDate) = CURRENT_DATE
""")
    double findTodayRevenueByStaff(@Param("adminId") Long adminId, @Param("staffId") Long staffId);

    @Query("""
      SELECT COALESCE(SUM(b.totalAmount), 0) FROM Bill b WHERE b.admin.id = :adminId AND (:staffId IS NULL OR b.staff.id = :staffId)AND b.billDate >= :sevenDaysAgo GROUP BY FUNCTION('DATE', b.billDate) ORDER BY FUNCTION('DATE', b.billDate)
    """)
    List<Double> getLast7DaysSalesByStaff(@Param("adminId") Long adminId, @Param("staffId") Long staffId, @Param("sevenDaysAgo") LocalDateTime sevenDaysAgo);

}