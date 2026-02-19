package com.hotel.model;
public class Service {
    private String serviceId;
    private String name;        // Tên dịch vụ [cite: 21]
    private String category;    // Mini bar, Giặt là, Spa... [cite: 21]
    private int inventory;      // Số lượng tồn kho [cite: 22]
    private double price;       // Giá bán [cite: 24]
    private String unit;        // Đơn vị tính [cite: 24]
    private double vatRate;     // Thuế suất VAT [cite: 24]

    public Service() {}
}