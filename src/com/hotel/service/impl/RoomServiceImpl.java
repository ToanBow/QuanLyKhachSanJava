package com.hotel.service.impl;
import com.hotel.dao.IRoomDAO;
import com.hotel.dao.impl.RoomDAOImpl;
import com.hotel.model.Room;
import com.hotel.service.IRoomService;
import java.util.List;


public class RoomServiceImpl implements IRoomService {

    private IRoomDAO roomDAO = new RoomDAOImpl();

    @Override
    public List<Room> getRoomMap() {
        //Lấy danh sách phòng hiển thị dạng lưới, phân biệt tầng và màu sắc trạng thái [cite: 2]
        return roomDAO.getAllRooms();
    }

    @Override
    public void updatePricingPolicy(String roomTypeId, double dayPrice, double hourPrice) {
        // Thiết lập bảng giá linh hoạt theo giờ, ngày, tuần, mùa [cite: 18, 19]
        roomDAO.updatePricing(roomTypeId, dayPrice, hourPrice);
    }

    @Override
    public void setMaintenance(String roomId, String reason, String expectedFinishDate) {
        // Chuyển sang trạng thái "Đang sửa chữa" kèm lý do [cite: 20]
        roomDAO.updateStatus(roomId,"Đang sửa chữa");
    }

    @Override
    public void confirmCleaningStatus(String roomId) {
        // Xác nhận dọn xong để đưa phòng về trạng thái "Sẵn sàng" [cite: 13]
         roomDAO.updateStatus(roomId,"Sẵn sàng");
    }
}