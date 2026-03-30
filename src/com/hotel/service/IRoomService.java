package com.hotel.service;

import com.hotel.model.Room;
import java.util.List;

public interface IRoomService {
    /**
     * [cite_start]Hiển thị danh sách phòng dưới dạng lưới, phân biệt tầng và màu sắc trạng thái.
     */
    List<Room> getRoomMap();

    /**
    Thiết lập bảng giá linh hoạt: Theo giờ, ngày, tuần, tháng hoặc theo mùa/lễ hội.
     */
    void updatePricingPolicy(String roomTypeId, double dayPrice, double hourPrice);

    /**
    Quản lý bảo trì: Chuyển trạng thái sang "Đang sửa chữa" kèm lý do và thời gian dự kiến. 
     */
    void setMaintenance(String roomId, String reason, String expectedFinishDate);

    /**
    Xác nhận dọn xong: Đưa phòng từ trạng thái "Chưa dọn" về "Sẵn sàng". 
     */
    void confirmCleaningStatus(String roomId);
}