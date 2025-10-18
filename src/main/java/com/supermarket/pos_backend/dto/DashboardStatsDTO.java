package com.supermarket.pos_backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class DashboardStatsDTO {
    // Getters and Setters
    private double totalSales;
    private int productsInStock;
    private long lowStock;
    private double todaysRevenue;
    private List<Double> salesData;

    public DashboardStatsDTO(double totalSales, int productsInStock, long lowStock, double todaysRevenue, List<Double> salesData) {
        this.totalSales = totalSales;
        this.productsInStock = productsInStock;
        this.lowStock = lowStock;
        this.todaysRevenue = todaysRevenue;
        this.salesData = salesData;
    }

}
