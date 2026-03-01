package com.hotel.dao.impl;


import com.hotel.dao.IRoomDAO;
import com.hotel.model.Room;
import com.hotel.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAOImpl implements IRoomDAO {

    // Lấy danh sách phòng để hiển thị dạng lưới trên sơ đồ [cite: 2]
    @Override
    public List<Room> getAllRooms() {

        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Room r = new Room();
                r.setRoomId(rs.getString("room_id"));
                r.setFloor(rs.getInt("floor"));
                r.setType(rs.getString("type"));
                r.setBeds(rs.getInt("beds"));
                r.setDailyPrice(rs.getDouble("daily_price"));
                r.setHourlyPrice(rs.getDouble("hourly_price"));
                r.setStatus(rs.getString("status"));

                rooms.add(r);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rooms;
    }

    // Tìm thông tin chi tiết của một phòng theo số phòng [cite: 5]
    @Override
    public Room findByRoomId(String roomId) {

        String sql = "SELECT * FROM rooms WHERE room_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, roomId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Room r = new Room();

                r.setRoomId(rs.getString("room_id"));
                r.setFloor(rs.getInt("floor"));
                r.setType(rs.getString("type"));
                r.setBeds(rs.getInt("beds"));
                r.setDailyPrice(rs.getDouble("daily_price"));
                r.setDailyPrice(rs.getDouble("hourly_price"));
                r.setStatus(rs.getString("status"));

                return r;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Cập nhật trạng thái (Sẵn sàng, Có khách, Đang dọn, Đang sửa chữa) [cite: 2, 20]
    @Override
    public boolean updateStatus(String roomId, String status) {

        String sql =
                "UPDATE rooms SET status=? WHERE room_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setString(2, roomId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Cấu hình lại giá phòng hoặc thuộc tính phòng [cite: 17, 18]    
    @Override
    public boolean updatePricing(String roomTypeId,
                                 double dailyPrice,
                                 double hourlyPrice) {

        String sql =
            "UPDATE rooms SET daily_price=?, hourly_price=? WHERE type=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, dailyPrice);
            ps.setDouble(2, hourlyPrice);
            ps.setString(3, roomTypeId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}