package com.hotel.view.room;

public enum RoomStatus {

    AVAILABLE("Sẵn sàng"),
    OCCUPIED("Có khách"),
    CLEANING("Chưa dọn"),
    MAINTENANCE("Đang sửa chữa");

    private final String text;

    RoomStatus(String text){
        this.text = text;
    }

    public String getText(){
        return text;
    }
}