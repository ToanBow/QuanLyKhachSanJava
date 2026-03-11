package com.hotel.service.impl;

import com.hotel.model.Invoice;
import com.hotel.service.IPaymentService;
import com.hotel.dao.IInvoiceDAO;
import com.hotel.dao.impl.InvoiceDAOImpl;

//itext7
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.HorizontalAlignment;
//zxing, xu ly ma qr
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
//file va luonng
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import com.hotel.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;

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
        if (invoice == null) {
            System.err.println("Invoice null");
            return;
        }
        String fileName = "Invoice_" + invoice.getInvoiceId() + ".pdf";
        try {
            //khoi tao itext
            PdfWriter writer = new PdfWriter(new FileOutputStream(fileName));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            //tieu de
            Paragraph header = new Paragraph("HOA DON KHACH SAN");
            header.setFontSize(20f); 
            header.setBold();
            header.setMarginBottom(10f);
            document.add(header);

            //thong tin chi tiet
            document.add(new Paragraph("Ma hoa don: " + invoice.getInvoiceId()));
            document.add(new Paragraph("Phong: " + invoice.getRoomId()));
            document.add(new Paragraph("Khach hang: " + invoice.getGuestCccd()));

            //chi tiet dich vu
            document.add(new Paragraph("Chi tiet thanh toan:"));
            Table table = new Table(UnitValue.createPercentArray(new float[]{70, 30}));
            table.setWidth(UnitValue.createPercentValue(100));
            table.addCell("Khoan muc");
            table.addCell("So tien (VND)");
            
            table.addCell("Tien phong & Phu thu");
            table.addCell(String.format("%,.0f", invoice.getTotalAmount() + invoice.getDeposit()));
            
            table.addCell("Giam gia");
            table.addCell("-" + invoice.getDiscount() + "%");
            
            document.add(table);

            //tong tien
            document.add(new Paragraph("\nTONG TIEN THANH TOAN: " + String.format("%,.0f", invoice.getTotalAmount()) + " VNĐ")
                    .setBold().setFontSize(15));

            //tao va chen ma qr thanh toan
            String qrContent = "Banking: 123456789 | Amount: " + invoice.getTotalAmount() 
                             + " | Msg: " + invoice.getInvoiceId();
            
            byte[] qrBytes = generateQRCodeBytes(qrContent, 200, 200);
            Image qrImage = new Image(ImageDataFactory.create(qrBytes));
            qrImage.setHorizontalAlignment(HorizontalAlignment.CENTER);
            
            document.add(new Paragraph("\nQUET MA DE THANH TOAN").setHorizontalAlignment(HorizontalAlignment.CENTER));
            document.add(qrImage);

            document.close();
            System.out.println("Đã xuất hóa đơn PDF thành công: " + fileName);

        } catch (Exception e) {
            System.err.println("Lỗi tạo PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private byte[] generateQRCodeBytes(String text, int width, int height) throws Exception {
    QRCodeWriter qrCodeWriter = new QRCodeWriter();
    BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        
    ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
    MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
    return pngOutputStream.toByteArray();
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
        try {
            // Thay thế class ẩn bằng việc khởi tạo đối tượng DAO thực sự
            IInvoiceDAO invoiceDAO = new InvoiceDAOImpl();
            
            double total = invoiceDAO.getRevenueByShift(employeeId, new java.util.Date());
            
            System.out.println("--- BÁO CÁO DOANH THU CA ---");
            System.out.println("Nhân viên thực hiện: " + employeeId);
            System.out.println("Tổng tiền thu được: " + String.format("%,.0f VNĐ", total));
            System.out.println("----------------------------");
        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }
    
}