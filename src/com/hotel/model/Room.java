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

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setBeds(int beds) {
        this.beds = beds;
    }

    public void setDailyPrice(double dailyPrice) {
        this.dailyPrice = dailyPrice;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    // Getters và Setters 

    public String getRoomId() {
        return roomId;
    }

    public int getFloor() {
        return floor;
    }

    public String getType() {
        return type;
    }

    public int getBeds() {
        return beds;
    }

    public double getDailyPrice() {
        return dailyPrice;
    }

    public double getHourlyPrice() {
        return hourlyPrice;
    }

    public void setHourlyPrice(double hourlyPrice) {
        this.hourlyPrice = hourlyPrice;
    }

    public String getStatus() {
        return status;
    }

    
    
    // (Lưu ý: Status dùng để render màu sắc trên giao diện sơ đồ phòng [cite: 2])
}