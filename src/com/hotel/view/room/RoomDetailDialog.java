package com.hotel.view.room;

import com.hotel.model.Guest;
import com.hotel.model.Invoice;
import com.hotel.model.Room;
import com.hotel.service.IStayService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RoomDetailDialog extends JDialog {

    private Room room;
    private IStayService stayService;

    public RoomDetailDialog(JFrame parentFrame, Room room, IStayService stayService) {
        super(parentFrame, "Quản lý phòng: " + room.getRoomId(), ModalityType.APPLICATION_MODAL);
        this.room = room;
        this.stayService = stayService;

        setSize(450, 500);
        setLocationRelativeTo(parentFrame);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(26, 35, 126));
        JLabel lblTitle = new JLabel("PHÒNG " + room.getRoomId() + " - " + room.getType());
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle);
        add(headerPanel, BorderLayout.NORTH);

        // Content phân nhánh theo trạng thái phòng
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        if ("Sẵn sàng".equalsIgnoreCase(room.getStatus())) {
            contentPanel.add(createCheckInForm(), BorderLayout.CENTER);
        } else if ("Có khách".equalsIgnoreCase(room.getStatus())) {
            contentPanel.add(createCheckOutPanel(), BorderLayout.CENTER);
        } else {
            // Trạng thái Chưa dọn / Đang sửa chữa
            JLabel lblMsg = new JLabel("Phòng hiện đang: " + room.getStatus(), SwingConstants.CENTER);
            lblMsg.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            contentPanel.add(lblMsg, BorderLayout.CENTER);
        }

        add(contentPanel, BorderLayout.CENTER);
    }

    // ================== FORM CHECK-IN ==================
    private JPanel createCheckInForm() {
        JPanel form = new JPanel(new GridLayout(7, 2, 10, 15));
        
        JTextField txtCccd = new JTextField();
        JTextField txtName = new JTextField();
        JTextField txtPhone = new JTextField();
        JTextField txtEmail = new JTextField();
        JComboBox<String> cbRentalType = new JComboBox<>(new String[]{"Theo ngày", "Theo giờ"});
        JTextField txtDeposit = new JTextField("0");

        form.add(new JLabel("CCCD/Passport: (*)")); form.add(txtCccd);
        form.add(new JLabel("Tên khách hàng: (*)")); form.add(txtName);
        form.add(new JLabel("Số điện thoại:")); form.add(txtPhone);
        form.add(new JLabel("Email:")); form.add(txtEmail);
        form.add(new JLabel("Hình thức thuê:")); form.add(cbRentalType);
        form.add(new JLabel("Tiền cọc (VND):")); form.add(txtDeposit);

        JButton btnCheckIn = new JButton("Nhận phòng (Check-in)");
        btnCheckIn.setBackground(new Color(46, 125, 50));
        btnCheckIn.setForeground(Color.WHITE);
        btnCheckIn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btnCheckIn.addActionListener(e -> {
            try {
                if (txtCccd.getText().trim().isEmpty() || txtName.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "CCCD và Tên không được để trống!");
                    return;
                }

                Guest guest = new Guest();
                guest.setCccd(txtCccd.getText().trim());
                guest.setName(txtName.getText().trim());
                guest.setPhone(txtPhone.getText().trim());
                guest.setEmail(txtEmail.getText().trim());

                Invoice invoice = new Invoice();
                invoice.setRentalType((String) cbRentalType.getSelectedItem());
                invoice.setDeposit(Double.parseDouble(txtDeposit.getText()));

                // Gọi Service xử lý CSDL
                boolean success = stayService.checkIn(room.getRoomId(), guest, invoice);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Check-in thành công phòng " + room.getRoomId());
                    dispose(); // Đóng Dialog, Panel ngoài sẽ tự động reload lại lưới phòng
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi hệ thống khi Check-in!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Tiền cọc phải là số hợp lệ!");
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(form, BorderLayout.CENTER);
        panel.add(btnCheckIn, BorderLayout.SOUTH);
        return panel;
    }

    // ================== FORM CHECK-OUT ==================
    private JPanel createCheckOutPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JLabel lblInfo = new JLabel("<html><div style='text-align: center; font-size: 14px;'>"
                + "Phòng đang có khách lưu trú.<br><br>"
                + "Bạn có muốn thực hiện chốt hóa đơn và trả phòng không?</div></html>", SwingConstants.CENTER);
        
        JButton btnCheckOut = new JButton("Trả phòng & Thanh toán");
        btnCheckOut.setBackground(new Color(198, 40, 40));
        btnCheckOut.setForeground(Color.WHITE);
        btnCheckOut.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btnCheckOut.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận Check-out phòng " + room.getRoomId() + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Invoice invoice = stayService.processCheckOut(room.getRoomId());
                if (invoice != null) {
                    JOptionPane.showMessageDialog(this, "Check-out hoàn tất!\nTổng thanh toán: " + String.format("%,.0f", invoice.getTotalAmount()) + " VND");
                    stayService.printInvoice(invoice.getInvoiceId()); // Xuất file hóa đơn
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Có lỗi xảy ra trong quá trình Check-out.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        panel.add(lblInfo, BorderLayout.CENTER);
        panel.add(btnCheckOut, BorderLayout.SOUTH);
        return panel;
    }
}