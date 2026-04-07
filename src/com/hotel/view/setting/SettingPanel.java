package com.hotel.view.setting;

import com.hotel.model.Room;
import com.hotel.model.User;
import com.hotel.service.impl.RoomServiceImpl;
import com.hotel.service.impl.AuthServiceImpl;
import com.hotel.util.DBConnection;
import com.hotel.util.EnvConfig;
import com.hotel.util.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingPanel extends JPanel {

    private RoomServiceImpl roomService;
    private AuthServiceImpl authService;
    
    private JTable roomTable, serviceTable, blacklistTable, staffTable;
    private DefaultTableModel roomModel, serviceModel, blacklistModel, staffModel;
    private final Color PRIMARY_COLOR = new Color(26, 35, 126);

    private JTextField txtHotelName, txtAddress, txtTaxCode, txtPhone;
    private JTextField txtCheckInTime, txtCheckOutTime, txtCurrency;
    private JTextField txtSmtpEmail;
    private JPasswordField txtSmtpPass;
    
    // Các biến cho thông tin ngân hàng
    private JTextField txtBankId, txtBankAccount, txtBankName;
    
    private JCheckBox[] leTanCb, keToanCb, buongPhongCb;
    private final String[] MODULES = {"DASHBOARD", "ROOM", "CUSTOMER", "INVOICE", "STATISTIC", "SETTING"};

    public SettingPanel(RoomServiceImpl roomService) {
        this.roomService = roomService;
        this.authService = new AuthServiceImpl();
        
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Cấu hình Hệ thống & Quản lý");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(PRIMARY_COLOR);
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        tabbedPane.addTab("Quản lý Phòng", createRoomTab());
        tabbedPane.addTab("Quản lý Dịch Vụ", createServiceTab());
        tabbedPane.addTab("Danh Sách Đen", createBlacklistTab());
        tabbedPane.addTab("Quản lý Nhân sự", createStaffTab());
        tabbedPane.addTab("Cấu hình Hệ thống", createSystemConfigTab()); 

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JButton createBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        return btn;
    }

    // ================== TAB QUẢN LÝ NHÂN SỰ ==================
    private JPanel createStaffTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        staffModel = new DefaultTableModel(new String[]{"Email / Tên đăng nhập", "Vai trò"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        staffTable = new JTable(staffModel);
        staffTable.setRowHeight(30);
        panel.add(new JScrollPane(staffTable), BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(Color.WHITE);

        JButton btnReload = createBtn("Làm mới", new Color(224, 224, 224));
        JButton btnAdd = createBtn("Thêm Nhân viên", new Color(165, 214, 167));
        JButton btnDelete = createBtn("Xóa Nhân viên", new Color(239, 154, 154));

        actionPanel.add(btnReload); actionPanel.add(btnAdd); actionPanel.add(btnDelete);
        panel.add(actionPanel, BorderLayout.SOUTH);

        loadStaffData();

        btnReload.addActionListener(e -> loadStaffData());
        btnAdd.addActionListener(e -> openRegisterStaffForm());
        btnDelete.addActionListener(e -> deleteSelectedStaff());

        return panel;
    }

    private void loadStaffData() {
        staffModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT email, role FROM users");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) staffModel.addRow(new Object[]{rs.getString("email"), rs.getString("role")});
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void openRegisterStaffForm() {
        JTextField txtEmail = new JTextField(); JPasswordField txtPass = new JPasswordField();
        JPasswordField txtConfirm = new JPasswordField(); JComboBox<String> cbRole = new JComboBox<>(new String[]{"Quản lý", "Lễ tân", "Kế toán", "Buồng phòng"});
        Object[] fields = { "Email đăng nhập:", txtEmail, "Mật khẩu:", txtPass, "Xác nhận mật khẩu:", txtConfirm, "Vai trò:", cbRole };
        if (JOptionPane.showConfirmDialog(this, fields, "Tạo tài khoản Nhân viên mới", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            String email = txtEmail.getText().trim(); String pass = new String(txtPass.getPassword());
            String confirm = new String(txtConfirm.getPassword()); String role = (String) cbRole.getSelectedItem();
            if (email.isEmpty() || pass.isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!"); return; }
            User newUser = new User(email, pass, role);
            if (authService.register(newUser, confirm)) { JOptionPane.showMessageDialog(this, "Đã tạo tài khoản thành công!"); loadStaffData(); } 
            else { JOptionPane.showMessageDialog(this, "Tạo thất bại!\n- Mật khẩu phải khớp.\n- Mật khẩu >= 8 ký tự.\n- Hoặc Email đã tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE); }
        }
    }

    private void deleteSelectedStaff() {
        int row = staffTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Chọn nhân viên cần xóa!"); return; }
        String email = (String) staffModel.getValueAt(row, 0);
        if (SessionManager.getCurrentUser() != null && email.equals(SessionManager.getCurrentUser().getEmail())) { JOptionPane.showMessageDialog(this, "Không thể tự xóa tài khoản đang đăng nhập!", "Lỗi", JOptionPane.ERROR_MESSAGE); return; }
        if (JOptionPane.showConfirmDialog(this, "Xóa tài khoản " + email + "?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE email = ?")) {
                ps.setString(1, email); ps.executeUpdate(); loadStaffData();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Tài khoản đang có dữ liệu Hóa đơn, không thể xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE); }
        }
    }

    // ================== TAB CẤU HÌNH HỆ THỐNG ==================
    private JPanel createSystemConfigTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(Color.WHITE);

        JPanel topForm = new JPanel(new GridLayout(0, 2, 20, 15));
        topForm.setBackground(Color.WHITE);
        topForm.setBorder(new EmptyBorder(20, 20, 20, 20));

        txtHotelName = new JTextField(); txtAddress = new JTextField(); txtTaxCode = new JTextField(); txtPhone = new JTextField();
        addSection(topForm, "THÔNG TIN KHÁCH SẠN");
        topForm.add(new JLabel("Tên khách sạn:")); topForm.add(txtHotelName);
        topForm.add(new JLabel("Địa chỉ:")); topForm.add(txtAddress);
        topForm.add(new JLabel("Mã số thuế:")); topForm.add(txtTaxCode);
        topForm.add(new JLabel("Hotline liên hệ:")); topForm.add(txtPhone);

        txtCheckInTime = new JTextField(); txtCheckOutTime = new JTextField(); txtCurrency = new JTextField();
        addSection(topForm, "THAM SỐ VẬN HÀNH");
        topForm.add(new JLabel("Giờ Check-in tiêu chuẩn:")); topForm.add(txtCheckInTime);
        topForm.add(new JLabel("Giờ Check-out tiêu chuẩn:")); topForm.add(txtCheckOutTime);
        topForm.add(new JLabel("Định dạng tiền tệ:")); topForm.add(txtCurrency);

        txtBankId = new JTextField(); txtBankAccount = new JTextField(); txtBankName = new JTextField();
        addSection(topForm, "THÔNG TIN THANH TOÁN (MÃ QR VietQR)");
        topForm.add(new JLabel("Mã Ngân hàng (VD: MB, VCB, TCB):")); topForm.add(txtBankId);
        topForm.add(new JLabel("Số tài khoản:")); topForm.add(txtBankAccount);
        topForm.add(new JLabel("Tên chủ tài khoản:")); topForm.add(txtBankName);

        txtSmtpEmail = new JTextField(); txtSmtpPass = new JPasswordField();
        addSection(topForm, "KẾT NỐI NGOẠI VI & SMTP (Lưu tại tệp .env)");
        topForm.add(new JLabel("Email gửi báo cáo tự động:")); topForm.add(txtSmtpEmail);
        topForm.add(new JLabel("Mật khẩu ứng dụng (App Password):")); topForm.add(txtSmtpPass);
        
        mainContent.add(topForm, BorderLayout.NORTH);

        JPanel rbacPanel = new JPanel(new BorderLayout(0, 10));
        rbacPanel.setBackground(Color.WHITE);
        rbacPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        JLabel rbacTitle = new JLabel("PHÂN QUYỀN TRUY CẬP (Đánh dấu để cấp quyền cho Từng chức danh)");
        rbacTitle.setFont(new Font("Segoe UI", Font.BOLD, 16)); rbacTitle.setForeground(PRIMARY_COLOR);
        rbacPanel.add(rbacTitle, BorderLayout.NORTH);

        JPanel rbacGrid = new JPanel(new GridLayout(4, 7, 5, 10));
        rbacGrid.setBackground(Color.WHITE);
        String[] headers = {"Vai trò", "Tổng quan", "Sơ đồ phòng", "Khách hàng", "Hóa đơn", "Thống kê", "Cài đặt"};
        for (int i=0; i<headers.length; i++) {
            JLabel lbl = new JLabel(headers[i], i==0 ? SwingConstants.LEFT : SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12)); rbacGrid.add(lbl);
        }

        leTanCb = new JCheckBox[6]; keToanCb = new JCheckBox[6]; buongPhongCb = new JCheckBox[6];
        addRoleRow(rbacGrid, "Lễ tân", leTanCb); addRoleRow(rbacGrid, "Kế toán", keToanCb); addRoleRow(rbacGrid, "Buồng phòng", buongPhongCb);
        rbacPanel.add(rbacGrid, BorderLayout.CENTER);
        
        mainContent.add(rbacPanel, BorderLayout.CENTER);
        JScrollPane scrollPane = new JScrollPane(mainContent); scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE);
        JButton btnSaveConfig = createBtn("Lưu toàn bộ Cấu hình", new Color(144, 202, 249));
        btnSaveConfig.setPreferredSize(new Dimension(250, 45));
        btnSaveConfig.addActionListener(e -> saveSystemConfigurations());
        bottomPanel.add(btnSaveConfig); panel.add(bottomPanel, BorderLayout.SOUTH);

        loadSystemConfigurations(); 
        return panel;
    }

    private void addSection(JPanel panel, String title) {
        JLabel lbl = new JLabel(title); lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setForeground(PRIMARY_COLOR); panel.add(lbl); panel.add(new JLabel("")); 
    }

    private void addRoleRow(JPanel panel, String roleName, JCheckBox[] cbs) {
        JLabel lblRole = new JLabel(roleName); lblRole.setFont(new Font("Segoe UI", Font.BOLD, 14)); panel.add(lblRole);
        for (int i = 0; i < 6; i++) { cbs[i] = new JCheckBox(); cbs[i].setBackground(Color.WHITE); cbs[i].setHorizontalAlignment(SwingConstants.CENTER); panel.add(cbs[i]); }
    }

    private void setRbacCheckboxes(JCheckBox[] cbs, String permissions) {
        for (int i = 0; i < 6; i++) cbs[i].setSelected(permissions.contains(MODULES[i]));
    }

    private String getRbacString(JCheckBox[] cbs) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) if (cbs[i].isSelected()) sb.append(MODULES[i]).append(",");
        return sb.toString();
    }

    private void saveEnvFile(String emailUser, String emailPass) {
        try {
            Path envPath = Paths.get(".env");
            List<String> lines = new ArrayList<>();
            if (Files.exists(envPath)) lines = Files.readAllLines(envPath, StandardCharsets.UTF_8);
            boolean hasUser = false, hasPass = false;
            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).startsWith("EMAIL_USER=")) { lines.set(i, "EMAIL_USER=" + emailUser); hasUser = true; } 
                else if (lines.get(i).startsWith("EMAIL_PASS=")) { lines.set(i, "EMAIL_PASS=" + emailPass); hasPass = true; }
            }
            if (!hasUser) lines.add("EMAIL_USER=" + emailUser); if (!hasPass) lines.add("EMAIL_PASS=" + emailPass);
            Files.write(envPath, lines, StandardCharsets.UTF_8);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadSystemConfigurations() {
        Map<String, String> configs = new HashMap<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT * FROM system_settings"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) configs.put(rs.getString("setting_key"), rs.getString("setting_value"));
        } catch (Exception e) { e.printStackTrace(); }

        txtHotelName.setText(configs.getOrDefault("HOTEL_NAME", "Khách sạn Ánh Trăng")); txtAddress.setText(configs.getOrDefault("HOTEL_ADDRESS", "123 Cầu Giấy, Hà Nội"));
        txtTaxCode.setText(configs.getOrDefault("TAX_CODE", "0102030405")); txtPhone.setText(configs.getOrDefault("HOTEL_PHONE", "024 1234 5678"));
        txtCheckInTime.setText(configs.getOrDefault("CHECKIN_TIME", "14:00")); txtCheckOutTime.setText(configs.getOrDefault("CHECKOUT_TIME", "12:00"));
        txtCurrency.setText(configs.getOrDefault("CURRENCY_FORMAT", "VND"));
        
        txtBankId.setText(configs.getOrDefault("BANK_ID", "VCB"));
        txtBankAccount.setText(configs.getOrDefault("BANK_ACCOUNT", "1045181602"));
        txtBankName.setText(configs.getOrDefault("BANK_ACCOUNT_NAME", "DANG THE TOAN"));
        
        txtSmtpEmail.setText(EnvConfig.getEmailUser() != null ? EnvConfig.getEmailUser() : ""); txtSmtpPass.setText(EnvConfig.getEmailPass() != null ? EnvConfig.getEmailPass() : "");

        setRbacCheckboxes(leTanCb, configs.getOrDefault("RBAC_LETAN", "DASHBOARD,ROOM,CUSTOMER,INVOICE"));
        setRbacCheckboxes(keToanCb, configs.getOrDefault("RBAC_KETOAN", "INVOICE,STATISTIC"));
        setRbacCheckboxes(buongPhongCb, configs.getOrDefault("RBAC_BUONGPHONG", "ROOM"));
    }

    private void saveSystemConfigurations() {
        saveEnvFile(txtSmtpEmail.getText().trim(), new String(txtSmtpPass.getPassword()).trim());
        Map<String, String> configs = new HashMap<>();
        configs.put("HOTEL_NAME", txtHotelName.getText().trim()); configs.put("HOTEL_ADDRESS", txtAddress.getText().trim());
        configs.put("TAX_CODE", txtTaxCode.getText().trim()); configs.put("HOTEL_PHONE", txtPhone.getText().trim());
        configs.put("CHECKIN_TIME", txtCheckInTime.getText().trim()); configs.put("CHECKOUT_TIME", txtCheckOutTime.getText().trim());
        configs.put("CURRENCY_FORMAT", txtCurrency.getText().trim());
        
        configs.put("BANK_ID", txtBankId.getText().trim());
        configs.put("BANK_ACCOUNT", txtBankAccount.getText().trim());
        configs.put("BANK_ACCOUNT_NAME", txtBankName.getText().trim().toUpperCase());
        
        configs.put("RBAC_LETAN", getRbacString(leTanCb)); configs.put("RBAC_KETOAN", getRbacString(keToanCb)); configs.put("RBAC_BUONGPHONG", getRbacString(buongPhongCb));

        String sql = "INSERT INTO system_settings (setting_key, setting_value) VALUES (?, ?) ON DUPLICATE KEY UPDATE setting_value = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Map.Entry<String, String> entry : configs.entrySet()) {
                ps.setString(1, entry.getKey()); ps.setString(2, entry.getValue()); ps.setString(3, entry.getValue()); ps.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Đã lưu thành công cấu hình hệ thống!");
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi lưu cấu hình: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); }
    }

    private JPanel createRoomTab() {
        JPanel panel = new JPanel(new BorderLayout()); panel.setBackground(Color.WHITE);
        roomModel = new DefaultTableModel(new String[]{"Mã Phòng", "Tầng", "Loại Phòng", "Số giường", "Giá Ngày", "Giá Giờ", "Trạng thái"}, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } };
        roomTable = new JTable(roomModel); roomTable.setRowHeight(30); panel.add(new JScrollPane(roomTable), BorderLayout.CENTER);
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); actionPanel.setBackground(Color.WHITE);
        JButton btnReload = createBtn("Làm mới", new Color(224, 224, 224)); JButton btnAdd = createBtn("Thêm Phòng", new Color(165, 214, 167)); JButton btnEdit = createBtn("Sửa Phòng", new Color(255, 204, 128)); JButton btnDelete = createBtn("Xóa Phòng", new Color(239, 154, 154));
        actionPanel.add(btnReload); actionPanel.add(btnEdit); actionPanel.add(btnAdd); actionPanel.add(btnDelete); panel.add(actionPanel, BorderLayout.SOUTH);
        loadRoomData();
        btnReload.addActionListener(e -> loadRoomData()); btnAdd.addActionListener(e -> insertNewRoom()); btnEdit.addActionListener(e -> editSelectedRoom()); btnDelete.addActionListener(e -> deleteSelectedRoom());
        return panel;
    }
    private void loadRoomData() {
        roomModel.setRowCount(0); List<Room> rooms = roomService.getRoomMap();
        for (Room r : rooms) { roomModel.addRow(new Object[]{ r.getRoomId(), r.getFloor(), r.getType(), r.getBeds(), r.getDailyPrice(), r.getHourlyPrice(), r.getStatus() }); }
    }
    private void updatePriceFields(JComboBox<String> cbType, JTextField txtDaily, JTextField txtHourly) {
        String sel = (String) cbType.getSelectedItem();
        if ("Standard".equals(sel)) { txtDaily.setText("500000"); txtHourly.setText("100000"); } else if ("Deluxe".equals(sel)) { txtDaily.setText("800000"); txtHourly.setText("150000"); } else if ("Suite".equals(sel)) { txtDaily.setText("1500000"); txtHourly.setText("250000"); } else if ("VIP".equals(sel)) { txtDaily.setText("3000000"); txtHourly.setText("500000"); }
    }
    private void insertNewRoom() {
        JTextField txtId = new JTextField(); JTextField txtFloor = new JTextField(); JComboBox<String> cbType = new JComboBox<>(new String[]{"Standard", "Deluxe", "Suite", "VIP"}); JTextField txtBeds = new JTextField("1"); JTextField txtDaily = new JTextField(); JTextField txtHourly = new JTextField();
        updatePriceFields(cbType, txtDaily, txtHourly); cbType.addActionListener(e -> updatePriceFields(cbType, txtDaily, txtHourly));
        Object[] fields = { "Mã Phòng (VD: P401):", txtId, "Tầng:", txtFloor, "Hạng phòng:", cbType, "Số giường:", txtBeds, "Giá ngày:", txtDaily, "Giá giờ:", txtHourly };
        if (JOptionPane.showConfirmDialog(this, fields, "Thêm phòng mới", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            String sql = "INSERT INTO rooms (room_id, floor, type, beds, daily_price, hourly_price, status) VALUES (?, ?, ?, ?, ?, ?, 'Sẵn sàng')";
            try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) { ps.setString(1, txtId.getText().trim()); ps.setInt(2, Integer.parseInt(txtFloor.getText().trim())); ps.setString(3, (String) cbType.getSelectedItem()); ps.setInt(4, Integer.parseInt(txtBeds.getText().trim())); ps.setDouble(5, Double.parseDouble(txtDaily.getText().trim())); ps.setDouble(6, Double.parseDouble(txtHourly.getText().trim())); ps.executeUpdate(); loadRoomData(); } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: Vui lòng kiểm tra lại thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE); }
        }
    }
    private void editSelectedRoom() {
        int row = roomTable.getSelectedRow(); if (row == -1) { JOptionPane.showMessageDialog(this, "Chọn một phòng để sửa!"); return; }
        String roomId = String.valueOf(roomModel.getValueAt(row, 0)); JTextField txtId = new JTextField(roomId); txtId.setEditable(false); JTextField txtFloor = new JTextField(String.valueOf(roomModel.getValueAt(row, 1))); JComboBox<String> cbType = new JComboBox<>(new String[]{"Standard", "Deluxe", "Suite", "VIP"}); cbType.setSelectedItem(String.valueOf(roomModel.getValueAt(row, 2))); JTextField txtBeds = new JTextField(String.valueOf(roomModel.getValueAt(row, 3))); JTextField txtDaily = new JTextField(String.valueOf(roomModel.getValueAt(row, 4))); JTextField txtHourly = new JTextField(String.valueOf(roomModel.getValueAt(row, 5)));
        cbType.addActionListener(e -> updatePriceFields(cbType, txtDaily, txtHourly));
        Object[] fields = { "Mã Phòng:", txtId, "Tầng:", txtFloor, "Hạng phòng:", cbType, "Số giường:", txtBeds, "Giá ngày:", txtDaily, "Giá giờ:", txtHourly };
        if (JOptionPane.showConfirmDialog(this, fields, "Sửa phòng", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            String sql = "UPDATE rooms SET floor=?, type=?, beds=?, daily_price=?, hourly_price=? WHERE room_id=?";
            try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) { ps.setInt(1, Integer.parseInt(txtFloor.getText().trim())); ps.setString(2, (String) cbType.getSelectedItem()); ps.setInt(3, Integer.parseInt(txtBeds.getText().trim())); ps.setDouble(4, Double.parseDouble(txtDaily.getText().trim())); ps.setDouble(5, Double.parseDouble(txtHourly.getText().trim())); ps.setString(6, roomId); ps.executeUpdate(); loadRoomData(); } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi cập nhật!", "Lỗi", JOptionPane.ERROR_MESSAGE); }
        }
    }
    private void deleteSelectedRoom() {
        int row = roomTable.getSelectedRow(); if (row == -1) return; String roomId = String.valueOf(roomModel.getValueAt(row, 0));
        if (JOptionPane.showConfirmDialog(this, "Xóa phòng " + roomId + "?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement("DELETE FROM rooms WHERE room_id = ?")) { ps.setString(1, roomId); ps.executeUpdate(); loadRoomData(); } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Phòng đang có hóa đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE); }
        }
    }
    private JPanel createServiceTab() {
        JPanel panel = new JPanel(new BorderLayout()); panel.setBackground(Color.WHITE);
        serviceModel = new DefaultTableModel(new String[]{"Mã DV", "Tên Dịch Vụ", "Danh mục", "Đơn Giá", "Tồn Kho"}, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } };
        serviceTable = new JTable(serviceModel); serviceTable.setRowHeight(30); panel.add(new JScrollPane(serviceTable), BorderLayout.CENTER);
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); actionPanel.setBackground(Color.WHITE);
        JButton btnReload = createBtn("Làm mới", new Color(224, 224, 224)); JButton btnAdd = createBtn("Thêm DV", new Color(165, 214, 167)); JButton btnEdit = createBtn("Sửa DV", new Color(255, 204, 128)); JButton btnDelete = createBtn("Xóa DV", new Color(239, 154, 154));
        actionPanel.add(btnReload); actionPanel.add(btnEdit); actionPanel.add(btnAdd); actionPanel.add(btnDelete); panel.add(actionPanel, BorderLayout.SOUTH);
        loadServiceData();
        btnReload.addActionListener(e -> loadServiceData()); btnAdd.addActionListener(e -> openServiceForm(false)); btnEdit.addActionListener(e -> openServiceForm(true)); btnDelete.addActionListener(e -> deleteSelectedService());
        return panel;
    }
    private void loadServiceData() {
        serviceModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT * FROM services"); ResultSet rs = ps.executeQuery()) { while (rs.next()) { serviceModel.addRow(new Object[]{ rs.getString("service_id"), rs.getString("name"), rs.getString("category"), rs.getDouble("price"), rs.getInt("inventory") }); } } catch (Exception e) { e.printStackTrace(); }
    }
    private void openServiceForm(boolean isEdit) {
        JTextField txtId = new JTextField(); JTextField txtName = new JTextField(); JTextField txtCategory = new JTextField(); JTextField txtPrice = new JTextField("0"); JTextField txtInventory = new JTextField("100");
        if (isEdit) { int row = serviceTable.getSelectedRow(); if (row == -1) { JOptionPane.showMessageDialog(this, "Chọn dịch vụ để sửa!"); return; } txtId.setText(String.valueOf(serviceModel.getValueAt(row, 0))); txtId.setEditable(false); txtName.setText(String.valueOf(serviceModel.getValueAt(row, 1))); txtCategory.setText(String.valueOf(serviceModel.getValueAt(row, 2))); txtPrice.setText(String.valueOf(serviceModel.getValueAt(row, 3))); txtInventory.setText(String.valueOf(serviceModel.getValueAt(row, 4))); }
        Object[] fields = { "Mã DV:", txtId, "Tên Dịch Vụ:", txtName, "Danh mục:", txtCategory, "Đơn giá:", txtPrice, "Tồn kho:", txtInventory };
        if (JOptionPane.showConfirmDialog(this, fields, isEdit ? "Sửa Dịch Vụ" : "Thêm Dịch Vụ", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            String sql = isEdit ? "UPDATE services SET name=?, category=?, price=?, inventory=? WHERE service_id=?" : "INSERT INTO services (name, category, price, inventory, service_id) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) { ps.setString(1, txtName.getText().trim()); ps.setString(2, txtCategory.getText().trim()); ps.setDouble(3, Double.parseDouble(txtPrice.getText().trim())); ps.setInt(4, Integer.parseInt(txtInventory.getText().trim())); ps.setString(5, txtId.getText().trim()); ps.executeUpdate(); loadServiceData(); } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); }
        }
    }
    private void deleteSelectedService() {
        int row = serviceTable.getSelectedRow(); if (row == -1) return; String serviceId = String.valueOf(serviceModel.getValueAt(row, 0));
        if (JOptionPane.showConfirmDialog(this, "Xóa dịch vụ " + serviceId + "?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement("DELETE FROM services WHERE service_id = ?")) { ps.setString(1, serviceId); ps.executeUpdate(); loadServiceData(); } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Dịch vụ đang nằm trong hóa đơn, không thể xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE); }
        }
    }
    private JPanel createBlacklistTab() {
        JPanel panel = new JPanel(new BorderLayout()); panel.setBackground(Color.WHITE);
        blacklistModel = new DefaultTableModel(new String[]{"CCCD / Passport", "Họ và Tên", "Lý do vi phạm", "Ngày đưa vào"}, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } };
        blacklistTable = new JTable(blacklistModel); blacklistTable.setRowHeight(30); panel.add(new JScrollPane(blacklistTable), BorderLayout.CENTER);
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); actionPanel.setBackground(Color.WHITE);
        JButton btnReload = createBtn("Làm mới", new Color(224, 224, 224)); JButton btnAdd = createBtn("Đưa vào Blacklist", new Color(239, 154, 154)); JButton btnEdit = createBtn("Sửa lý do", new Color(255, 204, 128)); JButton btnDelete = createBtn("Gỡ khỏi Blacklist", new Color(165, 214, 167));
        actionPanel.add(btnReload); actionPanel.add(btnEdit); actionPanel.add(btnAdd); actionPanel.add(btnDelete); panel.add(actionPanel, BorderLayout.SOUTH);
        loadBlacklistData();
        btnReload.addActionListener(e -> loadBlacklistData());
        btnAdd.addActionListener(e -> {
            JTextField txtCccd = new JTextField(); JTextField txtReason = new JTextField(); Object[] fields = { "CCCD (Khách đã từng lưu trú):", txtCccd, "Lý do vi phạm nội quy:", txtReason };
            if (JOptionPane.showConfirmDialog(this, fields, "Thêm Blacklist", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try (Connection conn = DBConnection.getConnection(); PreparedStatement psCheck = conn.prepareStatement("SELECT 1 FROM guests WHERE cccd=?")) { psCheck.setString(1, txtCccd.getText().trim()); if (!psCheck.executeQuery().next()) { JOptionPane.showMessageDialog(this, "CCCD chưa từng tồn tại trong hệ thống!", "Lỗi", JOptionPane.ERROR_MESSAGE); return; } } catch(Exception ex) {}
                try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement("INSERT INTO blacklists (cccd, reason) VALUES (?, ?)")) { ps.setString(1, txtCccd.getText().trim()); ps.setString(2, txtReason.getText().trim()); ps.executeUpdate(); loadBlacklistData(); } catch (Exception ex) { JOptionPane.showMessageDialog(this, "CCCD đã có sẵn trong Blacklist!", "Lỗi", JOptionPane.ERROR_MESSAGE); }
            }
        });
        btnEdit.addActionListener(e -> {
            int row = blacklistTable.getSelectedRow(); if (row == -1) { JOptionPane.showMessageDialog(this, "Chọn 1 dòng để sửa!"); return; } String cccd = (String) blacklistModel.getValueAt(row, 0); JTextField txtReason = new JTextField((String) blacklistModel.getValueAt(row, 2));
            if (JOptionPane.showConfirmDialog(this, new Object[]{"Sửa lý do vi phạm cho CCCD " + cccd + ":", txtReason}, "Sửa Blacklist", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement("UPDATE blacklists SET reason=? WHERE cccd=?")) { ps.setString(1, txtReason.getText().trim()); ps.setString(2, cccd); ps.executeUpdate(); loadBlacklistData(); } catch (Exception ex) { ex.printStackTrace(); }
            }
        });
        btnDelete.addActionListener(e -> {
            int row = blacklistTable.getSelectedRow(); if (row == -1) return; String cccd = (String) blacklistModel.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(this, "Gỡ CCCD " + cccd + " khỏi Blacklist?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement("DELETE FROM blacklists WHERE cccd=?")) { ps.setString(1, cccd); ps.executeUpdate(); loadBlacklistData(); } catch (Exception ex) { ex.printStackTrace(); }
            }
        });
        return panel;
    }
    private void loadBlacklistData() {
        blacklistModel.setRowCount(0); String sql = "SELECT b.cccd, g.name, b.reason, b.record_date FROM blacklists b JOIN guests g ON b.cccd = g.cccd ORDER BY b.record_date DESC";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) { while (rs.next()) { blacklistModel.addRow(new Object[]{ rs.getString("cccd"), rs.getString("name"), rs.getString("reason"), rs.getTimestamp("record_date") }); } } catch (Exception e) { e.printStackTrace(); }
    }
}