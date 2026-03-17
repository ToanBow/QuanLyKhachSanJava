package com.hotel.view.dashboard;

import com.hotel.service.impl.CustomerServiceImpl;
import com.hotel.service.impl.ReportServiceImpl;
import com.hotel.service.impl.RoomServiceImpl;
import com.hotel.service.impl.StayServiceImpl;
import com.hotel.view.customers.CustomerPanel;
import com.hotel.view.room.RoomMapPanel;
import com.hotel.app.DailyReportScheduler;
import com.hotel.view.setting.SettingPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class MainDashboard extends JFrame {

    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JLabel headerTitle;
    private List<JPanel> menuItems = new ArrayList<>();

    // KHAI BÁO CÁC SERVICE CHUẨN ĐỂ FIX LỖI
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
        // KHỞI TẠO CÁC TÂN TỪ SERVICE (BẮT BUỘC TRƯỚC KHI TRUYỀN VÀO PANEL)
        roomService = new RoomServiceImpl();
        stayService = new StayServiceImpl();
        reportService = new ReportServiceImpl();
        customerService = new CustomerServiceImpl();

        // Kích hoạt tác vụ nền
        DailyReportScheduler.startDailyReportTask("shopdieusao246206@gmail.com");

        setTitle("Hệ thống Quản lý Khách sạn Ánh Trăng");
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);
        add(createMainArea(), BorderLayout.CENTER);

        if (!menuItems.isEmpty()) {
            MouseEvent clickEvent = new MouseEvent(menuItems.get(0), MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, 0, 0, 1, false);
            menuItems.get(0).dispatchEvent(clickEvent);
        }
    }

    // ================== SIDEBAR ==================
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(PRIMARY_COLOR);
        sidebar.setPreferredSize(new Dimension(250, getHeight()));

        sidebar.add(createLogo());
        sidebar.add(Box.createVerticalStrut(30));

        sidebar.add(createMenuItem("🏠", "Tổng quan", "DASHBOARD"));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(createMenuItem("🛏️", "Sơ đồ phòng", "ROOM"));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(createMenuItem("👥", "Khách hàng", "CUSTOMER"));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(createMenuItem("🧾", "Hóa đơn & Thanh toán", "INVOICE"));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(createMenuItem("⚙️", "Cài đặt hệ thống", "SETTING"));

        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    private JPanel createLogo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(new EmptyBorder(30, 20, 10, 20));
        panel.setMaximumSize(new Dimension(250, 100));

        JLabel iconLabel = new JLabel("🏨 ");
        iconLabel.setForeground(Color.WHITE);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));

        JLabel textLabel = new JLabel("<html>HOTEL<br>ÁNH TRĂNG</html>");
        textLabel.setForeground(Color.WHITE);
        textLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(textLabel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMenuItem(String icon, String text, String cardName) {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setMaximumSize(new Dimension(250, 50));
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setForeground(Color.WHITE);
        iconLabel.setFont(ICON_FONT);

        JLabel textLabel = new JLabel(text);
        textLabel.setForeground(Color.WHITE);
        textLabel.setFont(MENU_FONT);

        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(textLabel, BorderLayout.CENTER);
        menuItems.add(panel);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                if (panel.getBackground() != ACTIVE_COLOR) {
                    panel.setBackground(HOVER_COLOR);
                }
            }
            @Override
            public void mouseExited(MouseEvent evt) {
                if (panel.getBackground() != ACTIVE_COLOR) {
                    panel.setBackground(PRIMARY_COLOR);
                }
            }
            @Override
            public void mouseClicked(MouseEvent evt) {
                for (JPanel menuItem : menuItems) {
                    menuItem.setBackground(PRIMARY_COLOR);
                    menuItem.setBorder(new EmptyBorder(10, 20, 10, 20));
                }
                panel.setBackground(ACTIVE_COLOR);
                panel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 5, 0, 0, Color.ORANGE),
                        new EmptyBorder(10, 15, 10, 20)
                ));
                cardLayout.show(contentPanel, cardName);
                headerTitle.setText("Quản lý / " + text);
            }
        });
        return panel;
    }

    // ================== MAIN AREA ==================
    private JPanel createMainArea() {
        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.add(createHeader(), BorderLayout.NORTH);
        mainArea.add(createContentPanel(), BorderLayout.CENTER);
        return mainArea;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_COLOR);
        header.setPreferredSize(new Dimension(getWidth(), 60));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(224, 224, 224)),
                new EmptyBorder(0, 30, 0, 30)
        ));

        headerTitle = new JLabel("Quản lý / Tổng quan");
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerTitle.setForeground(new Color(33, 33, 33));
        header.add(headerTitle, BorderLayout.WEST);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userPanel.setBackground(HEADER_COLOR);
        
        JLabel lblUserIcon = new JLabel("👤");
        lblUserIcon.setFont(ICON_FONT);
        JLabel lblUserName = new JLabel("Xin chào, Admin!");
        lblUserName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        userPanel.add(lblUserName);
        userPanel.add(lblUserIcon);
        header.add(userPanel, BorderLayout.EAST);
        return header;
    }

    // ================== CONTENT PANEL TÍCH HỢP ==================
    private JPanel createContentPanel() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG_COLOR);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // ĐÃ FIX: Truyền chuẩn các Service vào Panel
        contentPanel.add(new DashboardPanel(reportService, roomService), "DASHBOARD");
        contentPanel.add(new RoomMapPanel(roomService, stayService), "ROOM");
        contentPanel.add(new CustomerPanel(customerService), "CUSTOMER");
        
        contentPanel.add(createPlaceholderPanel("Màn hình Quản lý Hóa Đơn"), "INVOICE");
        contentPanel.add(new SettingPanel(roomService), "SETTING");

        return contentPanel;
    }

    private JPanel createPlaceholderPanel(String text) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(224, 224, 224), 1));
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.ITALIC, 24));
        label.setForeground(Color.GRAY);
        panel.add(label);
        return panel;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            new MainDashboard().setVisible(true);
        });
    }
}