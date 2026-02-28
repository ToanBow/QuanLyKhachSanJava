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

    public String getServiceId() {
        return serviceId;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public int getInventory() {
        return inventory;
    }

    public double getPrice() {
        return price;
    }

    public String getUnit() {
        return unit;
    }

    public double getVatRate() {
        return vatRate;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setInventory(int inventory) {
        this.inventory = inventory;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setVatRate(double vatRate) {
        this.vatRate = vatRate;
    }
}