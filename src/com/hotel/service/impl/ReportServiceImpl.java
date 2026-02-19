package com.hotel.service.impl;

import com.hotel.service.IReportService;

public class ReportServiceImpl implements IReportService {
    @Override
    public void getRevenueReport(String timePeriod) {
        // TODO: Thống kê doanh thu tiền phòng, dịch vụ, phụ thu và doanh thu thuần [cite: 14, 26]
    }

    @Override
    public double getOccupancyRate() {
        // TODO: Tính toán tỷ lệ lấp đầy phòng thực tế và dự báo [cite: 25]
        return 0.0;
    }

    @Override
    public void sendReportToEmail(String managerEmail) {
        // TODO: Gửi báo cáo doanh thu trực tiếp qua Email cho quản lý [cite: 15]
    }
}