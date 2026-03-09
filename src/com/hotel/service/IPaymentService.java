package com.hotel.service;

public interface IPaymentService {
    /**
   Quản lý phương thức: QR Code động, Thẻ tín dụng, Ví điện tử. [cite: 30]
     */
    void processPayment(String method, double amount);

    /**
    Quản lý công nợ khách đoàn hoặc các đại lý lữ hành (OTA). [cite: 31]
     */
    void manageAgencyDebt(String agencyId, double amount);

    /**
    Đối soát luồng tiền theo ca làm việc (Shift report) của nhân viên lễ tân. [cite: 32]
     */
    void generateShiftReport(String employeeId);
}