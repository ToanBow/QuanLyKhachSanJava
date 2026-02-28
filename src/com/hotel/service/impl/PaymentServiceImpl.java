package com.hotel.service.impl;

import com.hotel.model.Invoice;
import com.hotel.service.IPaymentService;

import com.hotel.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

public class PaymentServiceImpl implements IPaymentService {
    @Override
    public void processPayment(String method, double amount) {
        // TODO: Xử lý các phương thức thanh toán: QR Code, tín dụng, ví điện tử [cite: 30]
        double vatRate = 0.1; // thue 10%
        double total = amount * (1 + vatRate); //so 1 dai dien cho 100% gia tri goc ban dau
        
        System.out.println("\n========== HỆ THỐNG THANH TOÁN ==========");
        System.out.println("Tổng tiền cần thanh toán (gồm VAT): " + String.format("%,.0f", total) + " VNĐ");
        System.out.println("Phương thức lựa chọn: " + method);
        
    // tung phuong thuc thanh toans
    switch (method.toUpperCase()) {
        case "QR CODE":
        System.out.println("Đang tạo mã QR động...");
        // gia lap ma qr chua thong tin thanh toan
            String qrData = "PAY_INV_" + System.currentTimeMillis() + "_AMT_" + total;
            System.out.println("[QR]: mã_qr_image_data_[" + qrData + "]");
            System.out.println("Vui lòng quét mã để thanh toán.");
            break;
        case "VÍ ĐIỆN TỬ":
            System.out.println("Đang kết nối cổng thanh toán...");
            System.out.println("Vui lòng xác nhận trên điện thoại.");
            break;
        case "TÍN DỤNG":
            System.out.println("Đang kết nối máy quẹt thẻ (POS)...");
            System.out.println("Vui lòng quẹt/chạm thẻ.");
            break;
        default:
            System.out.println("Đang xử lý thanh toán tiền mặt.");
            break;
    }
    }
    // in hoa don ra file
    public void printInvoiceToFile(Invoice invoice) {
        String fileName = "Invoice_" + invoice.getInvoiceId() + ".txt";
        try (java.io.PrintWriter writer = new java.io.PrintWriter(fileName)) {
            writer.println("---------- HOTEL RECEIPT ----------");
            writer.println("Invoice ID: " + invoice.getInvoiceId());
            writer.println("Room: " + invoice.getRoomId());
            writer.println("Total Amount: " + invoice.getTotalAmount());
            writer.println("Method: " + invoice.getPaymentMethod());
            writer.println("-----------------------------------");
            System.out.println("Đã in hóa đơn thành công: " + fileName);
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }
    @Override
    public void manageAgencyDebt(String agencyId, double amount) {
        // TODO: Theo dõi công nợ của khách đoàn hoặc đại lý OTA [cite: 31]
        String sql = "INSERT INTO agency_debts (agency_id, debt_amount, record_date) VALUES (?, ?, NOW()) "
                   + "ON DUPLICATE KEY UPDATE debt_amount = debt_amount + ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, agencyId);
                ps.setDouble(2, amount);
                ps.setDouble(3, amount);

            int rows = ps.executeUpdate();
            if (rows > 0){   
                System.out.println("Đã ghi nhận nợ mới cho đại lý " + agencyId + " với số tiền " + amount);
            }
        } catch(Exception e){
            e.printStackTrace();
        } 
    }

    @Override
    public void generateShiftReport(String employeeId) {
        // TODO: Báo cáo chi tiết dòng tiền theo ca làm việc của lễ tân [cite: 32]
        try {
        // goi lop DAO de lay du lieu
        com.hotel.dao.IInvoiceDAO invoiceDAO = new com.hotel.dao.impl.InvoiceDAOImpl();
        double total = invoiceDAO.getRevenueByShift(employeeId, new java.util.Date());
        System.out.println("--- BÁO CÁO DOANH THU CA ---");
        System.out.println("Nhân viên thực hiện: " + employeeId);
        System.out.println("Tổng tiền thu được: " + total);
        System.out.println("----------------------------");
        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }
}