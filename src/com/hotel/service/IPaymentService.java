package com.hotel.service;

import com.hotel.model.Invoice;

public interface IPaymentService {
    /**
   Quản lý phương thức: QR Code động, Thẻ tín dụng, Ví điện tử. 
     */
    void processPayment(String method, double amount);

    /**
    Quản lý công nợ khách đoàn hoặc các đại lý lữ hành (OTA). 
     */
    void manageAgencyDebt(String agencyId, double amount);

    /**
    Đối soát luồng tiền theo ca làm việc (Shift report) của nhân viên lễ tân. 
     */
    void generateShiftReport(String employeeId);

    void printInvoiceToFile(Invoice invoice);
}