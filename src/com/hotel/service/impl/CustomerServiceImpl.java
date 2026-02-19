package com.hotel.service.impl;

import com.hotel.model.Guest;
import com.hotel.service.ICustomerService;

public class CustomerServiceImpl implements ICustomerService {
    @Override
    public void updateCustomerRank(String cccd) {
        // TODO: Tự động xếp hạng (Bạc, Vàng, Kim cương) theo tần suất lưu trú [cite: 28]
    }

    @Override
    public boolean isBlacklisted(String cccd) {
        // TODO: Cảnh báo khách hàng vi phạm nội quy (Blacklist) [cite: 29]
        return false;
    }

    @Override
    public Guest getCustomerProfile(String cccd) {
        // TODO: Truy xuất hồ sơ định danh và lịch sử lưu trú (CRM) [cite: 27]
        return null;
    }
}