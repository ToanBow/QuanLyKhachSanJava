package com.hotel.dao;

import com.hotel.model.Room;
import java.util.List;

public interface IRoomDAO {
    int countAll();        // thêm
    int countOccupied();
    // Lấy danh sách phòng để hiển thị dạng lưới trên sơ đồ 
    List<Room> getAllRooms();

    // Tìm thông tin chi tiết của một phòng theo số phòng 
    Room findByRoomId(String roomId);

    // Cập nhật trạng thái (Sẵn sàng, Có khách, Đang dọn, Đang sửa chữa) 
    boolean updateStatus(String roomId, String status);

    // Cấu hình lại giá phòng hoặc thuộc tính phòng 
    boolean updatePricing(String roomTypeId, double dailyPrice, double hourlyPrice);
}