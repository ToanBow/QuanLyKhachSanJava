package com.hotel.view.room;

public enum RoomStatus {

    AVAILABLE("Trống"),
    OCCUPIED("Có khách"),
    CLEANING("Đang dọn");

    private final String text;

    RoomStatus(String text){
        this.text = text;
    }

    public String getText(){
        return text;
    }
}