package com.hotel.service;
import com.hotel.model.Guest;
import com.hotel.model.Invoice;
import com.hotel.model.Service;

public interface IStayService {
    /**
     * [cite_start]Check-in: Nhập thông tin khách (Tên, CCCD, SĐT, Quê quán, Quốc tịch...) và hình thức thuê. [cite: 3, 4]
     * [cite_start]Tự động lấy ngày giờ check-in từ hệ thống. [cite: 4]
     */
    boolean checkIn(String roomId, Guest guest, Invoice bookingDetails);

    /**
     * [cite_start]Chỉnh sửa hoặc thêm thông tin khách hàng/phòng thuê sau khi đã đặt. [cite: 7]
     */
    boolean updateStayInformation(String roomId, Guest updatedGuest);

    /**
     * [cite_start]Thêm hoặc xóa dịch vụ (Dọn phòng, giặt đồ, đồ ăn...) cho phòng đang có khách. [cite: 5, 6]
     */
    void manageRoomServices(String roomId, Service service, int quantity, boolean isAddition);

    /**
     * [cite_start]Check-out: Tự động tính tiền thực tế, phụ thu sớm/muộn và áp dụng giảm giá. [cite: 8, 9, 10, 11]
     * [cite_start]Sau trả phòng, hệ thống tự chuyển trạng thái sang "Chưa dọn". [cite: 12]
     */
    Invoice processCheckOut(String roomId);

    /**
     * [cite_start]Xuất và in hóa đơn chi tiết dịch vụ và phương thức thanh toán. [cite: 12]
     */
    void printInvoice(String invoiceId);
}