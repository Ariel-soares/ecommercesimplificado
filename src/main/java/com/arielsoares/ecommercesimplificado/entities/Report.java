package com.arielsoares.ecommercesimplificado.entities;

import java.time.Instant;
import java.util.List;

public class Report {
    private String reportType;
    
    private Instant reportDate;
    private List<Order> orders;
    private Double totalSales;

    public Report(String reportType, Instant reportDate, List<Order> orders, Double totalSales) {
        this.reportType = reportType;
        this.reportDate = reportDate;
        this.orders = orders;
        this.totalSales = totalSales;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public Instant getReportDate() {
        return reportDate;
    }

    public void setReportDate(Instant reportDate) {
        this.reportDate = reportDate;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public Double getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(Double totalSales) {
        this.totalSales = totalSales;
    }
}