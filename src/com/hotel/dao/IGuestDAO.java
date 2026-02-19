package com.hotel.dao;

import com.hotel.model.Guest;
import java.util.List;

public interface IGuestDAO {
    // Tìm khách hàng theo CCCD/Passport để kiểm tra lịch sử hoặc danh sách đen [cite: 3, 29]
    Guest findByCccd(String cccd);

    // Lưu thông tin khách hàng mới khi Check-in [cite: 3]
    boolean insert(Guest guest);

    // Cập nhật hạng thành viên (Bạc, Vàng, Kim cương) dựa trên tần suất lưu trú [cite: 28]
    boolean updateRank(String cccd, String newRank);

    // Truy vấn lịch sử lưu trú của một khách hàng [cite: 27]
    List<Guest> getAllCustomers();
}