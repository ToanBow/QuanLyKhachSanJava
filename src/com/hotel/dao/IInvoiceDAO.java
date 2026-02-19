package com.hotel.dao;

import com.hotel.model.Invoice;
import java.util.List;
import java.util.Date;

public interface IInvoiceDAO {
    // Lưu hóa đơn mới sau khi khách hàng thanh toán hoặc Check-out [cite: 7, 12]
    boolean insert(Invoice invoice);

    // Tìm hóa đơn của một phòng đang có khách (để tính tiền tạm tính) [cite: 6]
    Invoice findActiveInvoiceByRoom(String roomId);

    // Lấy danh sách hóa đơn theo khoảng thời gian để báo cáo doanh thu [cite: 14, 26]
    List<Invoice> getInvoicesByPeriod(Date startDate, Date endDate);

    // Thống kê doanh thu theo ca làm việc của nhân viên [cite: 32]
    double getRevenueByShift(String employeeId, Date shiftTime);
}