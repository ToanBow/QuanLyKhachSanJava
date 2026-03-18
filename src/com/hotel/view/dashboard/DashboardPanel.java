package com.hotel.view.dashboard;

import com.hotel.service.impl.ReportServiceImpl;
import com.hotel.service.impl.RoomServiceImpl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;

public class DashboardPanel extends JPanel {

    private final Color BG_COLOR = new Color(245, 247, 250);
    private final Color CARD_COLOR = Color.WHITE;
    private final Color ACCENT_COLOR = new Color(13, 71, 161);

    private ReportServiceImpl reportService;
    private RoomServiceImpl roomService; // Bổ sung RoomService để đếm số phòng

    // Nhận cả 2 Service từ MainDashboard truyền vào
    public DashboardPanel(ReportServiceImpl reportService, RoomServiceImpl roomService) {
        this.reportService = reportService;
        this.roomService = roomService;

        setLayout(new BorderLayout());
        setBackground(BG_COLOR);

        add(createHeader(), BorderLayout.NORTH);
        add(createCenter(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Tổng quan Hệ thống (Tháng " + LocalDate.now().getMonthValue() + ")");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        panel.add(title, BorderLayout.WEST);
        
        // Thêm nút Làm mới
        JButton btnReload = new JButton("Làm mới Dữ liệu");
        btnReload.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnReload.setBackground(new Color(238, 238, 238));
        btnReload.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReload.addActionListener(e -> refreshDashboard());
        
        panel.add(btnReload, BorderLayout.EAST);

        return panel;
    }

    private JPanel createCenter() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        panel.add(createCards(), BorderLayout.NORTH);
        
        // ĐÃ MỞ KHÓA: Thêm khu vực Biểu đồ Doanh thu vào giữa màn hình
        panel.add(createChartSection(), BorderLayout.CENTER); 
        
        return panel;
    }

    private JPanel createCards() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 30, 30));
        panel.setOpaque(false);

        // 1. LẤY DỮ LIỆU THỰC TỪ DAO/SERVICE
        int totalRooms = roomService.countAll();
        int occupiedRooms = roomService.countOccupied();
        int availableRooms = totalRooms - occupiedRooms;
        
        // Lấy doanh thu tháng hiện tại
        int currentMonth = LocalDate.now().getMonthValue();
        int currentYear = LocalDate.now().getYear();
        double revenue = reportService.calculateRevenue(currentMonth, currentYear);

        // Tỷ lệ lấp đầy
        double occupancyRate = reportService.getOccupancyRate(); 

        // 2. RENDER RA UI BẰNG CARDPANEL
        panel.add(new CardPanel("🛏", "Phòng trống", String.valueOf(availableRooms), new Color(46, 125, 50)));
        panel.add(new CardPanel("👥", "Phòng có khách", String.valueOf(occupiedRooms), new Color(13, 71, 161)));
        panel.add(new CardPanel("📊", "Tỷ lệ lấp đầy", String.format("%.1f%%", occupancyRate), new Color(251, 192, 45)));
        panel.add(new CardPanel("💰", "Doanh thu (Tháng)", String.format("%,.0f đ", revenue), new Color(198, 40, 40)));

        return panel;
    }

    // TÍCH HỢP BIỂU ĐỒ DOANH THU 6 THÁNG GẦN NHẤT
    private JPanel createChartSection() {
        JPanel chartContainer = new JPanel(new BorderLayout());
        chartContainer.setOpaque(false);
        chartContainer.setBorder(new EmptyBorder(40, 0, 0, 0)); // Cách các thẻ phía trên 40px

        // Tiêu đề biểu đồ
        JLabel chartTitle = new JLabel("Thống kê doanh thu 6 tháng gần nhất");
        chartTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        chartTitle.setForeground(new Color(66, 66, 66));
        chartTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        chartContainer.add(chartTitle, BorderLayout.NORTH);

        // Chuẩn bị dữ liệu động
        double[] revData = new double[6];
        String[] labels = new String[6];

        LocalDate now = LocalDate.now();

        // Vòng lặp lùi về 6 tháng trước
        for (int i = 5; i >= 0; i--) {
            LocalDate targetMonth = now.minusMonths(i);
            int m = targetMonth.getMonthValue();
            int y = targetMonth.getYear();

            labels[5 - i] = "Tháng " + m; // Tạo nhãn (VD: Tháng 10)
            revData[5 - i] = reportService.calculateRevenue(m, y); // Gọi DAO truy xuất tiền
        }

        // Khởi tạo Component vẽ biểu đồ và nạp dữ liệu
        RevenueChartPanel chartPanel = new RevenueChartPanel(revData, labels);
        chartContainer.add(chartPanel, BorderLayout.CENTER);

        // Bọc vào một Panel trắng có bóng/bo góc để nhìn đẹp hơn
        JPanel cardWrapper = new JPanel(new BorderLayout());
        cardWrapper.setBackground(Color.WHITE);
        cardWrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
                new EmptyBorder(20, 20, 20, 20)));
        cardWrapper.add(chartContainer, BorderLayout.CENTER);

        // Wrapper phụ để giới hạn chiều cao biểu đồ không bị kéo giãn quá mức
        JPanel marginPanel = new JPanel(new BorderLayout());
        marginPanel.setOpaque(false);
        marginPanel.setBorder(new EmptyBorder(30, 0, 0, 0));
        marginPanel.add(cardWrapper, BorderLayout.CENTER);

        return marginPanel;
    }
    private void refreshDashboard() {
        removeAll(); // Xóa toàn bộ UI cũ
        add(createHeader(), BorderLayout.NORTH);
        add(createCenter(), BorderLayout.CENTER); // Hàm này tự động gọi DAO lấy dữ liệu mới nhất
        revalidate();
        repaint();
    }
}