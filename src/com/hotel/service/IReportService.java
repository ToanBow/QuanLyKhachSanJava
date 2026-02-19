package com.hotel.service;

import com.hotel.model.Invoice;

public interface IReportService {
    /**
     * [cite_start]Thống kê doanh thu chi tiết: Tiền phòng, dịch vụ, phụ thu và doanh thu thuần. [cite: 26]
     */
    void getRevenueReport(String timePeriod);

    /**
     * [cite_start]Theo dõi tỷ lệ lấp đầy phòng (Occupancy Rate) thực tế và dự báo tương lai. [cite: 25]
     */
    double getOccupancyRate();

    /**
     * [cite_start]Gửi báo cáo doanh thu và số lượng hóa đơn trực tiếp qua Email cho quản lý. [cite: 15]
     */
    void sendReportToEmail(String managerEmail);
}