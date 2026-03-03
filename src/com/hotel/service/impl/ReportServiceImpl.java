package com.hotel.service.impl;

import com.hotel.dao.IInvoiceDAO;
import com.hotel.dao.IRoomDAO;
import com.hotel.dao.impl.InvoiceDAOImpl;
import com.hotel.dao.impl.RoomDAOImpl;
import com.hotel.model.Invoice;
import com.hotel.service.IReportService;
import com.hotel.util.EmailReportUtil;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class ReportServiceImpl implements IReportService {
    
    private IInvoiceDAO invoiceDAO = new InvoiceDAOImpl();
    private IRoomDAO roomDAO = new RoomDAOImpl();

    @Override
    public void getRevenueReport(String timePeriod) {
        System.out.println("Tạo báo cáo doanh thu cho kỳ: " + timePeriod);
    }

    @Override
    public double getOccupancyRate() {
        // Tổng hợp dữ liệu thực từ CSDL
        int totalRooms = roomDAO.countAll();
        int occupiedRooms = roomDAO.countOccupied();
        
        if (totalRooms == 0) return 0.0;
        return ((double) occupiedRooms / totalRooms) * 100;
    }

    @Override
    public void sendReportToEmail(String managerEmail) {
        System.out.println("Đang tổng hợp dữ liệu báo cáo...");
        
        // 1. Lấy ngày hiện tại
        LocalDate today = LocalDate.now();
        
        // 2. Thu thập số liệu thống kê
        // Doanh thu tích lũy của tháng tính đến ngày hiện tại
        double currentMonthRevenue = calculateRevenue(today.getMonthValue(), today.getYear());
        // Tỷ lệ lấp đầy phòng ngay lúc này
        double occupancy = getOccupancyRate();
        
        // 3. Xây dựng nội dung (Template) Email
        String content = "===== BÁO CÁO HOẠT ĐỘNG KHÁCH SẠN HMS =====\n\n"
                + "Ngày lập báo cáo: " + today + "\n\n"
                
                + "1. TÌNH HÌNH LƯU TRÚ\n"
                + "- Tỷ lệ lấp đầy phòng hiện tại: " + String.format("%.2f", occupancy) + "%\n\n"
                
                + "2. TÌNH HÌNH DOANH THU\n"
                + "- Doanh thu tích lũy trong tháng (" + today.getMonthValue() + "/" + today.getYear() + "): " 
                + String.format("%,.0f", currentMonthRevenue) + " VNĐ\n\n"
                
                + "=============================================\n"
                + "Email này được gửi tự động từ Hotel Management System.";
                
        // 4. Gọi Utility để gửi Mail
        System.out.println("Đang gửi báo cáo đến email: " + managerEmail + "...");
        EmailReportUtil.sendReport(managerEmail, content);
        System.out.println("Đã gửi báo cáo thành công!");
    }

    // Tính doanh thu theo tháng/năm thực tế
    public double calculateRevenue(int month, int year) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        
        // Chuyển đổi LocalDate sang java.util.Date
        Date start = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(endDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());
        
        List<Invoice> invoices = invoiceDAO.getInvoicesByPeriod(start, end);
        double totalRevenue = 0;
        
        if (invoices != null) {
            for (Invoice inv : invoices) {
                totalRevenue += inv.getTotalAmount();
            }
        }
        return totalRevenue;
    }
}