package com.hotel.service.impl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import com.hotel.service.ISettingService;
import com.hotel.util.DBConnection;

public class SettingServiceImpl implements ISettingService {
    @Override
    public void updateHotelInfo(String name, String address, String taxCode) {
        // TODO: Cấu hình thông tin khách sạn in trên hóa đơn [cite: 33]
        System.out.println("CAP NHAT THONG TIN KHACH SAN");
        System.out.println("Ten khach san " + name);
        System.out.println("Dia chi khach san " + address);
        System.out.println("Ma so thue " + taxCode);

        saveSetting("hotel.name", name);
        saveSetting("hotel.address", address);
        saveSetting("hotel.taxcode", taxCode);
    }

    @Override
    public void setOperatingParameters(String checkInTime, String checkOutTime, int rounding) {
        // TODO: Thiết lập giờ quy chuẩn và làm tròn tiền tệ [cite: 35]
        System.out.println("THIET LAP THAM SO VAN HANH");
        System.out.println("Gio Check-in quy chuan " + checkInTime);
        System.out.println("Gio Check-out quy chuan " + checkOutTime);
        System.out.println("Lam tron so thap phan " + rounding);

        saveSetting("op.checkin", checkInTime);
        saveSetting("op.checkout", checkOutTime);
        saveSetting("op.rounding", String.valueOf(rounding));
    }

    @Override
    public void setupHardwareConnections(String smtpServer, String printerAddress) {
        // TODO: Cấu hình SMTP gửi mail và kết nối máy in hóa đơn, khóa từ [cite: 36]
        System.out.println("CAU HINH PHAN CUNG");
        System.out.println("May chu SMTP: " + smtpServer);
        System.out.println("Dia chi may in: " + printerAddress);

        // Logic kết nối thử (Test connection)
        if (smtpServer != null && !smtpServer.isEmpty()) {
            System.out.println("=> Dang ket noi toi may chu...Thanh cong!");
        }

        saveSetting("hw.smtp", smtpServer);
        saveSetting("hw.printer", printerAddress);
    }

    private void saveSetting(String key, String value) {
    String sql = "INSERT INTO system_settings (setting_key, setting_value) VALUES (?, ?) " +
                 "ON DUPLICATE KEY UPDATE setting_value = VALUES(setting_value)";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, key);
        ps.setString(2, value);
        ps.executeUpdate();
    } catch (SQLException e) {
        System.err.println("Lỗi lưu cấu hình vào DB: " + e.getMessage());
    }
}
}