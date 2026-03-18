package com.hotel.view.dashboard;

import com.hotel.service.impl.CustomerServiceImpl;
import com.hotel.service.impl.ReportServiceImpl;
import com.hotel.service.impl.RoomServiceImpl;
import com.hotel.service.impl.StayServiceImpl;
import com.hotel.view.customers.CustomerPanel;
import com.hotel.view.room.RoomMapPanel;
import com.hotel.view.invoice.InvoicePanel;
import com.hotel.view.setting.SettingPanel;
import com.hotel.app.DailyReportScheduler;
import com.hotel.util.SessionManager;
import com.hotel.util.AuditLogUtil;
import com.hotel.util.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MainDashboard extends JFrame {

    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JLabel headerTitle;
    private List<JPanel> menuItems = new ArrayList<>();

    private RoomServiceImpl roomService;
    private StayServiceImpl stayService;
    private ReportServiceImpl reportService;
    private CustomerServiceImpl customerService;

    private final Color PRIMARY_COLOR = new Color(26, 35, 126);
    private final Color HOVER_COLOR = new Color(40, 53, 147);
    private final Color ACTIVE_COLOR = new Color(13, 71, 161);
    private final Color BG_COLOR = new Color(245, 247, 250);
    private final Color HEADER_COLOR = new Color(255, 255, 255);
    private final Font MENU_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private final Font ICON_FONT = new Font("Segoe UI Emoji", Font.PLAIN, 18);

    public MainDashboard() {
        roomService = new RoomServiceImpl();
        stayService = new StayServiceImpl();
        reportService = new ReportServiceImpl();
        customerService = new CustomerServiceImpl();

        DailyReportScheduler.startDailyReportTask("shopdieusao246206@gmail.com");

        setTitle("Hệ thống Quản lý Khách sạn Ánh Trăng");
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);
        add(createMainArea(), BorderLayout.CENTER);

        // Tự động click vào Menu hợp lệ đầu tiên tùy theo quyền của User
        if (!menuItems.isEmpty()) {
            SwingUtilities.invokeLater(() -> {
                MouseEvent clickEvent = new MouseEvent(menuItems.get(0), MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, 0, 0, 1, false);
                menuItems.get(0).dispatchEvent(clickEvent);
            });
        }
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(PRIMARY_COLOR);
        sidebar.setPreferredSize(new Dimension(250, getHeight()));

        sidebar.add(createLogo());
        sidebar.add(Box.createVerticalStrut(30));

        String role = SessionManager.getCurrentUser() != null 
                      ? SessionManager.getCurrentUser().getRole() : "Quản lý";

        boolean canViewDashboard = false, canViewRoom = false, canViewCustomer = false;
        boolean canViewInvoice = false, canViewStat = false, canViewSetting = false;

        if ("Quản lý".equals(role)) {
            // Quản lý luôn có full quyền truy cập
            canViewDashboard = canViewRoom = canViewCustomer = canViewInvoice = canViewStat = canViewSetting = true;
        } else {
            String roleKey = "";
            if ("Lễ tân".equals(role)) roleKey = "RBAC_LETAN";
            else if ("Kế toán".equals(role)) roleKey = "RBAC_KETOAN";
            else if ("Buồng phòng".equals(role)) roleKey = "RBAC_BUONGPHONG";

            String permissions = getSystemSetting(roleKey, "");
            
            // Default Fallback
            if (permissions.isEmpty()) {
                if ("Lễ tân".equals(role)) permissions = "DASHBOARD,ROOM,CUSTOMER,INVOICE";
                if ("Kế toán".equals(role)) permissions = "INVOICE,STATISTIC";
                if ("Buồng phòng".equals(role)) permissions = "ROOM";
            }

            if (permissions.contains("DASHBOARD")) canViewDashboard = true;
            if (permissions.contains("ROOM")) canViewRoom = true;
            if (permissions.contains("CUSTOMER")) canViewCustomer = true;
            if (permissions.contains("INVOICE")) canViewInvoice = true;
            if (permissions.contains("STATISTIC")) canViewStat = true;
            if (permissions.contains("SETTING")) canViewSetting = true;
        }

        if (canViewDashboard) { sidebar.add(createMenuItem("🏠", "Tổng quan", "DASHBOARD")); sidebar.add(Box.createVerticalStrut(5)); }
        if (canViewRoom) { sidebar.add(createMenuItem("🛏️", "Sơ đồ phòng", "ROOM")); sidebar.add(Box.createVerticalStrut(5)); }
        if (canViewCustomer) { sidebar.add(createMenuItem("👥", "Khách hàng", "CUSTOMER")); sidebar.add(Box.createVerticalStrut(5)); }
        if (canViewInvoice) { sidebar.add(createMenuItem("🧾", "Hóa đơn & Thanh toán", "INVOICE")); sidebar.add(Box.createVerticalStrut(5)); }
        if (canViewStat) { sidebar.add(createMenuItem("📊", "Báo cáo Thống kê", "STATISTIC")); sidebar.add(Box.createVerticalStrut(5)); }
        if (canViewSetting) { sidebar.add(createMenuItem("⚙️", "Cài đặt hệ thống", "SETTING")); }

        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    private JPanel createLogo() {
        JPanel panel = new JPanel(new BorderLayout()); panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(new EmptyBorder(30, 20, 10, 20)); panel.setMaximumSize(new Dimension(250, 100));
        JLabel iconLabel = new JLabel("🏨 "); iconLabel.setForeground(Color.WHITE); iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        JLabel textLabel = new JLabel("<html>HOTEL<br>ÁNH TRĂNG</html>"); textLabel.setForeground(Color.WHITE); textLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(iconLabel, BorderLayout.WEST); panel.add(textLabel, BorderLayout.CENTER); return panel;
    }

    private JPanel createMenuItem(String icon, String text, String cardName) {
        JPanel panel = new JPanel(new BorderLayout(15, 0)); panel.setMaximumSize(new Dimension(250, 50));
        panel.setBackground(PRIMARY_COLOR); panel.setBorder(new EmptyBorder(10, 20, 10, 20)); panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JLabel iconLabel = new JLabel(icon); iconLabel.setForeground(Color.WHITE); iconLabel.setFont(ICON_FONT);
        JLabel textLabel = new JLabel(text); textLabel.setForeground(Color.WHITE); textLabel.setFont(MENU_FONT);
        panel.add(iconLabel, BorderLayout.WEST); panel.add(textLabel, BorderLayout.CENTER);
        menuItems.add(panel);

        panel.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent evt) { if (panel.getBackground() != ACTIVE_COLOR) panel.setBackground(HOVER_COLOR); }
            @Override public void mouseExited(MouseEvent evt) { if (panel.getBackground() != ACTIVE_COLOR) panel.setBackground(PRIMARY_COLOR); }
            @Override public void mouseClicked(MouseEvent evt) {
                for (JPanel menuItem : menuItems) { menuItem.setBackground(PRIMARY_COLOR); menuItem.setBorder(new EmptyBorder(10, 20, 10, 20)); }
                panel.setBackground(ACTIVE_COLOR);
                panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 5, 0, 0, Color.ORANGE), new EmptyBorder(10, 15, 10, 20)));
                cardLayout.show(contentPanel, cardName); headerTitle.setText("Quản lý / " + text);
            }
        });
        return panel;
    }

    private JPanel createMainArea() {
        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.add(createHeader(), BorderLayout.NORTH); mainArea.add(createContentPanel(), BorderLayout.CENTER);
        return mainArea;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout()); header.setBackground(HEADER_COLOR); header.setPreferredSize(new Dimension(getWidth(), 60));
        header.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(224, 224, 224)), new EmptyBorder(0, 30, 0, 30)));
        headerTitle = new JLabel("Quản lý / Tổng quan"); headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 18)); headerTitle.setForeground(new Color(33, 33, 33)); header.add(headerTitle, BorderLayout.WEST);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0)); userPanel.setBackground(HEADER_COLOR);
        JLabel lblUserIcon = new JLabel("👤"); lblUserIcon.setFont(ICON_FONT);
        String roleName = SessionManager.getCurrentUser() != null ? SessionManager.getCurrentUser().getRole() : "Admin";
        JLabel lblUserName = new JLabel("Xin chào, " + roleName + "!"); lblUserName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JButton btnLogout = new JButton("Đăng xuất"); btnLogout.setBackground(new Color(239, 154, 154)); btnLogout.setForeground(Color.BLACK); btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 14)); btnLogout.setFocusPainted(false); btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String email = SessionManager.getCurrentUser() != null ? SessionManager.getCurrentUser().getEmail() : "admin@system";
                AuditLogUtil.log(email, "ĐĂNG XUẤT (Kết thúc phiên làm việc)");
                SessionManager.clearSession(); dispose();
                new com.hotel.view.LoginFrame().setVisible(true);
            }
        });
        userPanel.add(lblUserName); userPanel.add(lblUserIcon); userPanel.add(Box.createHorizontalStrut(10)); userPanel.add(btnLogout);
        header.add(userPanel, BorderLayout.EAST); return header;
    }

    private JPanel createContentPanel() {
        cardLayout = new CardLayout(); contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG_COLOR); contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.add(new DashboardPanel(reportService, roomService), "DASHBOARD");
        contentPanel.add(new RoomMapPanel(roomService, stayService), "ROOM");
        contentPanel.add(new CustomerPanel(customerService), "CUSTOMER");
        contentPanel.add(new InvoicePanel(stayService), "INVOICE");
        contentPanel.add(new StatisticsPanel(reportService), "STATISTIC");
        contentPanel.add(new SettingPanel(roomService), "SETTING");
        return contentPanel;
    }

    private String getSystemSetting(String key, String defaultValue) {
        String value = defaultValue;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT setting_value FROM system_settings WHERE setting_key = ?")) {
            ps.setString(1, key);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) value = rs.getString("setting_value"); }
        } catch (Exception e) { e.printStackTrace(); }
        return value;
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> { new MainDashboard().setVisible(true); });
    }
}