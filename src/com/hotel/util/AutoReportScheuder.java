package com.hotel.util;

import com.hotel.service.impl.ReportServiceImpl;

import java.time.LocalDate;
import java.util.Timer;
import java.util.TimerTask;

public class AutoReportScheuder {

    public static void start() {

        Timer timer = new Timer(true);

        timer.schedule(new TimerTask() {

            @Override
            public void run() {

                try {

                    System.out.println("[AUTO REPORT] Dang tao bao cao...");

                    ReportServiceImpl reportService = new ReportServiceImpl();

                    int month = LocalDate.now().getMonthValue();
                    int year = LocalDate.now().getYear();

                    double revenue = reportService.calculateRevenue(month, year);
                    double occupancy = reportService.getOccupancyRate();

                    String pdf = PdfReportUtil.createReport(revenue, occupancy);

                    String managerEmail = EnvConfig.getReportEmail();

                    EmailReportUtil.sendReport(managerEmail, pdf);

                    System.out.println("[AUTO REPORT] Gui bao cao thanh cong!");

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }, 0, 24 * 60 * 60 * 10000);

    }
}