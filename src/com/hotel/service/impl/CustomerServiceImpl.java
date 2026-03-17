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
import java.util.ArrayList;
import java.util.List;

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

    public java.util.List<Guest> getAllCustomers() {
        java.util.List<Guest> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM guests ORDER BY name ASC";
        
        try (java.sql.Connection conn = com.hotel.util.DBConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                // SỬ DỤNG SETTER ĐỂ CHỐNG LƯU NGƯỢC DỮ LIỆU
                Guest g = new Guest(); 
                g.setCccd(rs.getString("cccd"));
                g.setName(rs.getString("name"));
                g.setPhone(rs.getString("phone"));
                g.setEmail(rs.getString("email"));
                g.setGender(rs.getString("gender"));
                g.setNationality(rs.getString("nationality"));
                g.setRank(rs.getString("rank"));
                list.add(g);
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void addCustomer(Guest guest) {
        // Dùng UPSERT: Nếu chưa có thì Thêm, nếu CCCD đã tồn tại thì Sửa
        String sql = "INSERT INTO guests (cccd, name, phone, email) VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE name=VALUES(name), phone=VALUES(phone), email=VALUES(email)";
        try (java.sql.Connection conn = com.hotel.util.DBConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, guest.getCccd());
            ps.setString(2, guest.getName());
            ps.setString(3, guest.getPhone());
            ps.setString(4, guest.getEmail());
            ps.executeUpdate();
            System.out.println("Thao tác dữ liệu khách hàng thành công!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}