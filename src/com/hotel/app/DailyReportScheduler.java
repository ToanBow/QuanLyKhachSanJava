package com.hotel.app;

import com.hotel.service.IReportService;
import com.hotel.service.impl.ReportServiceImpl;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DailyReportScheduler {
    public static void startDailyReportTask(String managerEmail) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        IReportService reportService = new ReportServiceImpl();

        // Chạy tác vụ đếm ngược, ví dụ: lặp lại mỗi 24 giờ
        scheduler.scheduleAtFixedRate(() -> {
            try {
                reportService.sendReportToEmail(managerEmail);
            } catch (Exception e) {
                System.err.println("Lỗi khi chạy tác vụ gửi email tự động: " + e.getMessage());
            }
        }, 0, 24, TimeUnit.HOURS); // Tham số: delay ban đầu = 0, lặp lại sau = 24, đơn vị = Giờ
    }
}