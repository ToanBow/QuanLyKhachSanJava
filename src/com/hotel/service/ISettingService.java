package com.hotel.service;
public interface ISettingService {
    /**
    Cấu hình tên khách sạn, địa chỉ, mã số thuế in trên hóa đơn.
     */
    void updateHotelInfo(String name, String address, String taxCode);

    /**
    Cài đặt giờ Check-in/Check-out quy chuẩn và định dạng tiền tệ. 
     */
    void setOperatingParameters(String checkInTime, String checkOutTime, int rounding);

    /**
    Cấu hình SMTP gửi mail và kết nối thiết bị ngoại vi (máy in, khóa từ). 
     */
    void setupHardwareConnections(String smtpServer, String printerAddress);
}