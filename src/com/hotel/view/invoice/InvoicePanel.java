package com.hotel.view.invoice;

import com.hotel.dao.IInvoiceDAO;
import com.hotel.dao.impl.InvoiceDAOImpl;
import com.hotel.model.Invoice;
import com.hotel.service.IStayService;
import com.hotel.util.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class InvoicePanel extends JPanel {

    private IInvoiceDAO invoiceDAO;
    private IStayService stayService;
    private JTable invTable, debtTable;
    private DefaultTableModel invModel, debtModel;

    private final Color PRIMARY_COLOR = new Color(26, 35, 126);

    public InvoicePanel(IStayService stayService) {
        this.stayService = stayService;
        this.invoiceDAO = new InvoiceDAOImpl();

        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- Header ---
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Hóa đơn & Quản lý Công nợ");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(PRIMARY_COLOR);
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // --- Tích hợp TabbedPane ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        tabbedPane.addTab("Lịch sử Hóa đơn", createInvoiceTab());
        tabbedPane.addTab("Sổ Công nợ (Đoàn/OTA)", createDebtTab());

        add(tabbedPane, BorderLayout.CENTER);
    }

    // TAB 1: LỊCH SỬ HÓA ĐƠN
    private JPanel createInvoiceTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controls.setBackground(Color.WHITE);
        JButton btnReload = new JButton("Làm mới");
        btnReload.setBackground(new Color(224, 224, 224)); btnReload.setForeground(Color.BLACK);
        
        JButton btnDetail = new JButton("Xem & In Hóa đơn PDF");
        btnDetail.setBackground(new Color(165, 214, 167)); btnDetail.setForeground(Color.BLACK);
        btnDetail.setFont(new Font("Segoe UI", Font.BOLD, 14));

        controls.add(btnReload); controls.add(btnDetail);
        panel.add(controls, BorderLayout.NORTH);

        String[] cols = {"Mã HĐ", "Phòng", "Khách (CCCD)", "Check-In", "Check-Out", "Tổng Tiền", "Trạng thái", "PT Thanh toán"};
        invModel = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        invTable = new JTable(invModel); invTable.setRowHeight(30);
        invTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(new JScrollPane(invTable), BorderLayout.CENTER);

        loadInvoiceData();

        btnReload.addActionListener(e -> loadInvoiceData());
        btnDetail.addActionListener(e -> {
            int row = invTable.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn!"); return; }
            String invoiceId = (String) invModel.getValueAt(row, 0);
            stayService.printInvoice(invoiceId);
            JOptionPane.showMessageDialog(this, "Đã trích xuất hóa đơn PDF thành công!");
        });
        return panel;
    }

    //TAB 2: QUẢN LÝ CÔNG NỢ
    private JPanel createDebtTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controls.setBackground(Color.WHITE);
        JButton btnReload = new JButton("Làm mới");
        btnReload.setBackground(new Color(224, 224, 224)); btnReload.setForeground(Color.BLACK);
        
        JButton btnPayDebt = new JButton("Chốt thanh toán khoản nợ");
        btnPayDebt.setBackground(new Color(255, 204, 128)); btnPayDebt.setForeground(Color.BLACK);
        btnPayDebt.setFont(new Font("Segoe UI", Font.BOLD, 14));

        controls.add(btnReload); controls.add(btnPayDebt);
        panel.add(controls, BorderLayout.NORTH);

        String[] cols = {"Mã HĐ", "Phòng", "Tên Khách / Đại diện OTA", "CCCD", "Ngày Check-out", "Số tiền nợ (VNĐ)"};
        debtModel = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        debtTable = new JTable(debtModel); debtTable.setRowHeight(30);
        debtTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(new JScrollPane(debtTable), BorderLayout.CENTER);

        loadDebtData();

        btnReload.addActionListener(e -> loadDebtData());
        btnPayDebt.addActionListener(e -> payDebt());

        return panel;
    }

    private void loadInvoiceData() {
        invModel.setRowCount(0);
        List<Invoice> list = invoiceDAO.findAll();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Invoice inv : list) {
            String checkIn = inv.getCheckInTime() != null ? inv.getCheckInTime().format(fmt) : "";
            String checkOut = inv.getCheckOutTime() != null ? inv.getCheckOutTime().format(fmt) : "Đang lưu trú";
            String status = inv.getCheckOutTime() == null ? "Chưa thanh toán" : "Đã Check-out";
            
            // Cảnh báo đỏ nếu hóa đơn bị treo nợ
            if ("Nợ".equals(inv.getPaymentMethod()) && inv.getCheckOutTime() != null) {
                status = "NỢ CHƯA TRẢ";
            }

            invModel.addRow(new Object[]{
                inv.getInvoiceId(), inv.getRoomId(), inv.getGuestCccd(), 
                checkIn, checkOut, String.format("%,.0f đ", inv.getTotalAmount()), status, inv.getPaymentMethod()
            });
        }
    }

    private void loadDebtData() {
        debtModel.setRowCount(0);
        String sql = "SELECT i.invoice_id, i.room_id, g.name, g.cccd, i.check_out_time, i.total_amount " +
                     "FROM invoices i JOIN guests g ON i.guest_cccd = g.cccd " +
                     "WHERE i.payment_method = 'Nợ' AND i.check_out_time IS NOT NULL ORDER BY i.check_out_time DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String checkOut = rs.getTimestamp("check_out_time") != null ? rs.getTimestamp("check_out_time").toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A";
                debtModel.addRow(new Object[]{
                    rs.getString("invoice_id"), rs.getString("room_id"), rs.getString("name"),
                    rs.getString("cccd"), checkOut, String.format("%,.0f đ", rs.getDouble("total_amount"))
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void payDebt() {
        int row = debtTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 khoản nợ để chốt thanh toán!"); return; }
        
        String invoiceId = (String) debtModel.getValueAt(row, 0);
        String amount = (String) debtModel.getValueAt(row, 5);
        String clientName = (String) debtModel.getValueAt(row, 2);
        
        String[] options = {"Chuyển khoản", "Tiền mặt", "Thẻ tín dụng"};
        int choice = JOptionPane.showOptionDialog(this, 
            "Thu hồi khoản nợ " + amount + " từ khách hàng: " + clientName + "\nKhách thanh toán bằng hình thức nào?", 
            "Chốt Công Nợ Hóa Đơn " + invoiceId, JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            
        if (choice >= 0) {
            String method = options[choice];
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("UPDATE invoices SET payment_method = ? WHERE invoice_id = ?")) {
                ps.setString(1, method);
                ps.setString(2, invoiceId);
                ps.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Đã thu hồi công nợ thành công!");
                loadDebtData(); // Cập nhật lại tab Nợ
                loadInvoiceData(); // Cập nhật lại tab Hóa đơn
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi cập nhật CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}