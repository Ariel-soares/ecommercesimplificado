package com.arielsoares.ecommercesimplificado.services;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arielsoares.ecommercesimplificado.entities.Order;
import com.arielsoares.ecommercesimplificado.entities.Report;
import com.arielsoares.ecommercesimplificado.repositories.OrderRepository;

@Service
public class ReportService {

	@Autowired
	private OrderRepository orderRepository;

	public Report generateSalesReportByDate(LocalDate date) {
		List<Order> orders = orderRepository.findByMomentBetween(date.atStartOfDay(ZoneOffset.UTC).toInstant(),
				date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant());
		Double totalSales = orders.stream().mapToDouble(Order::getTotal).sum();

		return new Report("Sales Report by Date", date.atStartOfDay(ZoneOffset.UTC).toInstant(), orders, totalSales);
	}

	public Report generateSalesReportByMonth(YearMonth month) {
		Instant startOfMonth = month.atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant();
		Instant endOfMonth = month.atEndOfMonth().atStartOfDay(ZoneOffset.UTC).toInstant();

		List<Order> orders = orderRepository.findByMomentBetween(startOfMonth, endOfMonth);
		Double totalSales = orders.stream().mapToDouble(Order::getTotal).sum();

		return new Report("Sales Report by Month", startOfMonth, orders, totalSales);
	}

	public Report generateWeeklySalesReport() {
		LocalDate now = LocalDate.now();
		LocalDate startOfWeek = now.with(DayOfWeek.MONDAY);
		Instant startOfWeekInstant = startOfWeek.atStartOfDay(ZoneOffset.UTC).toInstant();
		Instant endOfWeekInstant = now.with(DayOfWeek.SUNDAY).plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

		List<Order> orders = orderRepository.findByMomentBetween(startOfWeekInstant, endOfWeekInstant);
		Double totalSales = orders.stream().mapToDouble(Order::getTotal).sum();

		return new Report("Weekly Sales Report", startOfWeekInstant, orders, totalSales);
	}
}