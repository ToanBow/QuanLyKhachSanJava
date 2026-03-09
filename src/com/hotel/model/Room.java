package com.hotel.model;

public class Room {
    private String roomId; // Số phòng 
    private int floor; // Tầng 
    private String type; // Standard, Deluxe, Suite, VIP 
    private int beds; // Số giường 
    private double dailyPrice; // Giá ngày 
    private double hourlyPrice; // Giá giờ 
    private String status; // Sẵn sàng, Có khách, Đang dọn, Đang sửa chữa 

    public Room() {
    }

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

}
    // (Lưu ý: Status dùng để render màu sắc trên giao diện sơ đồ phòng)