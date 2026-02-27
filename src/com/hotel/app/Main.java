package com.hotel.app;

import com.hotel.model.User;
import com.hotel.service.IAuthService;
import com.hotel.service.ISettingService;
import com.hotel.service.impl.AuthServiceImpl;
import com.hotel.service.impl.SettingServiceImpl;
import com.hotel.util.DBConnection;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        System.out.println("========== KIỂM TRA HỆ THỐNG HMS ==========");

        // 1. Kiểm tra kết nối CSDL
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            System.out.println("[OK] Kết nối Database thành công.");
            DBConnection.closeConnection(conn);
        } else {
            System.out.println("[ERROR] Kết nối Database thất bại. Vui lòng kiểm tra file DBConnection.java.");
            return; 
        }

        // 2. Khởi tạo các Service
        IAuthService authService = new AuthServiceImpl();
        ISettingService settingService = new SettingServiceImpl();

        // 3. Kiểm tra Logic Xác thực (AuthService)
        System.out.println("\n--- 1. Kiểm tra Xác thực người dùng ---");
        User frontDesk = new User("nhanvien1@hotel.com", "Hotel@123", "Lễ tân");
        
        System.out.print("Đăng ký tài khoản mới: ");
        boolean isReg = authService.register(frontDesk, "Hotel@123");
        System.out.println(isReg ? "THÀNH CÔNG" : "THẤT BẠI (Có thể đã tồn tại)");

        System.out.print("Thử đăng nhập: ");
        boolean isLogin = authService.login("nhanvien1@hotel.com", "Hotel@123");
        System.out.println(isLogin ? "ĐĂNG NHẬP THÀNH CÔNG" : "SAI TÀI KHOẢN/MẬT KHẨU");

        // 4. Kiểm tra Logic Cấu hình (SettingService)
        System.out.println("\n--- 2. Kiểm tra Cấu hình hệ thống ---");
        
        // Cập nhật thông tin khách sạn
        settingService.updateHotelInfo("Grand Hotel Đông Anh", "Đông Anh, Hà Nội", "0102030405");
        
        // Thiết lập giờ quy chuẩn
        settingService.setOperatingParameters("14:00", "12:00", 0);
        
        // Cấu hình kỹ thuật
        settingService.setupHardwareConnections("smtp.gmail.com", "192.168.1.50");

        System.out.println("Chuc mung nam moi");
    }
}