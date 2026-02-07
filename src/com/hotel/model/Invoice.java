package com.hotel.model;
import java.util.Date;
import java.util.List;

public class Invoice {
    private String invoiceId;
    private String roomId;
    private String guestCccd;
    private Date checkInTime;    // Tự động lấy từ máy [cite: 4]
    private Date checkOutTime;
    private String rentalType;   // Theo ngày/Theo giờ [cite: 4]
    private double deposit;      // Tiền trả trước [cite: 4]
    private double earlySurcharge; // Phụ thu check-in sớm [cite: 10]
    private double lateSurcharge;  // Phụ thu check-out muộn [cite: 9]
    private double discount;     // Giảm giá % [cite: 11]
    private List<ServiceUsage> services; // Danh sách dịch vụ đã dùng [cite: 8]
    private String paymentMethod; // Tiền mặt, tín dụng, nợ [cite: 11]
    private double totalAmount;  // Tổng tiền sau thuế và giảm giá [cite: 7]

    public Invoice() {}
    
    // Logic tính toán: Total = (RoomPrice + Surcharges + Services) * (1 - Discount)
}