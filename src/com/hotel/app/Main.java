package com.hotel.app;

import com.hotel.model.Guest;
import com.hotel.service.impl.CustomerServiceImpl;
import com.hotel.service.impl.StayServiceImpl;
import com.hotel.service.impl.PaymentServiceImpl;
import com.hotel.service.impl.ReportServiceImpl;
import com.hotel.util.EmailReportUtil;

import java.time.LocalDate;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        try {

            Scanner sc = new Scanner(System.in);

            CustomerServiceImpl customerService = new CustomerServiceImpl();
            StayServiceImpl stayService = new StayServiceImpl();
            PaymentServiceImpl paymentService = new PaymentServiceImpl();
            ReportServiceImpl reportService = new ReportServiceImpl();

            System.out.println("===== HE THONG QUAN LY KHACH SAN =====");

            System.out.print("Nhap ma khach: ");
            String guestId = sc.nextLine();

            System.out.print("Nhap ten khach: ");
            String name = sc.nextLine();

            System.out.print("Nhap email: ");
            String email = sc.nextLine();

            System.out.print("Nhap SDT: ");
            String phone = sc.nextLine();

            System.out.print("Nhap CCCD: ");
            String cccd = sc.nextLine();

            Guest guest = new Guest(guestId, name, email, phone, cccd);

            // ✅ Đúng tên method trong project
            customerService.addCustomer(guest);

            System.out.println("Them khach thanh cong");

            System.out.print("Nhap ma phong: ");
            String roomId = sc.nextLine();

            stayService.checkIn(guestId, roomId);
            System.out.println("Checkin thanh cong");

            System.out.print("Nhap so tien thanh toan: ");
            double amount = Double.parseDouble(sc.nextLine());

            paymentService.processPayment(guestId, amount);
            System.out.println("Thanh toan thanh cong");

            // ✅ Đúng tên method trong ReportServiceImpl
            double revenue = reportService.calculateRevenue(
                    LocalDate.now().getMonthValue(),
                    LocalDate.now().getYear()
            );

            double occupancy = reportService.getOccupancyRate();

            System.out.println("Doanh thu thang: " + revenue);
            System.out.println("Ty le lap day: " + occupancy + "%");

            // ✅ EmailUtil là static
            String content =
                    "===== BAO CAO HE THONG KHACH SAN =====\n\n" +

                            "1. THONG TIN KHACH HANG\n" +
                            "Ma khach: " + guestId + "\n" +
                            "Ten khach: " + name + "\n" +
                            "Email: " + email + "\n" +
                            "SDT: " + phone + "\n" +
                            "CCCD: " + cccd + "\n\n" +

                            "2. THONG TIN DAT PHONG\n" +
                            "Ma phong: " + roomId + "\n" +
                            "So tien thanh toan: " + amount + " VND\n\n" +

                            "3. BAO CAO TONG HOP\n" +
                            "Thang: " + LocalDate.now().getMonthValue() + "/" + LocalDate.now().getYear() + "\n" +
                            "Tong doanh thu thang: " + revenue + " VND\n" +
                            "Ty le lap day: " + occupancy + " %\n\n" +

                            "Ngay tao bao cao: " + LocalDate.now() + "\n\n" +

                            "===== HE THONG QUAN LY KHACH SAN =====";

            EmailReportUtil.sendReport(email, content);

            System.out.println("Gui mail thanh cong");

        } catch (Exception e) {
            System.out.println("LOI: " + e.getMessage());
        }
    }
}