package com.hotel.service;

public interface IReportService {
    /**
    ]Thống kê doanh thu chi tiết: Tiền phòng, dịch vụ, phụ thu và doanh thu thuần.
     */
    void getRevenueReport(String timePeriod);

    /**
    Theo dõi tỷ lệ lấp đầy phòng (Occupancy Rate) thực tế và dự báo tương lai. 
     */
    double getOccupancyRate();

    /**
    Gửi báo cáo doanh thu và số lượng hóa đơn trực tiếp qua Email cho quản lý. 
     */
    void sendReportToEmail(String managerEmail);
}