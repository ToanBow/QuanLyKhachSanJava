package com.hotel.service;

import com.hotel.model.Guest;
import com.hotel.service.ICustomerService;
public interface ICustomerService {
    /**
    Phân hạng thành viên: Tự động xếp hạng (Bạc, Vàng, Kim cương) theo tần suất lưu trú. 
     */
    void updateCustomerRank(String cccd);

    /**
    Quản lý danh sách đen (Blacklist): Cảnh báo khi khách vi phạm nội quy đặt phòng. 
     */
    boolean isBlacklisted(String cccd);

    /**
    Lưu trữ lịch sử lưu trú, tổng chi tiêu và hồ sơ định danh. 
     */
    Guest getCustomerProfile(String cccd);
}