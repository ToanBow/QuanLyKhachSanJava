package com.hotel.service.report;

import com.hotel.dao.IInvoiceDAO;
import com.hotel.dao.IRoomDAO;
import com.hotel.dao.impl.InvoiceDAOImpl;
import com.hotel.dao.impl.RoomDAOImpl;
import com.hotel.model.Invoice;

import java.time.LocalDate;
import java.util.List;

public class AdvancedReportService {

    private final IInvoiceDAO invoiceDAO;
    private final IRoomDAO roomDAO;

    public AdvancedReportService() {
        this.invoiceDAO = new InvoiceDAOImpl() {
            @Override
            public List<Invoice> findAll() {
                return List.of();
            }
        };
        this.roomDAO = new RoomDAOImpl();
    }

    // Doanh thu theo ngày
    public double revenueByDay(LocalDate date) {
        return invoiceDAO.findAll().stream()
                .filter(i -> i.getCheckInTime() != null &&
                        i.getCheckInTime().toLocalDate().equals(date))
                .mapToDouble(Invoice::getTotalAmount)
                .sum();
    }

    // Doanh thu theo tháng
    public double revenueByMonth(int month) {
        return invoiceDAO.findAll().stream()
                .filter(i -> i.getCheckInTime() != null &&
                        i.getCheckInTime().getMonthValue() == month)
                .mapToDouble(Invoice::getTotalAmount)
                .sum();
    }

    // Doanh thu theo năm
    public double revenueByYear(int year) {
        return invoiceDAO.findAll().stream()
                .filter(i -> i.getCheckInTime() != null &&
                        i.getCheckInTime().getYear() == year)
                .mapToDouble(Invoice::getTotalAmount)
                .sum();
    }

    // Tỷ lệ lấp đầy phòng
    public double occupancyRate() {
        int totalRooms = roomDAO.countAll();
        int occupiedRooms = roomDAO.countOccupied();

        if (totalRooms == 0) return 0;

        return (double) occupiedRooms / totalRooms * 100;
    }
}