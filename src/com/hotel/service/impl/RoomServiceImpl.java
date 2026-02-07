package com.hotel.service.impl;

import com.hotel.model.Room;
import com.hotel.service.IRoomService;
import java.util.List;

public class RoomServiceImpl implements IRoomService {
    @Override
    public List<Room> getRoomMap() {
        // TODO: Lấy danh sách phòng hiển thị dạng lưới, phân biệt tầng và màu sắc trạng thái [cite: 2]
        return null;
    }

    @Override
    public void updatePricingPolicy(String roomTypeId, double dayPrice, double hourPrice) {
        // TODO: Thiết lập bảng giá linh hoạt theo giờ, ngày, tuần, mùa [cite: 18, 19]
    }

    @Override
    public void setMaintenance(String roomId, String reason, String expectedFinishDate) {
        // TODO: Chuyển sang trạng thái "Đang sửa chữa" kèm lý do [cite: 20]
    }

    @Override
    public void confirmCleaningStatus(String roomId) {
        // TODO: Xác nhận dọn xong để đưa phòng về trạng thái "Sẵn sàng" [cite: 13]
    }
}