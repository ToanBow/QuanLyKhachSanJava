package com.hotel.service;
public interface ISettingService {
    /**
     * [cite_start]Cấu hình tên khách sạn, địa chỉ, mã số thuế in trên hóa đơn. [cite: 33]
     */
    void updateHotelInfo(String name, String address, String taxCode);

    /**
     * [cite_start]Cài đặt giờ Check-in/Check-out quy chuẩn và định dạng tiền tệ. [cite: 35]
     */
    void setOperatingParameters(String checkInTime, String checkOutTime, int rounding);

    /**
     * [cite_start]Cấu hình SMTP gửi mail và kết nối thiết bị ngoại vi (máy in, khóa từ). [cite: 36]
     */
    void setupHardwareConnections(String smtpServer, String printerAddress);
}