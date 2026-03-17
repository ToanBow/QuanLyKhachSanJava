package com.hotel.service.impl;

import com.hotel.model.Invoice;
import com.hotel.service.IPaymentService;
import com.hotel.dao.IInvoiceDAO;
import com.hotel.dao.impl.InvoiceDAOImpl;
import java.time.format.DateTimeFormatter;

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
    // in hoa don ra file pdf
    public void printInvoiceToFile(Invoice invoice) {
        if (invoice == null) {
            System.err.println("Invoice null");
            return;
        }
        String fileName = "Invoice_" + invoice.getInvoiceId() + ".pdf";
        Document document = null;
        
        try {
            //khoi tao itext
            PdfWriter writer = new PdfWriter(new FileOutputStream(fileName));
            PdfDocument pdf = new PdfDocument(writer);
            document = new Document(pdf);
            //chen logo
            try {
                // SỬ DỤNG ĐƯỜNG DẪN TƯƠNG ĐỐI
                String logoPath = "logo/logo.jpg"; // Thư mục logo nằm ngang hàng với src
                Image logo = new Image(ImageDataFactory.create(logoPath));
                logo.setWidth(80); 
                logo.setFixedPosition(40, 750); 
                document.add(logo);
            } catch (Exception e) {
                System.err.println("Không tìm thấy logo tại đường dẫn: " + e.getMessage());
            }
            //tieu de
            document.add(new Paragraph("KHACH SAN ANH TRANG").setBold().setFontSize(18).setMarginLeft(100));
            document.add(new Paragraph("Dia chi: 123 pho Cau Giay, phuong Lang, TP.Ha Noi").setMarginLeft(100));
            document.add(new Paragraph("------------------------------------------------------------------").setMarginTop(20));

            //thong tin chi tiet
            document.add(new Paragraph("Ma hoa don: " + invoice.getInvoiceId()));
            document.add(new Paragraph("Phong: " + invoice.getRoomId()));
            document.add(new Paragraph("Khach hang: " + invoice.getGuestCccd()));

            //Thong tin hoa don
            document.add(new Paragraph("HOA DON THANH TOAN").setBold().setFontSize(16).setHorizontalAlignment(HorizontalAlignment.CENTER));
            document.add(new Paragraph("Ma hoa don: " + invoice.getInvoiceId()));
            document.add(new Paragraph("Khach hang (CCCD): " + invoice.getGuestCccd()));
            document.add(new Paragraph("Phong: " + invoice.getRoomId() + " | Loai hinh: " + invoice.getRentalType()));
            
            //Ngay gio check-in check-out
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String checkInStr = (invoice.getCheckInTime() != null) 
                    ? invoice.getCheckInTime().format(formatter) 
                    : "N/A";
            document.add(new Paragraph("Thoi gian Check-in:  " + checkInStr));

            String checkOutStr = java.time.LocalDateTime.now().format(formatter);
            document.add(new Paragraph("Thoi gian Check-out: " + checkOutStr));

            //Chi tiet dich vu
            Table table = new Table(UnitValue.createPercentArray(new float[]{40, 10, 20, 30}));
            table.setWidth(UnitValue.createPercentValue(100));
    
            table.addCell(new Paragraph("Noi dung").setBold());
            table.addCell(new Paragraph("SL").setBold());
            table.addCell(new Paragraph("Don gia").setBold());
            table.addCell(new Paragraph("Thanh tien (VND)").setBold());

            //Tien phong goc
            table.addCell("Tien thue phong");
            table.addCell("1");
            table.addCell(String.format("%,.0f", invoice.getTotalAmount()));
            table.addCell(String.format("%,.0f", invoice.getTotalAmount()));

            //Lay dich vụ khach hang dung tu DB
            double serviceTotal = 0;
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "SELECT s.name, su.quantity, su.price_at_time, (su.price_at_time * su.quantity) as subtotal " +
                             "FROM service_usage su " +
                             "JOIN services s ON su.service_id = s.service_id " +
                             "WHERE su.invoice_id = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, invoice.getInvoiceId());
            
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    table.addCell(rs.getString("name"));
                    table.addCell(String.valueOf(rs.getInt("quantity")));
                    table.addCell(String.format("%,.0f", rs.getDouble("price_at_time"))); // HIỂN THỊ ĐƠN GIÁ
                    double sub = rs.getDouble("subtotal");
                    table.addCell(String.format("%,.0f", sub));
                    serviceTotal += sub;
                }
            } catch (Exception e) { 
                System.err.println("Loi lay dich vu: " + e.getMessage()); 
            }
                document.add(table);

        // tinh toan va giam gia
            double subTotal = invoice.getTotalAmount() + serviceTotal;
            double discountAmt = subTotal * (invoice.getDiscount() / 100.0);
            double tax = (subTotal - discountAmt) * 0.1; 
            double finalTotal = (subTotal - discountAmt) + tax - invoice.getDeposit();

            document.add(new Paragraph("\n-----------------------------------------"));
            document.add(new Paragraph(String.format("Tong chua giam: %,.0f VND", subTotal)));
            document.add(new Paragraph(String.format("Khuyen mai (%d%%): -%,.0f VND", (int)invoice.getDiscount(), discountAmt))
                        .setFontColor(com.itextpdf.kernel.colors.ColorConstants.RED)); 
            document.add(new Paragraph(String.format("Thue VAT (10%%): %,.0f VND", tax)));
            document.add(new Paragraph(String.format("Da dat coc: -%,.0f VND", invoice.getDeposit())));
            document.add(new Paragraph(String.format("TONG TIEN THANH TOAN: %,.0f VND", finalTotal)).setBold().setFontSize(14));

            // ma QR
            String stk = "0812789637";
            String nganHang = "MB"; // Tên viết tắt ngân hàng
            String tenChuTK = "HOANG THI THU HUE";
            String noiDung = "Thanh toan " + invoice.getInvoiceId();

            // Tao duong dan VietQR
            String vietQrUrl = "https://img.vietqr.io/image/" + nganHang + "-" + stk + "-compact2.jpg?amount=" 
                             + (int)finalTotal 
                             + "&addInfo=" + noiDung.replace(" ", "%20") 
                             + "&accountName=" + tenChuTK.replace(" ", "%20");

            try {
                //tai anh QR truc tiep vao pdf
                Image qrImage = new Image(ImageDataFactory.create(vietQrUrl));
                qrImage.setWidth(180); 
                qrImage.setHorizontalAlignment(HorizontalAlignment.CENTER);
                document.add(new Paragraph("\nQUET MA DE THANH TOAN").setHorizontalAlignment(HorizontalAlignment.CENTER).setBold());
                document.add(qrImage);
            } catch (Exception ex) {
                System.err.println("Loi tai anh QR tu VietQR: " + ex.getMessage());
                String backupText = "NGAN HANG: " + nganHang + "\nSTK: " + stk + "\nSO TIEN: " + (int)finalTotal;
                byte[] qrBytes = generateQRCodeBytes(backupText, 200, 200);
                Image backupQr = new Image(ImageDataFactory.create(qrBytes)).setHorizontalAlignment(HorizontalAlignment.CENTER);
                document.add(backupQr);
            }

            document.add(new Paragraph("\nCam on Quy khach, hen gap lai!").setItalic().setHorizontalAlignment(HorizontalAlignment.CENTER));

            } catch (Exception e) { 
                System.err.println("Loi in hoa don: " + e.getMessage());
                e.printStackTrace(); 
            } finally {
                if (document != null) {
                    document.close();
                }
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