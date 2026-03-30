package com.hotel.service;
import com.hotel.model.Guest;
import com.hotel.model.Invoice;
import com.hotel.model.Service;

public interface IStayService {
    /**
    Check-in: Nhập thông tin khách (Tên, CCCD, SĐT, Quê quán, Quốc tịch...) và hình thức thuê. 
    Tự động lấy ngày giờ check-in từ hệ thống. [cite: 4]
     */
    boolean checkIn(String roomId, Guest guest, Invoice bookingDetails);

    /**
    Chỉnh sửa hoặc thêm thông tin khách hàng/phòng thuê sau khi đã đặt. 
     */
    boolean updateStayInformation(String roomId, Guest updatedGuest);

    /**
    Thêm hoặc xóa dịch vụ (Dọn phòng, giặt đồ, đồ ăn...) cho phòng đang có khách.
     */
    void manageRoomServices(String roomId, Service service, int quantity, boolean isAddition);

    /**
    Check-out: Tự động tính tiền thực tế, phụ thu sớm/muộn và áp dụng giảm giá. 
    Sau trả phòng, hệ thống tự chuyển trạng thái sang "Chưa dọn".
     */
    Invoice processCheckOut(String roomId);

    /**
    Xuất và in hóa đơn chi tiết dịch vụ và phương thức thanh toán. 
     */
    void printInvoice(String invoiceId);
}