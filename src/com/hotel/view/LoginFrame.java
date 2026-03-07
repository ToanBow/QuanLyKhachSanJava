package com.hotel.view;

import com.hotel.model.User;
import com.hotel.service.IAuthService;
import com.hotel.service.impl.AuthServiceImpl;
import com.hotel.app.Main; // Giả định bạn sẽ gọi Main để chạy tiếp ứng dụng

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginFrame extends JFrame {

    private IAuthService authService;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public LoginFrame() {
        // Khởi tạo Service xử lý logic xác thực
        authService = new AuthServiceImpl();
        
        setTitle("Hệ thống Quản lý Khách sạn Nhóm 2 - CNTT1");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Hiển thị ở giữa màn hình
        setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Khởi tạo 2 màn hình
        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createRegisterPanel(), "REGISTER");

        add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN");
    }

    // ================= MÀN HÌNH ĐĂNG NHẬP =================
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("ĐĂNG NHẬP HỆ THỐNG", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
        titleLabel.setBounds(50, 40, 300, 40);
        panel.add(titleLabel);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(50, 100, 300, 20);
        panel.add(emailLabel);

        JTextField emailField = new JTextField();
        emailField.setBounds(50, 120, 300, 30);
        panel.add(emailField);

        JLabel passLabel = new JLabel("Mật khẩu:");
        passLabel.setBounds(50, 160, 300, 20);
        panel.add(passLabel);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(50, 180, 300, 30);
        panel.add(passField);

        JCheckBox showPassCheck = new JCheckBox("Hiển thị mật khẩu");
        showPassCheck.setBackground(Color.WHITE);
        showPassCheck.setBounds(50, 215, 150, 20);
        showPassCheck.addActionListener(e -> togglePasswordVisibility(showPassCheck, passField, null));
        panel.add(showPassCheck);

        JButton loginBtn = new JButton("Đăng nhập");
        loginBtn.setBounds(50, 260, 300, 40);
        loginBtn.setBackground(new Color(0, 123, 255));
        loginBtn.setForeground(Color.BLACK);
        loginBtn.setFocusPainted(false);
        panel.add(loginBtn);

        JLabel switchLabel = new JLabel("Chưa có tài khoản? Đăng ký ngay", SwingConstants.CENTER);
        switchLabel.setForeground(Color.BLUE);
        switchLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        switchLabel.setBounds(50, 310, 300, 20);
        switchLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(mainPanel, "REGISTER");
            }
        });
        panel.add(switchLabel);

        // Xử lý sự kiện Đăng nhập liên kết với AuthServiceImpl
        loginBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passField.getPassword());

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Email và Mật khẩu!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Gọi logic Auth
            boolean isSuccess = authService.login(email, password);
            if (isSuccess) {
                JOptionPane.showMessageDialog(this, "Đăng nhập thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                this.dispose(); // Đóng cửa sổ đăng nhập
                
                // Kích hoạt giao diện chính hoặc console menu tại đây
                // Mở comment dòng dưới nếu bạn muốn chạy Main Console sau khi đăng nhập xong:
                // Main.main(new String[]{}); 
            } else {
                JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!", "Lỗi Đăng Nhập", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    // ================= MÀN HÌNH ĐĂNG KÝ =================
    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("ĐĂNG KÝ TÀI KHOẢN", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 22));
        titleLabel.setBounds(50, 30, 300, 40);
        panel.add(titleLabel);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(50, 80, 300, 20);
        panel.add(emailLabel);

        JTextField emailField = new JTextField();
        emailField.setBounds(50, 100, 300, 30);
        panel.add(emailField);

        JLabel passLabel = new JLabel("Mật khẩu:");
        passLabel.setBounds(50, 140, 300, 20);
        panel.add(passLabel);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(50, 160, 300, 30);
        panel.add(passField);

        JLabel confirmPassLabel = new JLabel("Xác nhận Mật khẩu:");
        confirmPassLabel.setBounds(50, 200, 300, 20);
        panel.add(confirmPassLabel);

        JPasswordField confirmPassField = new JPasswordField();
        confirmPassField.setBounds(50, 220, 300, 30);
        panel.add(confirmPassField);

        JLabel roleLabel = new JLabel("Vai trò:");
        roleLabel.setBounds(50, 260, 300, 20);
        panel.add(roleLabel);

        String[] roles = {"Quản lý", "Lễ tân", "Kế toán", "Buồng phòng"};
        JComboBox<String> roleCombo = new JComboBox<>(roles);
        roleCombo.setBounds(50, 280, 300, 30);
        panel.add(roleCombo);

        JCheckBox showPassCheck = new JCheckBox("Hiển thị mật khẩu");
        showPassCheck.setBackground(Color.WHITE);
        showPassCheck.setBounds(50, 315, 150, 20);
        showPassCheck.addActionListener(e -> togglePasswordVisibility(showPassCheck, passField, confirmPassField));
        panel.add(showPassCheck);

        JButton registerBtn = new JButton("Đăng ký");
        registerBtn.setBounds(50, 350, 300, 40);
        registerBtn.setBackground(new Color(40, 167, 69));
        registerBtn.setForeground(Color.BLACK);
        registerBtn.setFocusPainted(false);
        panel.add(registerBtn);

        JLabel switchLabel = new JLabel("Đã có tài khoản? Quay lại đăng nhập", SwingConstants.CENTER);
        switchLabel.setForeground(Color.BLUE);
        switchLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        switchLabel.setBounds(50, 410, 300, 20);
        switchLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(mainPanel, "LOGIN");
            }
        });
        panel.add(switchLabel);

        // Xử lý sự kiện Đăng ký liên kết với AuthServiceImpl
        registerBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passField.getPassword());
            String confirmPassword = new String(confirmPassField.getPassword());
            String role = (String) roleCombo.getSelectedItem();

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Khởi tạo đối tượng User với thông tin đã nhập
            User newUser = new User(email, password, role);

            // Chuyển giao việc kiểm tra Regex và lưu CSDL cho Service
            boolean isSuccess = authService.register(newUser, confirmPassword);
            
            if (isSuccess) {
                JOptionPane.showMessageDialog(this, "Đăng ký thành công! Vui lòng đăng nhập.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                cardLayout.show(mainPanel, "LOGIN");
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Đăng ký thất bại!\n- Vui lòng kiểm tra lại mật khẩu khớp nhau.\n- Mật khẩu cần 8 ký tự (chữ hoa, chữ thường, số, ký tự đặc biệt).\n- Hoặc email đã tồn tại.", 
                    "Lỗi Đăng Ký", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    // ================= CÁC HÀM TIỆN ÍCH (UTILITIES) =================
    
    // Tính năng ẩn/hiện mật khẩu áp dụng cho cả trường mật khẩu và xác nhận mật khẩu
    private void togglePasswordVisibility(JCheckBox checkBox, JPasswordField passField, JPasswordField confirmPassField) {
        char echoChar = checkBox.isSelected() ? (char) 0 : '\u2022';
        passField.setEchoChar(echoChar);
        if (confirmPassField != null) {
            confirmPassField.setEchoChar(echoChar);
        }
    }

    public static void main(String[] args) {
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