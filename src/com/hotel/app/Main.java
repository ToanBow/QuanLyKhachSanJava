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
        System.out.println("Đang khởi động Hệ thống Quản lý Khách sạn HMS...");
        
        // 0. KIỂM TRA KẾT NỐI CƠ SỞ DỮ LIỆU TRƯỚC TIÊN
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                System.out.println("[OK] Đã kết nối thành công tới CSDL MySQL!");
            } else {
                System.out.println("[LỖI] Không thể kết nối tới DB. Vui lòng kiểm tra lại DBConnection.java.");
                return; // Dừng chương trình nếu không có DB
            }
        } catch (Exception e) {
            System.err.println("[LỖI NGHIÊM TRỌNG] " + e.getMessage());
            System.err.println("Hãy chắc chắn bạn đã Add thư viện mysql-connector-j vào Referenced Libraries!");
            return;
        }

        // 1. Khởi động luồng gửi báo cáo tự động (Chạy ngầm)
        // Lưu ý: Thay email dưới đây thành email thật của bạn để test nhận báo cáo
        DailyReportScheduler.startDailyReportTask("quanly.hms.test@gmail.com");

        // 2. Khởi tạo các Service
        RoomServiceImpl roomService = new RoomServiceImpl();
        StayServiceImpl stayService = new StayServiceImpl();
        PaymentServiceImpl paymentService = new PaymentServiceImpl();
        ReportServiceImpl reportService = new ReportServiceImpl();
        ServiceDAOImpl serviceDAO = new ServiceDAOImpl(); 

        Scanner sc = new Scanner(System.in);
        boolean isRunning = true;

        while (isRunning) {
            System.out.println("\n===========================================");
            System.out.println("   HỆ THỐNG QUẢN LÝ KHÁCH SẠN HMS");
            System.out.println("===========================================");
            System.out.println("1. Xem sơ đồ phòng (Trạng thái phòng)");
            System.out.println("2. Check-in (Nhận phòng)");
            System.out.println("3. Gọi dịch vụ (Mini bar, Giặt là...)");
            System.out.println("4. Check-out & Thanh toán");
            System.out.println("5. Báo cáo doanh thu & Lấp đầy");
            System.out.println("6. Gửi báo cáo qua Email cho Quản lý");
            System.out.println("0. Thoát chương trình");
            System.out.println("===========================================");
            System.out.print("Chọn chức năng (0-6): ");
            
            String choice = sc.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        System.out.println("\n--- SƠ ĐỒ PHÒNG ---");
                        List<Room> rooms = roomService.getRoomMap();
                        if(rooms.isEmpty()) {
                            System.out.println("Chưa có dữ liệu phòng trong CSDL. Vui lòng thêm phòng vào bảng 'rooms'.");
                        } else {
                            for (Room r : rooms) {
                                System.out.printf("Phòng: %-5s | Loại: %-10s | Giá: %,.0f VND | Trạng thái: %s\n", 
                                    r.getRoomId(), r.getType(), r.getDailyPrice(), r.getStatus());
                            }
                        }
                        break;

                    case "2":
                        System.out.println("\n--- THỦ TỤC CHECK-IN ---");
                        System.out.print("Nhập số phòng (VD: 101): ");
                        String roomId = sc.nextLine().trim();

                        System.out.print("Nhập CCCD khách hàng: ");
                        String cccd = sc.nextLine().trim();
                        System.out.print("Nhập Tên khách hàng: ");
                        String name = sc.nextLine().trim();
                        System.out.print("Nhập Số điện thoại: ");
                        String phone = sc.nextLine().trim();
                        System.out.print("Nhập Email: ");
                        String email = sc.nextLine().trim();
                        
                        System.out.print("Hình thức thuê (Theo ngày / Theo giờ): ");
                        String rentalType = sc.nextLine().trim();
                        System.out.print("Tiền cọc trước (VND): ");
                        double deposit = Double.parseDouble(sc.nextLine().trim());

                        // Cấu hình Khách và Hóa đơn
                        Guest guest = new Guest(null, name, email, phone, cccd);
                        Invoice invoice = new Invoice();
                        invoice.setRentalType(rentalType);
                        invoice.setDeposit(deposit);

                        // Thực thi Check-in
                        boolean isSuccess = stayService.checkIn(roomId, guest, invoice);
                        if (!isSuccess) {
                            System.out.println("Check-in thất bại. Có thể phòng đã có người hoặc không tồn tại.");
                        }
                        break;

                    case "3":
                        System.out.println("\n--- GỌI DỊCH VỤ PHÒNG ---");
                        System.out.print("Nhập số phòng đang lưu trú: ");
                        String roomForService = sc.nextLine().trim();
                        System.out.print("Nhập Mã dịch vụ (VD: SV01): ");
                        String serviceId = sc.nextLine().trim();
                        System.out.print("Số lượng: ");
                        int qty = Integer.parseInt(sc.nextLine().trim());

                        Service service = serviceDAO.findById(serviceId);
                        if (service != null) {
                            stayService.manageRoomServices(roomForService, service, qty, true);
                        } else {
                            System.out.println("Không tìm thấy dịch vụ mã: " + serviceId);
                        }
                        break;

                    case "4":
                        System.out.println("\n--- CHECK-OUT & THANH TOÁN ---");
                        System.out.print("Nhập số phòng cần trả: ");
                        String roomOut = sc.nextLine().trim();
                        
                        Invoice outInvoice = stayService.processCheckOut(roomOut);
                        
                        if (outInvoice != null) {
                            System.out.println("\n>>> TỔNG TIỀN PHẢI THANH TOÁN: " + String.format("%,.0f", outInvoice.getTotalAmount()) + " VND");
                            System.out.print("Chọn phương thức thanh toán (Tiền mặt / Tín dụng): ");
                            String method = sc.nextLine().trim();
                            
                            paymentService.processPayment(method, outInvoice.getTotalAmount());
                            stayService.printInvoice(outInvoice.getInvoiceId());
                        }
                        break;

                    case "5":
                        System.out.println("\n--- BÁO CÁO THỐNG KÊ ---");
                        int month = LocalDate.now().getMonthValue();
                        int year = LocalDate.now().getYear();
                        
                        double revenue = reportService.calculateRevenue(month, year);
                        double occupancy = reportService.getOccupancyRate();

                        System.out.println("Tháng hiện tại: " + month + "/" + year);
                        System.out.println("Doanh thu trong tháng: " + String.format("%,.0f", revenue) + " VND");
                        System.out.println("Tỷ lệ lấp đầy phòng: " + String.format("%.2f", occupancy) + "%");
                        break;

                    case "6":
                        System.out.println("\n--- GỬI EMAIL BÁO CÁO ---");
                        System.out.print("Nhập Email của quản lý để nhận báo cáo: ");
                        String managerEmail = sc.nextLine().trim();
                        reportService.sendReportToEmail(managerEmail);
                        break;

                    case "0":
                        System.out.println("Đang lưu dữ liệu và thoát chương trình... Tạm biệt!");
                        isRunning = false;
                        break;

                    default:
                        System.out.println("Lựa chọn không hợp lệ, vui lòng chọn số từ 0 đến 6!");
                }
            } catch (NumberFormatException e) {
                System.err.println("LỖI: Bạn đã nhập chữ vào ô yêu cầu nhập số (VD: Tiền, số lượng). Vui lòng thử lại!");
            } catch (Exception e) {
                System.err.println("Cảnh báo: Lỗi hệ thống (" + e.getMessage() + ")");
                e.printStackTrace(); // In ra chi tiết lỗi để dễ fix
            }
        }
        sc.close();
        System.exit(0);
    }
}