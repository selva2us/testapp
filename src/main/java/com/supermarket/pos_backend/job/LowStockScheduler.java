package com.supermarket.pos_backend.job;

import com.supermarket.pos_backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LowStockScheduler {

    private final ProductService productService;

    @Scheduled(cron = "0 0 9 * * ?") // every day at 9 AM
    public void checkLowStock() {
        var lowStock = productService.getLowStockProducts();
        if (!lowStock.isEmpty()) {
            // send email, push notification, or log
            System.out.println("Low stock items: " + lowStock);
        }
    }
}

