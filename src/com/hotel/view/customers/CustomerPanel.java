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
    private JTextField txtSearch;

    private final Color PRIMARY_COLOR = new Color(26, 35, 126);

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
        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(250, 35));
        
        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.setBackground(new Color(224, 224, 224)); // Nền xám nhạt
        btnSearch.setForeground(Color.BLACK); // CHỮ ĐEN
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSearch.setFocusPainted(false);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // KÍCH HOẠT SỰ KIỆN TÌM KIẾM
        btnSearch.addActionListener(e -> searchCustomer());

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

        // Làm sáng màu nền của nút để chữ đen nổi bật hơn
        JButton btnReload = createStyledButton("Tải lại dữ liệu", new Color(224, 224, 224)); 
        JButton btnAdd = createStyledButton("Thêm Khách Mới", new Color(129, 199, 132)); 
        JButton btnEdit = createStyledButton("Cập Nhật", new Color(255, 183, 77)); 
        
        // Sự kiện Tải lại bảng
        btnReload.addActionListener(e -> {
            txtSearch.setText("");
            loadDataToTable();
        });

        // Sự kiện Thêm mới
        btnAdd.addActionListener(e -> openCustomerForm(null));

        // Sự kiện Cập nhật thông tin
        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một khách hàng trong bảng để sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // FIX LỖI ĐẢO DỮ LIỆU: Trích xuất trực tiếp từ lưới (Table) đang hiển thị chuẩn xác
            Guest guest = new Guest();
            guest.setCccd(String.valueOf(model.getValueAt(row, 1)).trim());
            guest.setName(String.valueOf(model.getValueAt(row, 2)).trim());
            guest.setPhone(String.valueOf(model.getValueAt(row, 3)).trim());
            guest.setEmail(String.valueOf(model.getValueAt(row, 4)).trim());
            
            openCustomerForm(guest);
        });

        panel.add(btnReload);
        panel.add(btnEdit);
        panel.add(btnAdd);

        return panel;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.BLACK); // CHỮ MÀU ĐEN YÊU CẦU CỦA BẠN
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

    // Xử lý Tìm Kiếm Khách Hàng
    private void searchCustomer() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            loadDataToTable(); // Nếu ô tìm kiếm trống -> Hiện toàn bộ
            return;
        }

        model.setRowCount(0); 
        List<Guest> guests = customerService.getAllCustomers();
        int stt = 1;
        for (Guest g : guests) {
            String cccd = g.getCccd() != null ? g.getCccd().toLowerCase() : "";
            String name = g.getName() != null ? g.getName().toLowerCase() : "";
            
            // Tìm theo tên hoặc theo CCCD
            if (cccd.contains(keyword) || name.contains(keyword)) {
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
            String cccd = txtCccd.getText().trim();
            String name = txtName.getText().trim();
            
            if (cccd.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "CCCD và Họ Tên không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Guest guest = new Guest();
            guest.setCccd(cccd);
            guest.setName(name);
            guest.setPhone(txtPhone.getText().trim());
            guest.setEmail(txtEmail.getText().trim());
            
            // Gọi Service để lưu vào DB
            customerService.addCustomer(guest); 
            JOptionPane.showMessageDialog(this, "Thao tác thành công!");
            
            loadDataToTable(); // Refresh lại lưới
        }
    }
}