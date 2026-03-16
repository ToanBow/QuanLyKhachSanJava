package com.hotel.util;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.File;
import java.time.LocalDate;
import java.text.DecimalFormat;

public class PdfReportUtil {

    public static String createReport(double revenue, double occupancy) {

        try {

            // 1️⃣ Tạo thư mục reports nếu chưa tồn tại
            File folder = new File("reports");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // 2️⃣ Tạo đường dẫn file
            String filePath = "reports/hotel_report_" + System.currentTimeMillis() + ".pdf";

            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            DecimalFormat money = new DecimalFormat("#,###");

            // Tiêu đề
            Paragraph title = new Paragraph("BÁO CÁO KHÁCH SẠN")
                    .setBold()
                    .setFontSize(20)
                    .setTextAlignment(TextAlignment.CENTER);

            document.add(title);

            document.add(new Paragraph("Ngày tạo: " + LocalDate.now()));
            document.add(new Paragraph(" "));

            // Bảng dữ liệu
            Table table = new Table(2);

            table.addCell("Chỉ số");
            table.addCell("Giá trị");

            table.addCell("Tổng doanh thu");
            table.addCell(money.format(revenue) + " VND");

            table.addCell("Tỷ lệ lấp đầy");
            table.addCell(String.format("%.2f %%", occupancy));

            document.add(table);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Báo cáo được tạo từ hệ thống quản lý khách sạn."));

            document.close();

            System.out.println("[INFO] Tạo PDF thành công: " + filePath);

            return filePath;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}