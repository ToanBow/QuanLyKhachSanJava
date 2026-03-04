package com.hotel.service.impl;

import com.hotel.dao.IGuestDAO;
import com.hotel.dao.impl.GuestDAOImpl;
import com.hotel.model.Guest;
import com.hotel.service.ICustomerService;
import com.hotel.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerServiceImpl implements ICustomerService {

    // Tái sử dụng DAO để đảm bảo tính đóng gói (Encapsulation)
    private IGuestDAO guestDAO = new GuestDAOImpl();

    @Override
    public void updateCustomerRank(String cccd) {
        // Thuật toán xếp hạng dựa trên tổng chi tiêu (Total Spent) từ các hóa đơn đã thanh toán
        String sql = "SELECT SUM(total_amount) FROM invoices WHERE guest_cccd = ? AND status = 'PAID'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setString(1, cccd);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double totalSpent = rs.getDouble(1);
                    String newRank = "Bạc"; // Hạng mặc định
                    
                    // Phân ranh giới phân hạng (Ngưỡng tham khảo)
                    if (totalSpent >= 50000000) { 
                        newRank = "Kim cương"; // Chi tiêu trên 50 triệu
                    } else if (totalSpent >= 20000000) { 
                        newRank = "Vàng";      // Chi tiêu trên 20 triệu
                    }
                    
                    // Cập nhật hạng vào bảng guests thông qua DAO
                    guestDAO.updateRank(cccd, newRank);
                    System.out.println("Cập nhật thành công hạng của khách " + cccd + " thành: " + newRank);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tính toán xếp hạng khách hàng: " + e.getMessage());
        }
    }

    @Override
    public boolean isBlacklisted(String cccd) {
        // Giả định kiến trúc mở rộng sẽ có bảng `blacklists(cccd, reason, date)` 
        // Hoặc có thể thêm cột `is_blacklisted` (BOOLEAN) trực tiếp vào bảng `guests`
        String sql = "SELECT 1 FROM blacklists WHERE cccd = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setString(1, cccd);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Trả về true nếu CCCD tồn tại trong danh sách đen
            }
        } catch (SQLException e) {
            // Fallback an toàn: Trả về false nếu bảng chưa được khởi tạo để không làm sập luồng Check-in
            System.err.println("Cảnh báo tra cứu Blacklist: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Guest getCustomerProfile(String cccd) {
        Guest guest = guestDAO.findByCccd(cccd);
        if (guest != null) {
            System.out.println("Hồ sơ định danh hợp lệ. Khách hàng: " + guest.getName());
        } else {
            System.out.println("Hồ sơ không tồn tại với CCCD: " + cccd);
        }
        return guest;
    }

    public void addCustomer(Guest guest) {
        // Kiểm tra tính toàn vẹn dữ liệu trước khi thêm mới (Tránh Duplicate Entry Exception)
        if (guestDAO.findByCccd(guest.getCccd()) == null) {
            boolean isSuccess = guestDAO.insert(guest);
            if (isSuccess) {
                System.out.println("Lưu trữ hồ sơ thành công: " + guest.getName());
            } else {
                System.err.println("Lỗi thao tác CSDL khi lưu hồ sơ khách.");
            }
        } else {
            System.out.println("Khách hàng với định danh " + guest.getCccd() + " đã tồn tại trong hệ thống.");
        }
    }
}