package com.hotel.service.impl;

import com.hotel.service.IPaymentService;

public class PaymentServiceImpl implements IPaymentService {
    @Override
    public void processPayment(String method, double amount) {
        // TODO: Xử lý các phương thức thanh toán: QR Code, tín dụng, ví điện tử [cite: 30]
    }

    @Override
    public void manageAgencyDebt(String agencyId, double amount) {
        // TODO: Theo dõi công nợ của khách đoàn hoặc đại lý OTA [cite: 31]
    }

    @Override
    public void generateShiftReport(String employeeId) {
        // TODO: Báo cáo chi tiết dòng tiền theo ca làm việc của lễ tân [cite: 32]
    }
}