package com.supermarket.pos_backend.service;

import com.supermarket.pos_backend.dto.DashboardStatsDTO;
import com.supermarket.pos_backend.model.AdminUser;
import com.supermarket.pos_backend.model.Bill;
import com.supermarket.pos_backend.model.Product;
import com.supermarket.pos_backend.repository.BillRepository;
import com.supermarket.pos_backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final ProductRepository productRepository;
    private final BillRepository billRepository;

    public DashboardStatsDTO getDashboardStats(AdminUser admin) {
        // 1️⃣ Total Sales for this admin
        double totalSales = billRepository.getTotalSalesByAdmin(admin.getId());

        // 2️⃣ Products in Stock for this admin
        Integer productsInStock = productRepository.getTotalProductsInStockByAdmin(admin);
        if (productsInStock == null) productsInStock = 0;

        // 3️⃣ Low Stock Count for this admin
        long lowStock = productRepository.countByAdminAndStockQuantityLessThan(admin, 10);

        // 4️⃣ Today's Revenue for this admin
        double todaysRevenue = billRepository.findTodayRevenueByAdmin(admin.getId());

        // 5️⃣ Last 7 Days Revenue for this admin
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(6).withHour(0).withMinute(0).withSecond(0);
        List<Double> salesData = billRepository.getLast7DaysSalesByAdmin(admin.getId(), sevenDaysAgo);

        // 6️⃣ Build DTO
        return new DashboardStatsDTO(totalSales, productsInStock, (int) lowStock, todaysRevenue, salesData);
    }

}
