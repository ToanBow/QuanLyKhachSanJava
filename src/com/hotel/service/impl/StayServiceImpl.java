package com.hotel.service.impl;

import com.hotel.model.Guest;
import com.hotel.model.Invoice;
import com.hotel.model.Service;
import com.hotel.service.IStayService;

public class StayServiceImpl implements IStayService {
    @Override
    public boolean checkIn(String roomId, Guest guest, Invoice bookingDetails) {
        // TODO: Lưu thông tin khách và hình thức thuê, tự động lấy giờ từ hệ thống [cite: 3, 4]
        return false;
    }

    @Override
    public boolean updateStayInformation(String roomId, Guest updatedGuest) {
        // TODO: Chỉnh sửa hoặc thêm thông tin khách hàng/phòng thuê sau khi đã đặt [cite: 7]
        return false;
    }

    @Override
    public void manageRoomServices(String roomId, Service service, int quantity, boolean isAddition) {
        // TODO: Thêm hoặc xóa dịch vụ cho phòng đang có khách [cite: 5, 6]
    }

    @Override
    public Invoice processCheckOut(String roomId) {
        // TODO: Tính tiền thực tế, phụ thu sớm/muộn, áp dụng giảm giá và đổi trạng thái phòng [cite: 8-12]
        return null;
    }

    @Override
    public void printInvoice(String invoiceId) {
        // TODO: Xuất và in hóa đơn chi tiết [cite: 12]
    }
}