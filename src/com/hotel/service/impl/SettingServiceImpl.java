package com.hotel.service.impl;

import com.hotel.service.ISettingService;

public class SettingServiceImpl implements ISettingService {
    @Override
    public void updateHotelInfo(String name, String address, String taxCode) {
        // TODO: Cấu hình thông tin khách sạn in trên hóa đơn [cite: 33]
    }

    @Override
    public void setOperatingParameters(String checkInTime, String checkOutTime, int rounding) {
        // TODO: Thiết lập giờ quy chuẩn và làm tròn tiền tệ [cite: 35]
    }

    @Override
    public void setupHardwareConnections(String smtpServer, String printerAddress) {
        // TODO: Cấu hình SMTP gửi mail và kết nối máy in hóa đơn, khóa từ [cite: 36]
    }
}