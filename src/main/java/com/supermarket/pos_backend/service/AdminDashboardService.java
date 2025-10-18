package com.supermarket.pos_backend.service;

import com.supermarket.pos_backend.dto.DashboardStatsDTO;
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

    public DashboardStatsDTO getDashboardStats() {
        // 1️⃣ Total Sales
        double totalSales = billRepository.getTotalSales();

        // 2️⃣ Products in Stock
        Integer productsInStock = productRepository.getTotalProductsInStock();
        if (productsInStock == null) productsInStock = 0;

        // 3️⃣ Low Stock Count
        long lowStock = productRepository.countByStockQuantityLessThan(10);

        // 4️⃣ Today's Revenue
        double todaysRevenue = billRepository.findTodayRevenue();

        // 5️⃣ Last 7 Days Revenue
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(6).withHour(0).withMinute(0).withSecond(0);
        List<Double> salesData = billRepository.getLast7DaysSales(sevenDaysAgo);

        // 6️⃣ Build DTO
        return new DashboardStatsDTO(totalSales, productsInStock, (int) lowStock, todaysRevenue, salesData);
    }
}
