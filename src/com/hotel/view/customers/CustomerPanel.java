package com.hotel.view.customers;

import com.hotel.model.Guest;
import com.hotel.service.impl.CustomerServiceImpl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private CustomerServiceImpl customerService;

    // Bảng màu chuẩn
    private final Color PRIMARY_COLOR = new Color(26, 35, 126);
    private final Color ACCENT_COLOR = new Color(46, 125, 50);

    public CustomerPanel(CustomerServiceImpl customerService) {
        this.customerService = customerService;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        add(createHeader(), BorderLayout.NORTH);
        add(createTable(), BorderLayout.CENTER);
        add(createButtons(), BorderLayout.SOUTH);

        // Load dữ liệu từ Database khi khởi tạo
        loadDataToTable();
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 20, 15, 20));
        panel.setOpaque(false);

        JLabel title = new JLabel("Danh sách Khách hàng");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(PRIMARY_COLOR);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        JTextField txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(250, 35));
        txtSearch.putClientProperty("JTextField.placeholderText", "Nhập CCCD hoặc Tên...");
        
        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.setBackground(PRIMARY_COLOR);
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);

        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        panel.add(title, BorderLayout.WEST);
        panel.add(searchPanel, BorderLayout.EAST);

        return panel;
    }

    private JScrollPane createTable() {
        String[] columns = {"STT", "CCCD/Passport", "Họ và Tên", "Số điện thoại", "Email", "Hạng Thành Viên"};
        
        // Khóa không cho người dùng edit trực tiếp trên Table
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(224, 224, 224));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(224, 224, 224)));
        return scrollPane;
    }

    private JPanel createButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        panel.setOpaque(false);

        JButton btnReload = createStyledButton("Tải lại dữ liệu", Color.GRAY);
        JButton btnAdd = createStyledButton("Thêm Khách Mới", ACCENT_COLOR);
        JButton btnEdit = createStyledButton("Cập Nhật", new Color(251, 140, 0));
        
        // Tải lại bảng
        btnReload.addActionListener(e -> loadDataToTable());

        // Thêm mới
        btnAdd.addActionListener(e -> openCustomerForm(null));

        // Cập nhật thông tin
        btnEdit.addActionListener(e -> {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần chỉnh sửa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Ép kiểu an toàn và chuẩn hóa chuỗi định danh
        String cccd = String.valueOf(model.getValueAt(row, 1)).trim();
        Guest guest = customerService.getCustomerProfile(cccd);
    
        if (guest != null) {
            openCustomerForm(guest);
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi tính toàn vẹn dữ liệu: Không tìm thấy hồ sơ cho CCCD " + cccd, "Lỗi truy xuất", JOptionPane.ERROR_MESSAGE);
        }
    });

        panel.add(btnReload);
        panel.add(btnEdit);
        panel.add(btnAdd);

        return panel;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(150, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Load dữ liệu thật vào Table
    private void loadDataToTable() {
        model.setRowCount(0); // Xóa dữ liệu cũ
        List<Guest> guests = customerService.getAllCustomers();
        int stt = 1;
        for (Guest g : guests) {
            model.addRow(new Object[]{
                    stt++,
                    g.getCccd(),
                    g.getName(),
                    g.getPhone(),
                    g.getEmail() != null ? g.getEmail() : "",
                    g.getRank() != null ? g.getRank() : "Bạc"
            });
        }
    }

    // Hiển thị Dialog Form (Dùng chung cho cả Thêm và Sửa)
    private void openCustomerForm(Guest existingGuest) {
        JTextField txtCccd = new JTextField();
        JTextField txtName = new JTextField();
        JTextField txtPhone = new JTextField();
        JTextField txtEmail = new JTextField();

        // Nếu là sửa, điền sẵn thông tin và khóa ô CCCD (Khóa chính)
        if (existingGuest != null) {
            txtCccd.setText(existingGuest.getCccd());
            txtCccd.setEditable(false); 
            txtName.setText(existingGuest.getName());
            txtPhone.setText(existingGuest.getPhone());
            txtEmail.setText(existingGuest.getEmail());
        }

        Object[] fields = {
                "CCCD / Passport (*):", txtCccd,
                "Họ và Tên (*):", txtName,
                "Số điện thoại:", txtPhone,
                "Email:", txtEmail
        };

        int option = JOptionPane.showConfirmDialog(this, fields, 
                existingGuest == null ? "THÊM KHÁCH HÀNG MỚI" : "CẬP NHẬT THÔNG TIN", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
                if (txtCccd.getText().trim().isEmpty() || txtName.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "CCCD và Họ Tên không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // FIX LỖI: Dùng Setters thay vì dùng new Guest(...)
                Guest guest = new Guest();
                guest.setCccd(txtCccd.getText().trim());
                guest.setName(txtName.getText().trim());
                guest.setPhone(txtPhone.getText().trim());
                guest.setEmail(txtEmail.getText().trim());
                
                customerService.addCustomer(guest); 
                JOptionPane.showMessageDialog(this, "Thao tác thành công!");
                loadDataToTable(); 
            }
    }
}