package com.hotel.dao.impl;

import com.hotel.dao.IGuestDAO;
import com.hotel.model.Guest;
import com.hotel.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GuestDAOImpl implements IGuestDAO {

    @Override
    public Guest findByCccd(String cccd) {
        String sql = "SELECT * FROM guests WHERE cccd = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, cccd);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Sử dụng constructor hiện có trong model Guest
                    Guest guest = new Guest(
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("cccd")
                    );
                    
                    // Set các thuộc tính còn lại
                    guest.setGender(rs.getString("gender"));
                    guest.setBirthDate(rs.getDate("birth_date"));
                    guest.setHomeTown(rs.getString("home_town"));
                    guest.setNationality(rs.getString("nationality"));
                    guest.setRank(rs.getString("rank"));
                    
                    return guest;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean insert(Guest guest) {
        // Lưu ý: Trường `rank` là từ khóa trong MySQL nên cần đặt trong dấu backtick (`)
        String sql = "INSERT INTO guests (cccd, name, phone, gender, birth_date, home_town, email, nationality, `rank`) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, guest.getCccd());
            ps.setString(2, guest.getName());
            ps.setString(3, guest.getPhone());
            ps.setString(4, guest.getGender());
            
            // Xử lý chuyển đổi java.util.Date sang java.sql.Date
            if (guest.getBirthDate() != null) {
                ps.setDate(5, new java.sql.Date(guest.getBirthDate().getTime()));
            } else {
                ps.setNull(5, Types.DATE);
            }
            
            ps.setString(6, guest.getHomeTown());
            ps.setString(7, guest.getEmail());
            ps.setString(8, guest.getNationality());
            
            // Cài đặt hạng mặc định nếu chưa có
            ps.setString(9, guest.getRank() != null ? guest.getRank() : "Bạc");
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateRank(String cccd, String newRank) {
        String sql = "UPDATE guests SET `rank` = ? WHERE cccd = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, newRank);
            ps.setString(2, cccd);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Guest> getAllCustomers() {
        List<Guest> list = new ArrayList<>();
        String sql = "SELECT * FROM guests";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Guest guest = new Guest(
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("cccd")
                );
                guest.setGender(rs.getString("gender"));
                guest.setBirthDate(rs.getDate("birth_date"));
                guest.setHomeTown(rs.getString("home_town"));
                guest.setNationality(rs.getString("nationality"));
                guest.setRank(rs.getString("rank"));
                
                list.add(guest);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}