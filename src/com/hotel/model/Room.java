package com.hotel.model;
public class Room {
    private String roomId;      // Số phòng [cite: 5]
    private int floor;          // Tầng [cite: 5]
    private String type;        // Standard, Deluxe, Suite, VIP [cite: 16]
    private int beds;           // Số giường [cite: 5]
    private double dailyPrice;  // Giá ngày [cite: 5]
    private double hourlyPrice; // Giá giờ [cite: 5]
    private String status;      // Sẵn sàng, Có khách, Đang dọn, Đang sửa chữa [cite: 2, 20]

    public Room() {}

    public void setRoomId(String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setRoomId'");
    }

    public void setFloor(int int1) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setFloor'");
    }

    public void setType(String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setType'");
    }

    public void setBeds(int int1) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setBeds'");
    }

    public void setDailyPrice(double double1) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setDailyPrice'");
    }

    public void setStatus(String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setStatus'");
    }

    // Getters và Setters 
    // (Lưu ý: Status dùng để render màu sắc trên giao diện sơ đồ phòng [cite: 2])
}