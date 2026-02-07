package com.hotel.dao;

import com.hotel.model.Room;
import java.util.List;

public interface IRoomDAO {
    // Lấy danh sách phòng để hiển thị dạng lưới trên sơ đồ [cite: 2]
    List<Room> getAllRooms();

    // Tìm thông tin chi tiết của một phòng theo số phòng [cite: 5]
    Room findByRoomId(String roomId);

    // Cập nhật trạng thái (Sẵn sàng, Có khách, Đang dọn, Đang sửa chữa) [cite: 2, 20]
    boolean updateStatus(String roomId, String status);

    // Cấu hình lại giá phòng hoặc thuộc tính phòng [cite: 17, 18]
    boolean updatePricing(String roomTypeId, double dailyPrice, double hourlyPrice);
}