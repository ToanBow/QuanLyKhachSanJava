package com.hotel.view.room;

import com.hotel.dao.IGuestDAO;
import com.hotel.dao.IInvoiceDAO;
import com.hotel.dao.IRoomDAO;
import com.hotel.dao.IServiceDAO;
import com.hotel.dao.impl.GuestDAOImpl;
import com.hotel.dao.impl.InvoiceDAOImpl;
import com.hotel.dao.impl.RoomDAOImpl;
import com.hotel.dao.impl.ServiceDAOImpl;
import com.hotel.model.Guest;
import com.hotel.model.Invoice;
import com.hotel.model.Room;
import com.hotel.model.Service;
import com.hotel.service.IStayService;
import com.hotel.util.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class RoomDetailDialog extends JDialog {

    private Room room;
    private IStayService stayService;

    private IInvoiceDAO invoiceDAO = new InvoiceDAOImpl();
    private IGuestDAO guestDAO = new GuestDAOImpl();
    private IServiceDAO serviceDAO = new ServiceDAOImpl();
    private IRoomDAO roomDAO = new RoomDAOImpl();

    public RoomDetailDialog(JFrame parentFrame, Room room, IStayService stayService) {
        super(parentFrame, "Quản lý phòng: " + room.getRoomId(), ModalityType.APPLICATION_MODAL);
        this.room = room;
        this.stayService = stayService;

        setSize(550, 650);
        setLocationRelativeTo(parentFrame);
        setLayout(new BorderLayout());

        add(createRoomInfoHeader(), BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout());
        if ("Sẵn sàng".equalsIgnoreCase(room.getStatus())) {
            contentPanel.add(createCheckInForm(), BorderLayout.CENTER);
        } else if ("Có khách".equalsIgnoreCase(room.getStatus())) {
            contentPanel.add(createOccupiedManager(), BorderLayout.CENTER);
        } else if ("Chưa dọn".equalsIgnoreCase(room.getStatus())) {
            contentPanel.add(createCleaningPanel(), BorderLayout.CENTER);
        } else {
            JLabel lblMsg = new JLabel("Phòng hiện đang: " + room.getStatus(), SwingConstants.CENTER);
            lblMsg.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            contentPanel.add(lblMsg, BorderLayout.CENTER);
        }

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createRoomInfoHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(26, 35, 126));
        headerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel lblTitle = new JLabel("PHÒNG " + room.getRoomId() + " - " + room.getType());
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel detailsPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        detailsPanel.setOpaque(false);
        detailsPanel.setBorder(new EmptyBorder(5, 0, 0, 0));

        detailsPanel.add(createWhiteLabel("Tầng: " + room.getFloor()));
        detailsPanel.add(createWhiteLabel("Số giường: " + room.getBeds()));
        detailsPanel.add(createWhiteLabel("Giá ngày: " + String.format("%,.0f", room.getDailyPrice()) + " đ"));
        detailsPanel.add(createWhiteLabel("Giá giờ: " + String.format("%,.0f", room.getHourlyPrice()) + " đ"));

        headerPanel.add(detailsPanel, BorderLayout.CENTER);
        return headerPanel;
    }

    private JLabel createWhiteLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return lbl;
    }

    private JPanel createCheckInForm() {
        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBorder(new EmptyBorder(15, 15, 15, 15));

        JTextField txtName = new JTextField();
        JTextField txtCccd = new JTextField();
        JTextField txtPhone = new JTextField();
        JComboBox<String> cbGender = new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"});
        JTextField txtDob = new JTextField(); txtDob.setToolTipText("dd/MM/yyyy");
        JTextField txtHometown = new JTextField();
        JTextField txtNationality = new JTextField("Việt Nam");
        JTextField txtEmail = new JTextField();

        JComboBox<String> cbRentalType = new JComboBox<>(new String[]{"Theo ngày", "Theo giờ"});
        JTextField txtDeposit = new JTextField("0");
        JTextField txtSurcharge = new JTextField("0");
        JTextField txtDiscount = new JTextField("0");
        
        // ĐÃ BỔ SUNG CHỨC NĂNG GHI NỢ NGAY TỪ LÚC CHECK-IN
        JComboBox<String> cbPayment = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản", "Thẻ tín dụng", "Nợ"});

        form.add(new JLabel("Họ và tên (*):")); form.add(txtName);
        form.add(new JLabel("CCCD/Passport (*):")); form.add(txtCccd);
        form.add(new JLabel("Số điện thoại:")); form.add(txtPhone);
        form.add(new JLabel("Giới tính:")); form.add(cbGender);
        form.add(new JLabel("Ngày sinh (dd/MM/yyyy):")); form.add(txtDob);
        form.add(new JLabel("Quê quán:")); form.add(txtHometown);
        form.add(new JLabel("Quốc tịch:")); form.add(txtNationality);
        form.add(new JLabel("Email:")); form.add(txtEmail);
        form.add(new JLabel("--- THÔNG TIN THUÊ ---")); form.add(new JLabel(""));
        form.add(new JLabel("Hình thức thuê:")); form.add(cbRentalType);
        form.add(new JLabel("Tiền cọc trước (VNĐ):")); form.add(txtDeposit);
        form.add(new JLabel("Phí phụ thu (VNĐ):")); form.add(txtSurcharge);
        form.add(new JLabel("Giảm giá (%):")); form.add(txtDiscount);
        form.add(new JLabel("Hình thức thanh toán:")); form.add(cbPayment);

        JScrollPane scrollPane = new JScrollPane(form);
        scrollPane.setBorder(null);

        JButton btnCheckIn = new JButton("Xác nhận Check-in");
        btnCheckIn.setBackground(new Color(165, 214, 167)); 
        btnCheckIn.setForeground(Color.BLACK); 
        btnCheckIn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnCheckIn.setPreferredSize(new Dimension(100, 45));

        btnCheckIn.addActionListener(e -> {
            try {
                String cccdCheck = txtCccd.getText().trim();
                String nameCheck = txtName.getText().trim();

                if (cccdCheck.isEmpty() || nameCheck.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Tên và CCCD!"); return;
                }

                // Kiểm tra Blacklist
                boolean isBlacklisted = false;
                String blReason = "";
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement("SELECT reason FROM blacklists WHERE cccd = ?")) {
                    ps.setString(1, cccdCheck);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        isBlacklisted = true;
                        blReason = rs.getString("reason");
                    }
                } catch (Exception ex) { ex.printStackTrace(); }

                if (isBlacklisted) {
                    int warn = JOptionPane.showConfirmDialog(this, 
                        "CẢNH BÁO HỆ THỐNG!\nKhách hàng mang CCCD " + cccdCheck + " đang nằm trong DANH SÁCH ĐEN.\nLý do vi phạm: " + blReason + "\n\nBạn có CHẮC CHẮN muốn mạo hiểm cho khách này thuê phòng không?", 
                        "Lệnh Cấm Từ Quản Lý", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (warn != JOptionPane.YES_OPTION) return; 
                }

                Guest guest = new Guest();
                guest.setName(nameCheck);
                guest.setCccd(cccdCheck);
                guest.setPhone(txtPhone.getText().trim());
                guest.setEmail(txtEmail.getText().trim());
                guest.setGender(cbGender.getSelectedItem().toString());
                guest.setHomeTown(txtHometown.getText().trim());
                guest.setNationality(txtNationality.getText().trim());
                
                if (!txtDob.getText().trim().isEmpty()) {
                    guest.setBirthDate(new SimpleDateFormat("dd/MM/yyyy").parse(txtDob.getText().trim()));
                }

                Invoice invoice = new Invoice();
                invoice.setRentalType(cbRentalType.getSelectedItem().toString());
                invoice.setDeposit(Double.parseDouble(txtDeposit.getText().trim()));
                invoice.setEarlySurcharge(Double.parseDouble(txtSurcharge.getText().trim()));
                invoice.setDiscount(Double.parseDouble(txtDiscount.getText().trim()));
                invoice.setPaymentMethod(cbPayment.getSelectedItem().toString());

                if (stayService.checkIn(room.getRoomId(), guest, invoice)) {
                    JOptionPane.showMessageDialog(this, "Check-in thành công!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi hệ thống khi Check-in!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this, "Ngày sinh sai định dạng (Chuẩn: dd/MM/yyyy)!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Tiền cọc, phụ thu, giảm giá phải là số!");
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(btnCheckIn, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createOccupiedManager() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        Invoice activeInv = invoiceDAO.findActiveInvoiceByRoom(room.getRoomId());
        Guest activeGuest = null;
        if (activeInv != null) {
            activeGuest = guestDAO.findByCccd(activeInv.getGuestCccd());
        }

        tabbedPane.addTab("👤 Khách hàng", createGuestUpdateForm(activeGuest));
        tabbedPane.addTab("🛎️ Dịch vụ", createServiceManager(activeInv));
        tabbedPane.addTab("💳 Check-out", createCheckOutPanel());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(tabbedPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createGuestUpdateForm(Guest guest) {
        JPanel form = new JPanel(new GridLayout(0, 2, 10, 15));
        form.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField txtName = new JTextField(guest != null ? guest.getName() : "");
        JTextField txtCccd = new JTextField(guest != null ? guest.getCccd() : ""); txtCccd.setEditable(false);
        JTextField txtPhone = new JTextField(guest != null ? guest.getPhone() : "");
        JTextField txtEmail = new JTextField(guest != null ? guest.getEmail() : "");
        JTextField txtHometown = new JTextField(guest != null ? guest.getHomeTown() : "");

        form.add(new JLabel("CCCD/Passport:")); form.add(txtCccd);
        form.add(new JLabel("Họ và tên:")); form.add(txtName);
        form.add(new JLabel("Số điện thoại:")); form.add(txtPhone);
        form.add(new JLabel("Email:")); form.add(txtEmail);
        form.add(new JLabel("Quê quán:")); form.add(txtHometown);

        JButton btnUpdate = new JButton("Cập nhật thông tin");
        btnUpdate.setBackground(new Color(255, 204, 128)); 
        btnUpdate.setForeground(Color.BLACK); 
        btnUpdate.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btnUpdate.addActionListener(e -> {
            if(guest == null) return;
            guest.setName(txtName.getText().trim());
            guest.setPhone(txtPhone.getText().trim());
            guest.setEmail(txtEmail.getText().trim());
            guest.setHomeTown(txtHometown.getText().trim());
            
            if(stayService.updateStayInformation(room.getRoomId(), guest)) {
                JOptionPane.showMessageDialog(this, "Đã cập nhật thông tin khách!");
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(form, BorderLayout.CENTER);
        panel.add(btnUpdate, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createServiceManager(Invoice invoice) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        List<Service> services = serviceDAO.getAllServices();
        JComboBox<String> cbServices = new JComboBox<>();
        for (Service s : services) {
            cbServices.addItem(s.getServiceId() + " - " + s.getName() + " (" + String.format("%,.0f", s.getPrice()) + ")");
        }
        
        JSpinner spnQty = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));
        
        JButton btnAddService = new JButton("Thêm DV");
        btnAddService.setBackground(new Color(165, 214, 167)); 
        btnAddService.setForeground(Color.BLACK); 

        topPanel.add(new JLabel("Chọn DV:"));
        topPanel.add(cbServices);
        topPanel.add(new JLabel("SL:"));
        topPanel.add(spnQty);
        topPanel.add(btnAddService);

        DefaultTableModel svcModel = new DefaultTableModel(new String[]{"Mã DV", "Tên DV", "SL", "Thành tiền"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable svcTable = new JTable(svcModel);
        loadServiceTable(svcModel, invoice != null ? invoice.getInvoiceId() : "");

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnDeleteSvc = new JButton("Xóa Dịch vụ đã chọn");
        btnDeleteSvc.setBackground(new Color(239, 154, 154)); 
        btnDeleteSvc.setForeground(Color.BLACK); 
        bottomPanel.add(btnDeleteSvc);

        btnAddService.addActionListener(e -> {
            if (invoice == null) return;
            int idx = cbServices.getSelectedIndex();
            if (idx >= 0) {
                Service selectedSvc = services.get(idx);
                int qty = (int) spnQty.getValue();
                
                if (selectedSvc.getInventory() < qty) {
                    JOptionPane.showMessageDialog(this, "Kho chỉ còn " + selectedSvc.getInventory() + " sản phẩm. Không đủ để thêm!", "Hết hàng", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                stayService.manageRoomServices(room.getRoomId(), selectedSvc, qty, true);
                selectedSvc.setInventory(selectedSvc.getInventory() - qty); 
                loadServiceTable(svcModel, invoice.getInvoiceId());
            }
        });

        btnDeleteSvc.addActionListener(e -> {
            int row = svcTable.getSelectedRow();
            if (row == -1 || invoice == null) {
                JOptionPane.showMessageDialog(this, "Chọn một dịch vụ trong bảng để xóa!"); return;
            }
            String serviceId = String.valueOf(svcModel.getValueAt(row, 0));
            int qty = Integer.parseInt(String.valueOf(svcModel.getValueAt(row, 2))); 
            
            removeServiceFromDB(invoice.getInvoiceId(), serviceId);
            serviceDAO.updateInventory(serviceId, qty); 
            
            for (Service s : services) {
                if(s.getServiceId().equals(serviceId)) {
                    s.setInventory(s.getInventory() + qty);
                    break;
                }
            }
            loadServiceTable(svcModel, invoice.getInvoiceId());
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(svcTable), BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadServiceTable(DefaultTableModel model, String invoiceId) {
        model.setRowCount(0);
        if (invoiceId == null || invoiceId.isEmpty()) return;
        String sql = "SELECT s.service_id, s.name, su.quantity, (su.quantity * su.price_at_time) as subtotal " +
                     "FROM service_usage su JOIN services s ON su.service_id = s.service_id WHERE su.invoice_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, invoiceId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("service_id"), rs.getString("name"),
                        rs.getInt("quantity"), String.format("%,.0f", rs.getDouble("subtotal"))
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void removeServiceFromDB(String invoiceId, String serviceId) {
        String sql = "DELETE FROM service_usage WHERE invoice_id = ? AND service_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, invoiceId); ps.setString(2, serviceId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ================== CẬP NHẬT GIAO DIỆN CHECK-OUT ==================
    private JPanel createCheckOutPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JLabel lblInfo = new JLabel("<html><div style='text-align: center; font-size: 16px;'>"
                + "Phòng đang có khách lưu trú.<br><br>"
                + "Vui lòng kiểm tra kỹ các <b>Dịch vụ phát sinh</b> ở Tab bên cạnh<br>trước khi tiến hành chốt hóa đơn.</div></html>", SwingConstants.CENTER);
        
        JPanel paymentPanel = new JPanel(new FlowLayout());
        paymentPanel.setBackground(Color.WHITE);
        paymentPanel.add(new JLabel("Hình thức chốt:"));
        
        // Bổ sung ô cập nhật phương thức thanh toán ngay lúc Check-out
        JComboBox<String> cbFinalPayment = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản", "Thẻ tín dụng", "Nợ"});
        Invoice activeInv = invoiceDAO.findActiveInvoiceByRoom(room.getRoomId());
        if (activeInv != null && activeInv.getPaymentMethod() != null) {
            cbFinalPayment.setSelectedItem(activeInv.getPaymentMethod());
        }
        paymentPanel.add(cbFinalPayment);

        JPanel centerWrap = new JPanel(new BorderLayout());
        centerWrap.setOpaque(false);
        centerWrap.add(lblInfo, BorderLayout.CENTER);
        centerWrap.add(paymentPanel, BorderLayout.SOUTH);
        
        JButton btnCheckOut = new JButton("Thực hiện Trả phòng & Chốt Hóa đơn");
        btnCheckOut.setBackground(new Color(239, 154, 154)); 
        btnCheckOut.setForeground(Color.BLACK); 
        btnCheckOut.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnCheckOut.setPreferredSize(new Dimension(100, 50));

        btnCheckOut.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận Check-out phòng " + room.getRoomId() + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // Cập nhật phương thức thanh toán mới (VD: Báo ghi nợ) vào Database trước khi chốt
                if (activeInv != null) {
                    try (Connection conn = DBConnection.getConnection();
                         PreparedStatement ps = conn.prepareStatement("UPDATE invoices SET payment_method = ? WHERE invoice_id = ?")) {
                        ps.setString(1, (String) cbFinalPayment.getSelectedItem());
                        ps.setString(2, activeInv.getInvoiceId());
                        ps.executeUpdate();
                    } catch(Exception ex) { ex.printStackTrace(); }
                }

                Invoice invoice = stayService.processCheckOut(room.getRoomId());
                if (invoice != null) {
                    JOptionPane.showMessageDialog(this, "Check-out hoàn tất!\nTổng thanh toán: " + String.format("%,.0f", invoice.getTotalAmount()) + " VND");
                    stayService.printInvoice(invoice.getInvoiceId());
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Đã dọn dẹp hệ thống phòng bị kẹt!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                }
            }
        });

        panel.add(centerWrap, BorderLayout.CENTER);
        panel.add(btnCheckOut, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createCleaningPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JLabel lblInfo = new JLabel("<html><div style='text-align: center; font-size: 16px;'>"
                + "Phòng đang trong trạng thái chờ dọn dẹp.<br><br>"
                + "Nhân viên buồng phòng đã hoàn tất công việc?</div></html>", SwingConstants.CENTER);
        
        JButton btnCleaned = new JButton("Xác nhận Đã dọn xong");
        btnCleaned.setBackground(new Color(165, 214, 167)); 
        btnCleaned.setForeground(Color.BLACK); 
        btnCleaned.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnCleaned.setPreferredSize(new Dimension(100, 50));

        btnCleaned.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận phòng " + room.getRoomId() + " đã dọn xong?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (roomDAO.updateStatus(room.getRoomId(), "Sẵn sàng")) {
                    JOptionPane.showMessageDialog(this, "Đã cập nhật phòng thành Sẵn sàng!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi cập nhật CSDL.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        panel.add(lblInfo, BorderLayout.CENTER);
        panel.add(btnCleaned, BorderLayout.SOUTH);
        return panel;
    }
}