package com.hotel.service.impl;

import com.hotel.dao.IGuestDAO;
import com.hotel.dao.IRoomDAO;
import com.hotel.dao.impl.GuestDAOImpl;
import com.hotel.dao.impl.RoomDAOImpl;
import com.hotel.model.Guest;
import com.hotel.model.Invoice;
import com.hotel.model.Room;
import com.hotel.service.IPaymentService;
import com.hotel.util.DBConnection;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PaymentServiceImpl implements IPaymentService {

    @Override
    public void processPayment(String method, double amount) {
        double vatRate = 0.1;
        double total = amount * (1 + vatRate);
        System.out.println("\n========== HỆ THỐNG THANH TOÁN ==========");
        System.out.println("Tổng tiền cần thanh toán (gồm VAT): " + String.format("%,.0f", total) + " VNĐ");
        System.out.println("Phương thức lựa chọn: " + method);
    }

    @Override
    public void manageAgencyDebt(String agencyId, double amount) {
        String sql = "INSERT INTO agency_debts (agency_id, debt_amount, record_date) VALUES (?, ?, NOW()) "
                   + "ON DUPLICATE KEY UPDATE debt_amount = debt_amount + ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, agencyId);
            ps.setDouble(2, amount);
            ps.setDouble(3, amount);
            ps.executeUpdate();
        } catch(Exception e){
            e.printStackTrace();
        } 
    }

    @Override
    public void generateShiftReport(String employeeId) {
        System.out.println("Đang tạo báo cáo ca...");
    }

    @Override
    public void printInvoiceToFile(Invoice invoice) {
        if (invoice == null) {
            System.err.println("Lỗi: Hóa đơn trống!");
            return;
        }

        IGuestDAO guestDAO = new GuestDAOImpl();
        IRoomDAO roomDAO = new RoomDAOImpl();
        Guest guest = guestDAO.findByCccd(invoice.getGuestCccd());
        Room room = roomDAO.findByRoomId(invoice.getRoomId());
        
        String guestName = (guest != null && guest.getName() != null) ? guest.getName() : "Khach vang lai";
        String roomType = (room != null && room.getType() != null) ? room.getType() : "Standard";

        try {
            File folder = new File("invoices");
            if (!folder.exists()) folder.mkdirs();

            String fileName = "invoices/HoaDon_" + invoice.getInvoiceId() + ".pdf";
            PdfWriter writer = new PdfWriter(new FileOutputStream(fileName));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            DecimalFormat money = new DecimalFormat("#,###");
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            // 1. PHẦN HEADER: LOGO VÀ THÔNG TIN KHÁCH SẠN
            Table headerTable = new Table(UnitValue.createPercentArray(new float[]{30, 70})).useAllAvailableWidth();
            try {
                Image logo = new Image(ImageDataFactory.create("logo/logo.jpg"));
                logo.setWidth(90);
                headerTable.addCell(new Cell().add(logo).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
            } catch (Exception e) {
                headerTable.addCell(new Cell().add(new Paragraph("HOTEL LOGO")).setBorder(Border.NO_BORDER));
            }

            Cell hotelInfo = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT);
            hotelInfo.add(new Paragraph("KHACH SAN ANH TRANG").setBold().setFontSize(18).setFontColor(new DeviceRgb(26, 35, 126)));
            hotelInfo.add(new Paragraph("Dia chi: 123 Cau Giay, Phuong Lang, Ha Noi"));
            hotelInfo.add(new Paragraph("Hotline: 024 1234 5678 | Email: contact@anhtrang.com"));
            headerTable.addCell(hotelInfo);
            document.add(headerTable);
            
            document.add(new Paragraph("---------------------------------------------------------------------------------------------------------").setFontColor(ColorConstants.GRAY));

            // 2. TIÊU ĐỀ HÓA ĐƠN
            document.add(new Paragraph("HOA DON DICH VU (INVOICE)").setBold().setFontSize(22).setTextAlignment(TextAlignment.CENTER).setMarginTop(10));
            document.add(new Paragraph("Ma HD: " + invoice.getInvoiceId()).setTextAlignment(TextAlignment.CENTER).setFontSize(11).setFontColor(ColorConstants.DARK_GRAY));
            document.add(new Paragraph("\n"));

            // 3. THÔNG TIN KHÁCH HÀNG & LƯU TRÚ
            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth();
            
            Cell guestCell = new Cell().setBorder(Border.NO_BORDER);
            guestCell.add(new Paragraph("THONG TIN KHACH HANG").setBold().setFontColor(new DeviceRgb(46, 125, 50)));
            guestCell.add(new Paragraph("Khach hang: " + guestName));
            guestCell.add(new Paragraph("CCCD/Passport: " + invoice.getGuestCccd()));
            
            Cell stayCell = new Cell().setBorder(Border.NO_BORDER);
            stayCell.add(new Paragraph("THONG TIN LUU TRU").setBold().setFontColor(new DeviceRgb(46, 125, 50)));
            stayCell.add(new Paragraph("Phong: " + invoice.getRoomId() + " (" + roomType + ")"));
            
            String checkInStr = invoice.getCheckInTime() != null ? invoice.getCheckInTime().format(dtf) : "N/A";
            String checkOutStr = invoice.getCheckOutTime() != null ? invoice.getCheckOutTime().format(dtf) : LocalDateTime.now().format(dtf);
            stayCell.add(new Paragraph("Check-in:  " + checkInStr));
            stayCell.add(new Paragraph("Check-out: " + checkOutStr));
            
            infoTable.addCell(guestCell);
            infoTable.addCell(stayCell);
            document.add(infoTable);
            document.add(new Paragraph("\n"));

            // 4. BẢNG CHI TIẾT CHI PHÍ
            Table billTable = new Table(UnitValue.createPercentArray(new float[]{10, 45, 10, 15, 20})).useAllAvailableWidth();
            billTable.addHeaderCell(new Cell().add(new Paragraph("STT").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            billTable.addHeaderCell(new Cell().add(new Paragraph("NOI DUNG").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            billTable.addHeaderCell(new Cell().add(new Paragraph("SL").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            billTable.addHeaderCell(new Cell().add(new Paragraph("DON GIA").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.RIGHT));
            billTable.addHeaderCell(new Cell().add(new Paragraph("THANH TIEN").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.RIGHT));

            int stt = 1;
            double serviceTotal = 0;

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT s.name, su.quantity, su.price_at_time FROM service_usage su JOIN services s ON su.service_id = s.service_id WHERE su.invoice_id = ?")) {
                ps.setString(1, invoice.getInvoiceId());
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String sName = rs.getString("name");
                    int qty = rs.getInt("quantity");
                    double price = rs.getDouble("price_at_time");
                    double sub = qty * price;
                    serviceTotal += sub;
                    
                    billTable.addCell(new Cell().add(new Paragraph(String.valueOf(stt++))).setTextAlignment(TextAlignment.CENTER));
                    billTable.addCell(new Cell().add(new Paragraph(sName)));
                    billTable.addCell(new Cell().add(new Paragraph(String.valueOf(qty))).setTextAlignment(TextAlignment.CENTER));
                    billTable.addCell(new Cell().add(new Paragraph(money.format(price))).setTextAlignment(TextAlignment.RIGHT));
                    billTable.addCell(new Cell().add(new Paragraph(money.format(sub))).setTextAlignment(TextAlignment.RIGHT));
                }
            } catch (Exception e) { e.printStackTrace(); }

            double totalAmount = invoice.getTotalAmount();
            double deposit = invoice.getDeposit();
            double discount = invoice.getDiscount();
            double earlySur = invoice.getEarlySurcharge();
            double lateSur = invoice.getLateSurcharge();
            
            double subTotal = (totalAmount + deposit) / (1.0 - discount / 100.0);
            double roomPrice = subTotal - serviceTotal - earlySur - lateSur;
            if (roomPrice < 0) roomPrice = 0; 
            
            billTable.addCell(new Cell().add(new Paragraph(String.valueOf(stt++))).setTextAlignment(TextAlignment.CENTER));
            billTable.addCell(new Cell().add(new Paragraph("Tien thue phong (" + invoice.getRentalType() + ")")));
            billTable.addCell(new Cell().add(new Paragraph("1")).setTextAlignment(TextAlignment.CENTER));
            billTable.addCell(new Cell().add(new Paragraph(money.format(roomPrice))).setTextAlignment(TextAlignment.RIGHT));
            billTable.addCell(new Cell().add(new Paragraph(money.format(roomPrice))).setTextAlignment(TextAlignment.RIGHT));

            if (earlySur > 0) {
                billTable.addCell(new Cell().add(new Paragraph(String.valueOf(stt++))).setTextAlignment(TextAlignment.CENTER));
                billTable.addCell(new Cell().add(new Paragraph("Phu thu nhan phong som")));
                billTable.addCell(new Cell().add(new Paragraph("1")).setTextAlignment(TextAlignment.CENTER));
                billTable.addCell(new Cell().add(new Paragraph(money.format(earlySur))).setTextAlignment(TextAlignment.RIGHT));
                billTable.addCell(new Cell().add(new Paragraph(money.format(earlySur))).setTextAlignment(TextAlignment.RIGHT));
            }
            if (lateSur > 0) {
                billTable.addCell(new Cell().add(new Paragraph(String.valueOf(stt++))).setTextAlignment(TextAlignment.CENTER));
                billTable.addCell(new Cell().add(new Paragraph("Phu thu tra phong muon")));
                billTable.addCell(new Cell().add(new Paragraph("1")).setTextAlignment(TextAlignment.CENTER));
                billTable.addCell(new Cell().add(new Paragraph(money.format(lateSur))).setTextAlignment(TextAlignment.RIGHT));
                billTable.addCell(new Cell().add(new Paragraph(money.format(lateSur))).setTextAlignment(TextAlignment.RIGHT));
            }

            document.add(billTable);
            document.add(new Paragraph("\n"));

            // 5. TỔNG KẾT THANH TOÁN
            Table sumTable = new Table(UnitValue.createPercentArray(new float[]{70, 30})).useAllAvailableWidth();
            double discountAmt = subTotal * (discount / 100.0);
            
            sumTable.addCell(new Cell().add(new Paragraph("Tong cong (Subtotal):")).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
            sumTable.addCell(new Cell().add(new Paragraph(money.format(subTotal) + " VND")).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
            
            if (discountAmt > 0) {
                sumTable.addCell(new Cell().add(new Paragraph("Giam gia (Discount " + discount + "%):")).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
                sumTable.addCell(new Cell().add(new Paragraph("-" + money.format(discountAmt) + " VND")).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT).setFontColor(ColorConstants.RED));
            }
            if (deposit > 0) {
                sumTable.addCell(new Cell().add(new Paragraph("Da dat coc (Deposit):")).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
                sumTable.addCell(new Cell().add(new Paragraph("-" + money.format(deposit) + " VND")).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT).setFontColor(ColorConstants.RED));
            }
            
            sumTable.addCell(new Cell().add(new Paragraph("TONG THANH TOAN (TOTAL):").setBold().setFontColor(new DeviceRgb(198, 40, 40))).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
            sumTable.addCell(new Cell().add(new Paragraph(money.format(totalAmount) + " VND").setBold().setFontSize(16).setFontColor(new DeviceRgb(198, 40, 40))).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
            
            document.add(sumTable);

            // 6. MÃ QR CODE THANH TOÁN (Lấy thông tin từ CSDL)
            String bankId = "VCB";
            String accNo = "1045181602"; 
            String accName = "DANG THE TOAN";
            
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT setting_key, setting_value FROM system_settings WHERE setting_key IN ('BANK_ID', 'BANK_ACCOUNT', 'BANK_ACCOUNT_NAME')")) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String key = rs.getString("setting_key");
                    String val = rs.getString("setting_value");
                    if ("BANK_ID".equals(key) && !val.isEmpty()) bankId = val;
                    if ("BANK_ACCOUNT".equals(key) && !val.isEmpty()) accNo = val;
                    if ("BANK_ACCOUNT_NAME".equals(key) && !val.isEmpty()) accName = val;
                }
            } catch (Exception e) { e.printStackTrace(); }

            try {
                String addInfoEncoded = URLEncoder.encode("Thanh toan HD " + invoice.getInvoiceId(), "UTF-8");
                String accNameEncoded = URLEncoder.encode(accName, "UTF-8");
                String qrUrl = "https://img.vietqr.io/image/" + bankId + "-" + accNo + "-compact2.png"
                        + "?amount=" + (long)totalAmount + "&addInfo=" + addInfoEncoded + "&accountName=" + accNameEncoded;
                
                byte[] qrBytes = fetchImageFromUrl(qrUrl);
                Image qrImage = new Image(ImageDataFactory.create(qrBytes));
                qrImage.setWidth(140);
                qrImage.setHorizontalAlignment(HorizontalAlignment.CENTER);
                
                document.add(new Paragraph("\nQUET MA DE THANH TOAN").setBold().setTextAlignment(TextAlignment.CENTER).setFontColor(ColorConstants.DARK_GRAY));
                document.add(qrImage);
            } catch (Exception ex) {
                System.err.println("Sử dụng QR văn bản thay thế: " + ex.getMessage());
                byte[] qrBytes = generateQRCodeBytes("Chuyen khoan " + (long)totalAmount + " VND. Noi dung: " + invoice.getInvoiceId(), 150, 150);
                Image fallbackQr = new Image(ImageDataFactory.create(qrBytes)).setHorizontalAlignment(HorizontalAlignment.CENTER);
                document.add(fallbackQr);
            }
            
            document.add(new Paragraph("\nCam on Quy khach da tin tuong va su dung dich vu cua chung toi!").setItalic().setTextAlignment(TextAlignment.CENTER).setFontColor(ColorConstants.GRAY));

            document.close();
            System.out.println("=> Xuất Hóa đơn PDF thành công tại: " + fileName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] fetchImageFromUrl(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0"); 
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        
        try (InputStream in = connection.getInputStream();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int n;
            while ((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }
            return out.toByteArray();
        }
    }

    private byte[] generateQRCodeBytes(String text, int width, int height) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }
}