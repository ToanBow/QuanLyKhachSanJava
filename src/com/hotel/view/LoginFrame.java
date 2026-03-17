package com.hotel.view;

import com.hotel.model.User;
import com.hotel.service.IAuthService;
import com.hotel.service.impl.AuthServiceImpl;
import com.hotel.view.dashboard.MainDashboard;
import com.hotel.app.Main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginFrame extends JFrame {

    private IAuthService authService;
    private CardLayout cardLayout;
    private JPanel rightPanel;

    // Bảng màu chuẩn UI hiện đại
    private final Color PRIMARY_COLOR = new Color(26, 35, 126); // Xanh đậm
    private final Color ACCENT_COLOR = new Color(13, 71, 161); // Xanh nút bấm
    private final Color SUCCESS_COLOR = new Color(46, 125, 50); // Xanh lá đăng ký
    private final Color TEXT_COLOR = new Color(33, 33, 33);
    private final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);

    public LoginFrame() {
        authService = new AuthServiceImpl();

        setTitle("Hệ thống Quản lý Khách sạn Nhóm 2 - CNTT1");
        setSize(800, 500); // Mở rộng chiều ngang để chia đôi màn hình
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        //Panel Bên Trái (Branding)
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(PRIMARY_COLOR);
        leftPanel.setPreferredSize(new Dimension(350, 500));

        JLabel welcomeLabel = new JLabel("<html><div style='text-align: center;'>KHÁCH SẠN<br>ÁNH TRĂNG</div></html>");
        welcomeLabel.setFont(TITLE_FONT);
        welcomeLabel.setForeground(Color.WHITE);
        leftPanel.add(welcomeLabel);

        add(leftPanel, BorderLayout.WEST);

        //Panel Bên Phải (Chứa form Đăng nhập / Đăng ký)
        cardLayout = new CardLayout();
        rightPanel = new JPanel(cardLayout);
        rightPanel.setBackground(Color.WHITE);

        rightPanel.add(createLoginPanel(), "LOGIN");
        rightPanel.add(createRegisterPanel(), "REGISTER");

        add(rightPanel, BorderLayout.CENTER);
        cardLayout.show(rightPanel, "LOGIN");
    }

    // MÀN HÌNH ĐĂNG NHẬP
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = createGbc();

        JLabel titleLabel = new JLabel("ĐĂNG NHẬP");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        gbc.insets = new Insets(0, 0, 30, 0);
        panel.add(titleLabel, gbc);

        gbc.insets = new Insets(5, 0, 5, 0);
        
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(MAIN_FONT);
        panel.add(emailLabel, gbc);

        JTextField emailField = createTextField();
        panel.add(emailField, gbc);

        JLabel passLabel = new JLabel("Mật khẩu:");
        passLabel.setFont(MAIN_FONT);
        gbc.insets = new Insets(15, 0, 5, 0);
        panel.add(passLabel, gbc);

        JPasswordField passField = createPasswordField();
        gbc.insets = new Insets(5, 0, 5, 0);
        panel.add(passField, gbc);

        JCheckBox showPassCheck = new JCheckBox("Hiển thị mật khẩu");
        showPassCheck.setBackground(Color.WHITE);
        showPassCheck.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPassCheck.addActionListener(e -> togglePasswordVisibility(showPassCheck, passField, null));
        panel.add(showPassCheck, gbc);

        JButton loginBtn = createButton("Đăng Nhập", ACCENT_COLOR);
        gbc.insets = new Insets(20, 0, 15, 0);
        panel.add(loginBtn, gbc);

        JLabel switchLabel = new JLabel("Chưa có tài khoản? Đăng ký ngay");
        switchLabel.setForeground(ACCENT_COLOR);
        switchLabel.setFont(MAIN_FONT);
        switchLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        switchLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(rightPanel, "REGISTER");
            }
        });
        gbc.insets = new Insets(10, 0, 0, 0);
        panel.add(switchLabel, gbc);

        //Logic Đăng nhập
        loginBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passField.getPassword());

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Email và Mật khẩu!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (authService.login(email, password)) {
                JOptionPane.showMessageDialog(this, "Đăng nhập thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
                // Kích hoạt luồng Main
                SwingUtilities.invokeLater(() -> {
                    new MainDashboard().setVisible(true);
                });
            } else {
                JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!", "Lỗi Đăng Nhập", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    //MÀN HÌNH ĐĂNG KÝ
    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = createGbc();

        JLabel titleLabel = new JLabel("TẠO TÀI KHOẢN");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(SUCCESS_COLOR);
        gbc.insets = new Insets(0, 0, 20, 0);
        panel.add(titleLabel, gbc);

        gbc.insets = new Insets(5, 0, 2, 0);

        panel.add(new JLabel("Email:"), gbc);
        JTextField emailField = createTextField();
        panel.add(emailField, gbc);

        panel.add(new JLabel("Mật khẩu:"), gbc);
        JPasswordField passField = createPasswordField();
        panel.add(passField, gbc);

        panel.add(new JLabel("Xác nhận Mật khẩu:"), gbc);
        JPasswordField confirmPassField = createPasswordField();
        panel.add(confirmPassField, gbc);

        panel.add(new JLabel("Vai trò:"), gbc);
        String[] roles = {"Quản lý", "Lễ tân", "Kế toán", "Buồng phòng"};
        JComboBox<String> roleCombo = new JComboBox<>(roles);
        roleCombo.setFont(MAIN_FONT);
        roleCombo.setPreferredSize(new Dimension(300, 35));
        roleCombo.setBackground(Color.WHITE);
        panel.add(roleCombo, gbc);

        JCheckBox showPassCheck = new JCheckBox("Hiển thị mật khẩu");
        showPassCheck.setBackground(Color.WHITE);
        showPassCheck.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPassCheck.addActionListener(e -> togglePasswordVisibility(showPassCheck, passField, confirmPassField));
        panel.add(showPassCheck, gbc);

        JButton registerBtn = createButton("Đăng Ký", SUCCESS_COLOR);
        gbc.insets = new Insets(15, 0, 10, 0);
        panel.add(registerBtn, gbc);

        JLabel switchLabel = new JLabel("Đã có tài khoản? Đăng nhập");
        switchLabel.setForeground(SUCCESS_COLOR);
        switchLabel.setFont(MAIN_FONT);
        switchLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        switchLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(rightPanel, "LOGIN");
            }
        });
        gbc.insets = new Insets(5, 0, 0, 0);
        panel.add(switchLabel, gbc);

        // Logic Đăng ký
        registerBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passField.getPassword());
            String confirmPassword = new String(confirmPassField.getPassword());
            String role = (String) roleCombo.getSelectedItem();

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            User newUser = new User(email, password, role);
            if (authService.register(newUser, confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Đăng ký thành công! Vui lòng đăng nhập.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                cardLayout.show(rightPanel, "LOGIN");
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Đăng ký thất bại!\n- Mật khẩu phải khớp nhau.\n- Mật khẩu cần 8 ký tự (chữ hoa, thường, số, ký tự đặc biệt).\n- Hoặc email đã tồn tại.", 
                    "Lỗi Đăng Ký", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    //CÁC HÀM TIỆN ÍCH UI

    private GridBagConstraints createGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        return gbc;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(300, 38));
        field.setFont(MAIN_FONT);
        field.setForeground(TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(5, 10, 5, 10)));
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setPreferredSize(new Dimension(300, 38));
        field.setFont(MAIN_FONT);
        field.setForeground(TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(5, 10, 5, 10)));
        return field;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(300, 42));
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        return button;
    }

    private void togglePasswordVisibility(JCheckBox checkBox, JPasswordField passField, JPasswordField confirmPassField) {
        char echoChar = checkBox.isSelected() ? (char) 0 : '\u2022';
        passField.setEchoChar(echoChar);
        if (confirmPassField != null) {
            confirmPassField.setEchoChar(echoChar);
        }
    }

    public static void main(String[] args) {
        // Kích hoạt giao diện hệ thống (Native Look and Feel)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}