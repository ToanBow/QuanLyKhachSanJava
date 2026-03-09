package com.hotel.model;
import java.time.LocalDateTime;
import java.util.List;

public class Invoice {
    private String invoiceId;
    private String roomId;
    private String guestCccd;
    private LocalDateTime checkInTime;    // Tự động lấy từ máy
    private LocalDateTime checkOutTime;
    private String rentalType;   // Theo ngày/Theo giờ 
    private double deposit;      // Tiền trả trước 
    private double earlySurcharge; // Phụ thu check-in sớm 
    private double lateSurcharge;  // Phụ thu check-out muộn 
    private double discount;     // Giảm giá % 
    private List<ServiceUsage> services; // Danh sách dịch vụ đã dùng 
    private String paymentMethod; // Tiền mặt, tín dụng, nợ
    private double totalAmount; // Tổng tiền sau thuế và giảm giá
    private String status; //trạng thái hóa đơn

    public Invoice() {}
    
    // Logic tính toán: Total = (RoomPrice + Surcharges + Services) * (1 - Discount)

    public String getInvoiceId() {
        return invoiceId;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getGuestCccd() {
        return guestCccd;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public LocalDateTime getCheckOutTime() {
        return checkOutTime;
    }

    public String getRentalType() {
        return rentalType;
    }

    public double getDeposit() {
        return deposit;
    }

    public double getEarlySurcharge() {
        return earlySurcharge;
    }

    public double getLateSurcharge() {
        return lateSurcharge;
    }

    public double getDiscount() {
        return discount;
    }

    public List<ServiceUsage> getServices() {
        return services;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setGuestCccd(String guestCccd) {
        this.guestCccd = guestCccd;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public void setCheckOutTime(LocalDateTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public void setRentalType(String rentalType) {
        this.rentalType = rentalType;
    }

    public void setDeposit(double deposit) {
        this.deposit = deposit;
    }

    public void setEarlySurcharge(double earlySurcharge) {
        this.earlySurcharge = earlySurcharge;
    }

    public void setLateSurcharge(double lateSurcharge) {
        this.lateSurcharge = lateSurcharge;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public void setServices(List<ServiceUsage> services) {
        this.services = services;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
    //tinh tien thuc te
    public void calculateActualAmount(double roomPrice){
        double serviceTotal = 0;
        if(services != null){
            for (ServiceUsage su : services) {
                serviceTotal += su.getSubTotal();
            }
        }
        double subTotal = roomPrice + earlySurcharge + lateSurcharge + serviceTotal;
        //ap dung giam gia va tru tien dat coc
        double discountMoney = subTotal * (discount/100);
        this.totalAmount = (subTotal - discountMoney) - deposit;
        System.out.println("Đã hoàn tất hóa đơn "+ this.totalAmount);
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }


}