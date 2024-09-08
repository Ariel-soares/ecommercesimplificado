package com.arielsoares.ecommercesimplificado.controllers;

import java.time.LocalDate;
import java.time.YearMonth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.arielsoares.ecommercesimplificado.entities.Report;
import com.arielsoares.ecommercesimplificado.services.ReportService;

@RestController
@RequestMapping("/reports")
public class ReportController {

	@Autowired
	private ReportService reportService;

	@GetMapping("/sales")
    public ResponseEntity<Report> getSalesReportByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        Report report = reportService.generateSalesReportByDate(date);
        return ResponseEntity.ok(report);
    }

	@GetMapping("/sales/month")
    public ResponseEntity<Report> getSalesReportByMonth(
            @RequestParam("month") @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {

        Report report = reportService.generateSalesReportByMonth(month);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/sales/week")
    public ResponseEntity<Report> getWeeklySalesReport() {
        Report report = reportService.generateWeeklySalesReport();
        return ResponseEntity.ok(report);
    }
}