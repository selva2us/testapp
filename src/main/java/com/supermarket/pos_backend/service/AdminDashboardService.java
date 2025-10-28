package com.supermarket.pos_backend.service;

import com.supermarket.pos_backend.dto.DashboardStatsDTO;
import com.supermarket.pos_backend.model.AdminUser;
import com.supermarket.pos_backend.model.Bill;
import com.supermarket.pos_backend.model.Product;
import com.supermarket.pos_backend.model.StaffUser;
import com.supermarket.pos_backend.repository.BillRepository;
import com.supermarket.pos_backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    @Autowired
    private final ProductRepository productRepository;

    @Autowired
    private final BillRepository billRepository;

    public DashboardStatsDTO getDashboardStats(AdminUser admin, StaffUser staff) {
        Long adminId = (admin != null) ? admin.getId() : (staff != null ? staff.getAdmin().getId() : null);
        Long staffId = staff != null ? staff.getId() : null;
        // 1️⃣ Total Sales for this staff (only their own sales)
        double totalSales = billRepository.getTotalSalesByStaff(adminId, staffId);

        // 2️⃣ Products in stock - usually the same for the admin’s store
        Integer productsInStock = productRepository.getTotalProductsInStockByAdmin(admin);
        if (productsInStock == null) productsInStock = 0;

        // 3️⃣ Low stock (same logic, since stock is per admin)
        long lowStock = productRepository.countByAdminAndStockQuantityLessThan(admin, 10);

        // 4️⃣ Today's revenue for this staff
        double todaysRevenue = billRepository.findTodayRevenueByStaff(adminId, staffId);

        // 5️⃣ Last 7 days revenue for this staff
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(6).withHour(0).withMinute(0).withSecond(0);
        List<Double> salesData = billRepository.getLast7DaysSalesByStaff(adminId, staffId, sevenDaysAgo);

        // 6️⃣ Build DTO
        return new DashboardStatsDTO(totalSales, productsInStock, (int) lowStock, todaysRevenue, salesData);
    }

}
