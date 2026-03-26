package com.hotel.util;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.io.File;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PdfReportUtil {

    public static String createReport(double revenue, double occupancy) {
        try {
            File folder = new File("reports");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            String filePath = "reports/hotel_report_" + System.currentTimeMillis() + ".pdf";
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // 1. Chèn Logo
            try {
                String logoPath = "logo/logo.jpg";
                Image logo = new Image(ImageDataFactory.create(logoPath));
                logo.setWidth(80);
                logo.setFixedPosition(40, 750); // Chỉnh vị trí logo góc trái
                document.add(logo);
            } catch (Exception e) {
                System.err.println("Không tìm thấy logo: " + e.getMessage());
            }

            // 2. Tiêu đề Khách sạn
            document.add(new Paragraph("KHACH SAN ANH TRANG").setBold().setFontSize(18).setMarginLeft(100));
            document.add(new Paragraph("Dia chi: 123 pho Cau Giay, phuong Lang, TP.Ha Noi").setMarginLeft(100));
            document.add(new Paragraph("------------------------------------------------------------------").setMarginTop(20));

            // 3. Tiêu đề Báo cáo
            Paragraph title = new Paragraph("BAO CAO DOANH THU VA HOAT DONG")
                    .setBold()
                    .setFontSize(20)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(30)
                    .setMarginBottom(10);
            document.add(title);

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            document.add(new Paragraph("Thoi gian lap bao cao: " + dtf.format(LocalDateTime.now())).setItalic().setTextAlignment(TextAlignment.RIGHT));
            document.add(new Paragraph("Nguoi xuat: Admin dep trai, xinh gai").setItalic().setTextAlignment(TextAlignment.RIGHT).setMarginBottom(20));

            // 4. Bảng số liệu chi tiết
            Table table = new Table(UnitValue.createPercentArray(new float[]{50, 50}));
            table.setWidth(UnitValue.createPercentValue(100));

            Cell header1 = new Cell().add(new Paragraph("CHI SO THONG KE").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER);
            Cell header2 = new Cell().add(new Paragraph("GIA TRI DAT DUOC").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER);
            
            table.addCell(header1);
            table.addCell(header2);

            DecimalFormat money = new DecimalFormat("#,###");

            table.addCell(new Cell().add(new Paragraph("Tong Doanh Thu Thuc Te (VND)")).setPadding(10));
            table.addCell(new Cell().add(new Paragraph(money.format(revenue) + " VND")).setPadding(10).setTextAlignment(TextAlignment.RIGHT).setBold());

            table.addCell(new Cell().add(new Paragraph("Hieu Suat Lap Day Phong (%)")).setPadding(10));
            table.addCell(new Cell().add(new Paragraph(String.format("%.2f %%", occupancy))).setPadding(10).setTextAlignment(TextAlignment.RIGHT).setBold());

            document.add(table);

            // 5. Chữ ký / Lời kết
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Ghi chu: Bao cao nay duoc xuat tu dong tu He thong Quan ly Khach san. So lieu duoc lay dua tren tinh hinh kinh doanh thuc te tinh den thoi diem tao.")
                    .setItalic().setFontColor(ColorConstants.DARK_GRAY));

            document.close();
            System.out.println("[INFO] Tao PDF thanh cong: " + filePath);
            return filePath;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}