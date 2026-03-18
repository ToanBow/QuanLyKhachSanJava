package com.hotel.view;

import com.hotel.model.User;
import com.hotel.service.IAuthService;
import com.hotel.service.impl.AuthServiceImpl;
import com.hotel.view.dashboard.MainDashboard;
import com.hotel.util.AuditLogUtil;
import com.hotel.util.DBConnection;
import com.hotel.util.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginFrame extends JFrame {

    private IAuthService authService;
    private final Color PRIMARY_COLOR = new Color(26, 35, 126);
    private final Color ACCENT_COLOR = new Color(13, 71, 161);
    private final Color TEXT_COLOR = new Color(33, 33, 33);
    private final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 32);

    public LoginFrame() {
        authService = new AuthServiceImpl();

        setTitle("Hệ thống Quản lý Khách sạn Ánh Trăng");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // --- PHẦN BÊN TRÁI (Banner & Logo) ---
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(PRIMARY_COLOR);
        leftPanel.setPreferredSize(new Dimension(420, 550));

        JLabel welcomeLabel = new JLabel("<html><div style='text-align: center; line-height: 1.5;'>HỆ THỐNG<br>QUẢN LÝ KHÁCH SẠN</div></html>");
        welcomeLabel.setFont(TITLE_FONT);
        welcomeLabel.setForeground(Color.WHITE);
        leftPanel.add(welcomeLabel);

        add(leftPanel, BorderLayout.WEST);

        // --- PHẦN BÊN PHẢI (Form Đăng nhập) ---
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("ĐĂNG NHẬP", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(PRIMARY_COLOR);
        gbc.insets = new Insets(0, 0, 40, 0);
        rightPanel.add(titleLabel, gbc);

        gbc.insets = new Insets(5, 0, 8, 0);
        JLabel emailLabel = new JLabel("Email đăng nhập:");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        emailLabel.setForeground(Color.DARK_GRAY);
        rightPanel.add(emailLabel, gbc);

        JTextField emailField = createTextField();
        rightPanel.add(emailField, gbc);

        JLabel passLabel = new JLabel("Mật khẩu:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passLabel.setForeground(Color.DARK_GRAY);
        gbc.insets = new Insets(20, 0, 8, 0);
        rightPanel.add(passLabel, gbc);

        JPasswordField passField = createPasswordField();
        gbc.insets = new Insets(5, 0, 5, 0);
        rightPanel.add(passField, gbc);

        JCheckBox showPassCheck = new JCheckBox("Hiển thị mật khẩu");
        showPassCheck.setBackground(Color.WHITE);
        showPassCheck.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        showPassCheck.setForeground(Color.GRAY);
        showPassCheck.setFocusPainted(false);
        showPassCheck.setCursor(new Cursor(Cursor.HAND_CURSOR));
        showPassCheck.addActionListener(e -> {
            char echoChar = showPassCheck.isSelected() ? (char) 0 : '\u2022';
            passField.setEchoChar(echoChar);
        });
        gbc.insets = new Insets(0, 0, 30, 0);
        rightPanel.add(showPassCheck, gbc);

        // Nút Đăng nhập được nâng cấp giao diện (Bo tròn, Hover sáng lên)
        JButton loginBtn = createStyledButton("ĐĂNG NHẬP", ACCENT_COLOR);
        gbc.insets = new Insets(10, 0, 15, 0);
        rightPanel.add(loginBtn, gbc);

        // LOGIC ĐĂNG NHẬP
        loginBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passField.getPassword());

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Email và Mật khẩu!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (authService.login(email, password)) {
                String role = "Quản lý"; 
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement("SELECT role FROM users WHERE email = ?")) {
                    ps.setString(1, email);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) role = rs.getString("role");
                } catch (Exception ex) { ex.printStackTrace(); }

                User loggedInUser = new User(email, "", role);
                SessionManager.setCurrentUser(loggedInUser);
                AuditLogUtil.log(email, "ĐĂNG NHẬP (Bắt đầu phiên làm việc)");

                this.dispose();
                SwingUtilities.invokeLater(() -> new MainDashboard().setVisible(true));
            } else {
                JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!", "Lỗi Đăng Nhập", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(rightPanel, BorderLayout.CENTER);
    }

    // Làm đẹp ô nhập liệu (Text Field)
    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(340, 45));
        field.setFont(MAIN_FONT);
        field.setForeground(TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true), 
                new EmptyBorder(5, 15, 5, 15)));
        return field;
    }

    // Làm đẹp ô mật khẩu (Password Field)
    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setPreferredSize(new Dimension(340, 45));
        field.setFont(MAIN_FONT);
        field.setForeground(TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true), 
                new EmptyBorder(5, 15, 5, 15)));
        return field;
    }

    // Phương thức tạo Nút bấm Bo tròn góc (Rounded Button)
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                // Kích hoạt chống răng cưa để đường cong mượt mà
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25); // Bán kính bo góc 25px
                super.paintComponent(g);
                g2.dispose();
            }
        };
        
        button.setPreferredSize(new Dimension(340, 50));
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false); // Quan trọng để hiển thị màu vẽ tùy chỉnh
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Thêm hiệu ứng Đổi màu khi đưa chuột vào (Hover Effect)
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { button.setBackground(bgColor.brighter()); }
            public void mouseExited(MouseEvent e) { button.setBackground(bgColor); }
            public void mousePressed(MouseEvent e) { button.setBackground(bgColor.darker()); }
            public void mouseReleased(MouseEvent e) { button.setBackground(bgColor.brighter()); }
        });
        
        return button;
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}