package com.hotel.view.customers;

import com.hotel.model.Guest;
import com.hotel.service.impl.CustomerServiceImpl;
import com.hotel.util.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CustomerPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private CustomerServiceImpl customerService;
    private JTextField txtSearch;
    
    // Mảng lưu trữ đối tượng gốc để chống lỗi trích xuất ngược từ Bảng hiển thị
    private List<Guest> currentGuests = new ArrayList<>();

    private final Color PRIMARY_COLOR = new Color(26, 35, 126);

    public CustomerPanel(CustomerServiceImpl customerService) {
        this.customerService = customerService;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        add(createHeader(), BorderLayout.NORTH);
        add(createTable(), BorderLayout.CENTER);
        add(createButtons(), BorderLayout.SOUTH);

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
        btnSearch.setBackground(new Color(224, 224, 224));
        btnSearch.setForeground(Color.BLACK); 
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSearch.setFocusPainted(false);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnSearch.addActionListener(e -> searchCustomer());

        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        panel.add(title, BorderLayout.WEST);
        panel.add(searchPanel, BorderLayout.EAST);

        return panel;
    }

    private JScrollPane createTable() {
        String[] columns = {"STT", "CCCD/Passport", "Họ và Tên", "Số điện thoại", "Email", "Hạng Thành Viên"};
        
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
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

        JButton btnReload = createStyledButton("Tải lại dữ liệu", new Color(224, 224, 224)); 
        JButton btnAdd = createStyledButton("Thêm Khách Mới", new Color(129, 199, 132)); 
        JButton btnEdit = createStyledButton("Cập Nhật", new Color(255, 183, 77)); 
        
        btnReload.addActionListener(e -> {
            txtSearch.setText("");
            loadDataToTable();
        });

        btnAdd.addActionListener(e -> openCustomerForm(null));

        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một khách hàng trong bảng để sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Lấy trực tiếp đối tượng Guest gốc được lưu ngầm thay vì đọc trên dòng của JTable
            Guest guest = currentGuests.get(row);
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
        btn.setForeground(Color.BLACK); 
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(150, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void loadDataToTable() {
        model.setRowCount(0); 
        currentGuests = customerService.getAllCustomers();
        int stt = 1;
        for (Guest g : currentGuests) {
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

    private void searchCustomer() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            loadDataToTable(); 
            return;
        }

        model.setRowCount(0); 
        List<Guest> guests = customerService.getAllCustomers();
        currentGuests.clear();
        int stt = 1;
        for (Guest g : guests) {
            String cccd = g.getCccd() != null ? g.getCccd().toLowerCase() : "";
            String name = g.getName() != null ? g.getName().toLowerCase() : "";
            
            if (cccd.contains(keyword) || name.contains(keyword)) {
                currentGuests.add(g); // Lưu đối tượng gốc vào mảng tạm
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

    private void openCustomerForm(Guest existingGuest) {
        JTextField txtCccd = new JTextField();
        JTextField txtName = new JTextField();
        JTextField txtPhone = new JTextField();
        JTextField txtEmail = new JTextField();
        JComboBox<String> cbGender = new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"});
        JTextField txtDob = new JTextField(); txtDob.setToolTipText("dd/MM/yyyy");
        JTextField txtHometown = new JTextField();
        JTextField txtNationality = new JTextField("Việt Nam");

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        // FIX LỖI: Điền đầy đủ tất cả các trường dữ liệu khi Update
        if (existingGuest != null) {
            txtCccd.setText(existingGuest.getCccd());
            txtCccd.setEditable(false); 
            txtName.setText(existingGuest.getName());
            txtPhone.setText(existingGuest.getPhone());
            txtEmail.setText(existingGuest.getEmail());
            if (existingGuest.getGender() != null) cbGender.setSelectedItem(existingGuest.getGender());
            if (existingGuest.getBirthDate() != null) txtDob.setText(sdf.format(existingGuest.getBirthDate()));
            txtHometown.setText(existingGuest.getHomeTown());
            txtNationality.setText(existingGuest.getNationality());
        }

        Object[] fields = {
                "CCCD / Passport (*):", txtCccd,
                "Họ và Tên (*):", txtName,
                "Số điện thoại:", txtPhone,
                "Email:", txtEmail,
                "Giới tính:", cbGender,
                "Ngày sinh (dd/MM/yyyy):", txtDob,
                "Quê quán:", txtHometown,
                "Quốc tịch:", txtNationality
        };

        int option = JOptionPane.showConfirmDialog(this, fields, 
                existingGuest == null ? "THÊM KHÁCH HÀNG MỚI" : "CẬP NHẬT ĐẦY ĐỦ THÔNG TIN KHÁCH HÀNG", 
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
            guest.setGender(cbGender.getSelectedItem().toString());
            guest.setHomeTown(txtHometown.getText().trim());
            guest.setNationality(txtNationality.getText().trim());

            try {
                if (!txtDob.getText().trim().isEmpty()) {
                    guest.setBirthDate(sdf.parse(txtDob.getText().trim()));
                }
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this, "Ngày sinh sai định dạng (Chuẩn: dd/MM/yyyy)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (existingGuest == null) {
                // THÊM MỚI
                customerService.addCustomer(guest); 
                JOptionPane.showMessageDialog(this, "Đã thêm khách hàng mới thành công!");
            } else {
                // UPDATE CỨNG QUA CSDL ĐỂ CHỐNG LỖI ĐẢO DỮ LIỆU
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement("UPDATE guests SET name=?, phone=?, email=?, gender=?, birth_date=?, home_town=?, nationality=? WHERE cccd=?")) {
                    ps.setString(1, guest.getName());
                    ps.setString(2, guest.getPhone());
                    ps.setString(3, guest.getEmail());
                    ps.setString(4, guest.getGender());
                    if (guest.getBirthDate() != null) {
                        ps.setDate(5, new java.sql.Date(guest.getBirthDate().getTime()));
                    } else {
                        ps.setNull(5, java.sql.Types.DATE);
                    }
                    ps.setString(6, guest.getHomeTown());
                    ps.setString(7, guest.getNationality());
                    ps.setString(8, guest.getCccd());
                    
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Đã cập nhật chuẩn xác thông tin Khách hàng!");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Lỗi khi lưu dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
            
            loadDataToTable(); // Refresh lại lưới
        }
    }
}