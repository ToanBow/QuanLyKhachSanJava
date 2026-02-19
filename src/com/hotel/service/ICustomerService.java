package com.hotel.service;

import com.hotel.model.Guest;

public interface ICustomerService {
    /**
     * [cite_start]Phân hạng thành viên: Tự động xếp hạng (Bạc, Vàng, Kim cương) theo tần suất lưu trú. [cite: 28]
     */
    void updateCustomerRank(String cccd);

    /**
     * [cite_start]Quản lý danh sách đen (Blacklist): Cảnh báo khi khách vi phạm nội quy đặt phòng. [cite: 29]
     */
    boolean isBlacklisted(String cccd);

    /**
     * [cite_start]Lưu trữ lịch sử lưu trú, tổng chi tiêu và hồ sơ định danh. [cite: 27]
     */
    Guest getCustomerProfile(String cccd);
}