package com.hotel.service.impl;

import com.hotel.dao.IInvoiceDAO;
import com.hotel.dao.IRoomDAO;
import com.hotel.dao.impl.InvoiceDAOImpl;
import com.hotel.dao.impl.RoomDAOImpl;
import com.hotel.service.IReportService;
import com.hotel.util.DBConnection;
import com.hotel.util.EmailReportUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class ReportServiceImpl implements IReportService {
    
    private IInvoiceDAO invoiceDAO = new InvoiceDAOImpl();
    private IRoomDAO roomDAO = new RoomDAOImpl();

    @Override
    public void getRevenueReport(String timePeriod) {
        System.out.println("Tạo báo cáo doanh thu cho kỳ: " + timePeriod);
    }

    @Override
    public double getOccupancyRate() {
        int totalRooms = roomDAO.countAll();
        int occupiedRooms = roomDAO.countOccupied();
        if (totalRooms == 0) return 0.0;
        return ((double) occupiedRooms / totalRooms) * 100;
    }

    @Override
    public void sendReportToEmail(String managerEmail) {
        LocalDate today = LocalDate.now();
        double currentMonthRevenue = calculateRevenue(today.getMonthValue(), today.getYear());
        double occupancy = getOccupancyRate();
        
        String content = "===== BÁO CÁO HOẠT ĐỘNG KHÁCH SẠN HMS =====\n\n"
                + "Ngày lập báo cáo: " + today + "\n\n"
                + "1. TÌNH HÌNH LƯU TRÚ\n"
                + "- Tỷ lệ lấp đầy phòng hiện tại: " + String.format("%.2f", occupancy) + "%\n\n"
                + "2. TÌNH HÌNH DOANH THU\n"
                + "- Doanh thu tích lũy trong tháng (" + today.getMonthValue() + "/" + today.getYear() + "): " 
                + String.format("%,.0f", currentMonthRevenue) + " VNĐ\n\n"
                + "=============================================\n"
                + "Email này được gửi tự động từ Hotel Management System.";
                
        EmailReportUtil.sendReport(managerEmail, content);
    }

    // TÍNH DOANH THU THUẦN CHUẨN XÁC BẰNG SQL
    public double calculateRevenue(int month, int year) {
        double totalRevenue = 0;
        String sql = "SELECT SUM(total_amount + deposit) as net_revenue FROM invoices " +
                     "WHERE payment_date IS NOT NULL AND MONTH(payment_date) = ? AND YEAR(payment_date) = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) totalRevenue = rs.getDouble("net_revenue");
            }
        } catch (Exception e) { e.printStackTrace(); }
        
        return totalRevenue;
    }
}