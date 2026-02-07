package com.hotel.service;

import com.hotel.model.Room;
import java.util.List;

public interface IRoomService {
    /**
     * [cite_start]Hiển thị danh sách phòng dưới dạng lưới, phân biệt tầng và màu sắc trạng thái. [cite: 2]
     */
    List<Room> getRoomMap();

    /**
     * [cite_start]Thiết lập bảng giá linh hoạt: Theo giờ, ngày, tuần, tháng hoặc theo mùa/lễ hội. [cite: 18, 19]
     */
    void updatePricingPolicy(String roomTypeId, double dayPrice, double hourPrice);

    /**
     * [cite_start]Quản lý bảo trì: Chuyển trạng thái sang "Đang sửa chữa" kèm lý do và thời gian dự kiến. [cite: 20]
     */
    void setMaintenance(String roomId, String reason, String expectedFinishDate);

    /**
     * [cite_start]Xác nhận dọn xong: Đưa phòng từ trạng thái "Chưa dọn" về "Sẵn sàng". [cite: 13]
     */
    void confirmCleaningStatus(String roomId);
}