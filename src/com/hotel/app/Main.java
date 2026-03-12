package com.hotel.app;

import com.hotel.dao.impl.ServiceDAOImpl;
import com.hotel.model.Guest;
import com.hotel.model.Invoice;
import com.hotel.model.Room;
import com.hotel.model.Service;
import com.hotel.service.impl.PaymentServiceImpl;
import com.hotel.service.impl.ReportServiceImpl;
import com.hotel.service.impl.RoomServiceImpl;
import com.hotel.service.impl.StayServiceImpl;
import com.hotel.util.DBConnection;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
//        // 1. Giả lập một hóa đơn
//    Invoice testInvoice = new Invoice();
//    testInvoice.setInvoiceId("HD001");
//    testInvoice.setRoomId("P101");
//    testInvoice.setGuestCccd("0123456789");
//    testInvoice.setTotalAmount(1500000); // 1.5 triệu
//    testInvoice.setDiscount(10);        // Giảm 10%
//
//    // 2. Gọi Service để in PDF
//    PaymentServiceImpl paymentService = new PaymentServiceImpl();
//    paymentService.printInvoiceToFile(testInvoice);
//    
//    System.out.println("Hãy kiểm tra thư mục dự án để xem file PDF!");
        // 1. Kiểm tra kết nối CSDL
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                System.out.println("[INFO] Kết nối CSDL MySQL thành công.");
            } else {
                System.err.println("[ERROR] Lỗi kết nối CSDL. Vui lòng kiểm tra DBConnection.");
                return;
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Hệ thống không thể khởi động: " + e.getMessage());
            return;
        }

        // 2. Khởi tạo các Service
        RoomServiceImpl roomService = new RoomServiceImpl();
        StayServiceImpl stayService = new StayServiceImpl();
        PaymentServiceImpl paymentService = new PaymentServiceImpl();
        ReportServiceImpl reportService = new ReportServiceImpl();
        ServiceDAOImpl serviceDAO = new ServiceDAOImpl();

        Scanner sc = new Scanner(System.in);
        boolean isRunning = true;

        while (isRunning) {
            System.out.println("\n=== HỆ THỐNG QUẢN LÝ KHÁCH SẠN ===");
            System.out.println("1. Xem sơ đồ phòng");
            System.out.println("2. Check-in (Nhận phòng)");
            System.out.println("3. Gọi dịch vụ phòng");
            System.out.println("4. Check-out (Trả phòng & Thanh toán)");
            System.out.println("5. Báo cáo thống kê");
            System.out.println("0. Thoát");
            System.out.print("Chọn chức năng (0-5): ");
            
            String choice = sc.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        System.out.println("\n[SƠ ĐỒ PHÒNG]");
                        List<Room> rooms = roomService.getRoomMap();
                        if (rooms.isEmpty()) {
                            System.out.println("Không có dữ liệu phòng.");
                        } else {
                            for (Room r : rooms) {
                                System.out.printf("Phòng %-4s | Loại: %-10s | Giá: %,.0f VND | Trạng thái: %s\n", 
                                    r.getRoomId(), r.getType(), r.getDailyPrice(), r.getStatus());
                            }
                        }
                        break;

                    case "2":
                        System.out.println("\n[THỦ TỤC CHECK-IN]");
                        System.out.print("Mã phòng (VD: 101): "); String roomId = sc.nextLine().trim();
                        System.out.print("CCCD: "); String cccd = sc.nextLine().trim();
                        System.out.print("Tên khách hàng: "); String name = sc.nextLine().trim();
                        System.out.print("Số điện thoại: "); String phone = sc.nextLine().trim();
                        System.out.print("Email: "); String email = sc.nextLine().trim();
                        System.out.print("Hình thức thuê (Theo ngày/Theo giờ): "); String rentalType = sc.nextLine().trim();
                        System.out.print("Tiền cọc (VND): "); double deposit = Double.parseDouble(sc.nextLine().trim());

                        Guest guest = new Guest(null, name, email, phone, cccd);
                        Invoice invoice = new Invoice();
                        invoice.setRentalType(rentalType);
                        invoice.setDeposit(deposit);

                        stayService.checkIn(roomId, guest, invoice);
                        break;

                    case "3":
                        System.out.println("\n[GỌI DỊCH VỤ]");
                        System.out.print("Mã phòng: "); String roomForService = sc.nextLine().trim();
                        System.out.print("Mã dịch vụ (VD: SV01): "); String serviceId = sc.nextLine().trim();
                        System.out.print("Số lượng: "); int qty = Integer.parseInt(sc.nextLine().trim());

                        Service service = serviceDAO.findById(serviceId);
                        if (service != null) {
                            stayService.manageRoomServices(roomForService, service, qty, true);
                        } else {
                            System.out.println("Mã dịch vụ không tồn tại.");
                        }
                        break;

                    case "4":
                        System.out.println("\n[CHECK-OUT & THANH TOÁN]");
                        System.out.print("Mã phòng cần trả: "); String roomOut = sc.nextLine().trim();
                        
                        Invoice outInvoice = stayService.processCheckOut(roomOut);
                        if (outInvoice != null) {
                            System.out.printf("Tổng thanh toán: %,.0f VND\n", outInvoice.getTotalAmount());
                            System.out.print("Phương thức thanh toán (Tiền mặt/Tín dụng): ");
                            String method = sc.nextLine().trim();
                            
                            paymentService.processPayment(method, outInvoice.getTotalAmount());
                            stayService.printInvoice(outInvoice.getInvoiceId());
                        }
                        break;

                    case "5":
                        System.out.println("\n[BÁO CÁO THỐNG KÊ]");
                        int month = LocalDate.now().getMonthValue();
                        int year = LocalDate.now().getYear();
                        
                        double revenue = reportService.calculateRevenue(month, year);
                        double occupancy = reportService.getOccupancyRate();

                        System.out.printf("Kỳ báo cáo: %d/%d\n", month, year);
                        System.out.printf("Doanh thu: %,.0f VND\n", revenue);
                        System.out.printf("Tỷ lệ lấp đầy: %.2f%%\n", occupancy);
                        break;

                    case "0":
                        System.out.println("Kết thúc phiên làm việc.");
                        isRunning = false;
                        break;

                    default:
                        System.out.println("Lựa chọn không hợp lệ.");
                }
            } catch (NumberFormatException e) {
                System.err.println("[LỖI] Dữ liệu nhập vào phải là số.");
            } catch (Exception e) {
                System.err.println("[LỖI HỆ THỐNG] " + e.getMessage());
            }
        }
        sc.close();
        System.exit(0);
    }
}