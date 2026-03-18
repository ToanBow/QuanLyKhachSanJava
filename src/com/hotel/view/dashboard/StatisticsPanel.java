package com.hotel.view.dashboard;

import com.hotel.service.impl.ReportServiceImpl;
import com.hotel.util.DBConnection;
import com.hotel.util.EmailReportUtil;
import com.hotel.util.PdfReportUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class StatisticsPanel extends JPanel {

    private ReportServiceImpl reportService;
    private final Color PRIMARY_COLOR = new Color(26, 35, 126);
    private JPanel chkPanel;
    private List<JCheckBox> managerCheckboxes = new ArrayList<>();
    
    private double netRev = 0, surcharge = 0, serviceRev = 0, roomRev = 0, occRate = 0;

    public StatisticsPanel(ReportServiceImpl reportService) {
        this.reportService = reportService;
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Thống kê & Phân tích Doanh thu (Tháng Hiện Tại)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(PRIMARY_COLOR);
        add(title, BorderLayout.NORTH);

        calculateMetrics();

        // FIX LỖI BỊ CHE: Đổi thành 3 dòng, 2 cột để các thẻ tự dàn trải ngang
        JPanel statsPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        statsPanel.setOpaque(false);
        statsPanel.add(new CardPanel("📊", "Công suất lấp đầy", String.format("%.1f%%", occRate), new Color(251, 192, 45)));
        statsPanel.add(new CardPanel("📈", "Dự báo tháng tới", "Tăng trưởng ổn định", new Color(0, 150, 136)));
        statsPanel.add(new CardPanel("💰", "Doanh Thu Thuần", String.format("%,.0f đ", netRev), new Color(198, 40, 40)));
        statsPanel.add(new CardPanel("🛏", "Tiền Phòng", String.format("%,.0f đ", roomRev), new Color(46, 125, 50)));
        statsPanel.add(new CardPanel("🛎", "Dịch Vụ & Phụ Thu", String.format("%,.0f đ", serviceRev + surcharge), new Color(13, 71, 161)));

        JPanel reportPanel = new JPanel(new BorderLayout());
        reportPanel.setBackground(Color.WHITE);
        reportPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblRep = new JLabel("Gửi Báo Cáo Định Kỳ");
        lblRep.setFont(new Font("Segoe UI", Font.BOLD, 18));
        reportPanel.add(lblRep, BorderLayout.NORTH);

        chkPanel = new JPanel();
        chkPanel.setLayout(new BoxLayout(chkPanel, BoxLayout.Y_AXIS));
        chkPanel.setBackground(Color.WHITE);
        chkPanel.setBorder(new TitledBorder("Chọn Quản lý nhận báo cáo:"));
        loadManagers();
        reportPanel.add(new JScrollPane(chkPanel), BorderLayout.CENTER);

        JButton btnSend = new JButton("Gửi Báo Cáo PDF");
        btnSend.setBackground(new Color(144, 202, 249)); // Xanh dương nhạt
        btnSend.setForeground(Color.BLACK); // CHỮ ĐEN
        btnSend.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSend.setPreferredSize(new Dimension(200, 40));
        
        btnSend.addActionListener(e -> sendPdfReport(btnSend));
        reportPanel.add(btnSend, BorderLayout.SOUTH);

        JPanel centerWrapper = new JPanel(new GridLayout(1, 2, 20, 0));
        centerWrapper.setOpaque(false);
        centerWrapper.add(statsPanel);
        centerWrapper.add(reportPanel);

        add(centerWrapper, BorderLayout.CENTER);
    }

    private void calculateMetrics() {
        occRate = reportService.getOccupancyRate();
        try (Connection conn = DBConnection.getConnection()) {
            String sqlNet = "SELECT SUM(total_amount) as net, SUM(early_surcharge + late_surcharge) as sur FROM invoices WHERE payment_date IS NOT NULL AND MONTH(payment_date) = MONTH(CURRENT_DATE())";
            PreparedStatement ps1 = conn.prepareStatement(sqlNet);
            ResultSet rs1 = ps1.executeQuery();
            if (rs1.next()) {
                netRev = rs1.getDouble("net");
                surcharge = rs1.getDouble("sur");
            }
            String sqlSvc = "SELECT SUM(su.quantity * su.price_at_time) as svc FROM service_usage su JOIN invoices i ON su.invoice_id = i.invoice_id WHERE i.payment_date IS NOT NULL AND MONTH(i.payment_date) = MONTH(CURRENT_DATE())";
            PreparedStatement ps2 = conn.prepareStatement(sqlSvc);
            ResultSet rs2 = ps2.executeQuery();
            if (rs2.next()) {
                serviceRev = rs2.getDouble("svc");
            }
            roomRev = netRev - surcharge - serviceRev;
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadManagers() {
        chkPanel.removeAll();
        managerCheckboxes.clear();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT email FROM users WHERE role = 'Quản lý'")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                JCheckBox chk = new JCheckBox(rs.getString("email"));
                chk.setBackground(Color.WHITE);
                chk.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                managerCheckboxes.add(chk);
                chkPanel.add(chk);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void sendPdfReport(JButton btnSend) {
        boolean hasSelected = managerCheckboxes.stream().anyMatch(JCheckBox::isSelected);
        if (!hasSelected) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất 1 Quản lý để nhận email!");
            return;
        }

        btnSend.setEnabled(false);
        btnSend.setText("Đang khởi tạo & Gửi...");

        new Thread(() -> {
            try {
                String pdfPath = PdfReportUtil.createReport(netRev, occRate);
                for (JCheckBox chk : managerCheckboxes) {
                    if (chk.isSelected()) {
                        EmailReportUtil.sendReport(chk.getText(), pdfPath);
                    }
                }
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Đã tạo và gửi Báo cáo PDF thành công!");
                    btnSend.setEnabled(true);
                    btnSend.setText("Gửi Báo Cáo PDF");
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Lỗi gửi mail: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    btnSend.setEnabled(true);
                    btnSend.setText("Gửi Báo Cáo PDF");
                });
            }
        }).start();
    }
}