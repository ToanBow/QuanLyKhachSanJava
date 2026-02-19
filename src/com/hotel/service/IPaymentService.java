package com.hotel.service;

public interface IPaymentService {
    /**
     * [cite_start]Quản lý phương thức: QR Code động, Thẻ tín dụng, Ví điện tử. [cite: 30]
     */
    void processPayment(String method, double amount);

    /**
     * [cite_start]Quản lý công nợ khách đoàn hoặc các đại lý lữ hành (OTA). [cite: 31]
     */
    void manageAgencyDebt(String agencyId, double amount);

    /**
     * [cite_start]Đối soát luồng tiền theo ca làm việc (Shift report) của nhân viên lễ tân. [cite: 32]
     */
    void generateShiftReport(String employeeId);
}