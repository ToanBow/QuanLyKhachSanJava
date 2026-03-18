package com.hotel.service.impl;

import com.hotel.dao.IGuestDAO;
import com.hotel.dao.IInvoiceDAO;
import com.hotel.dao.IRoomDAO;
import com.hotel.dao.impl.GuestDAOImpl;
import com.hotel.dao.impl.InvoiceDAOImpl;
import com.hotel.dao.impl.RoomDAOImpl;
import com.hotel.model.Guest;
import com.hotel.model.Invoice;
import com.hotel.model.Room;
import com.hotel.model.Service;
import com.hotel.service.IStayService;
import com.hotel.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;

public class StayServiceImpl implements IStayService {

    private IRoomDAO roomDAO = new RoomDAOImpl();
    private IInvoiceDAO invoiceDAO = new InvoiceDAOImpl();
    private IGuestDAO guestDAO = new GuestDAOImpl();

    @Override
    public boolean checkIn(String roomId, Guest guest, Invoice bookingDetails) {
        try {
            // 1. Kiểm tra trạng thái phòng
            Room room = roomDAO.findByRoomId(roomId);
            if (room == null || !"Sẵn sàng".equals(room.getStatus())) {
                System.err.println("Lỗi: Phòng không sẵn sàng!");
                return false;
            }

            // 2. Thêm khách hàng (nếu chưa có)
            Guest existingGuest = guestDAO.findByCccd(guest.getCccd());
            if (existingGuest == null) {
                boolean isGuestSaved = guestDAO.insert(guest);
                if (!isGuestSaved) {
                    System.err.println("Lỗi: Không thể lưu thông tin Khách hàng vào CSDL.");
                    return false;
                }
            }

            // 3. Tạo mã hóa đơn an toàn
            bookingDetails.setInvoiceId("INV-" + System.currentTimeMillis());
            bookingDetails.setRoomId(roomId);
            bookingDetails.setGuestCccd(guest.getCccd());
            bookingDetails.setCheckInTime(LocalDateTime.now());
            
            // 4. Kiểm tra nghiêm ngặt việc tạo hóa đơn
            boolean isInvoiceCreated = invoiceDAO.insert(bookingDetails);
            if (!isInvoiceCreated) {
                System.err.println("LỖI DB NGHIÊM TRỌNG: Không thể lưu Hóa đơn. Hủy Check-in để bảo vệ phòng không bị kẹt!");
                return false; 
            }

            // 5. Thành công 100% thì mới đổi màu phòng
            roomDAO.updateStatus(roomId, "Có khách");
            System.out.println("Check-in thành công cho phòng: " + roomId);
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateStayInformation(String roomId, Guest updatedGuest) {
        Invoice activeInvoice = invoiceDAO.findActiveInvoiceByRoom(roomId);
        if (activeInvoice == null) {
            return false;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String sqlUpsertGuest = "INSERT INTO guests (cccd, name, phone, gender, birth_date, home_town, email, nationality) " +
                                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                                    "ON DUPLICATE KEY UPDATE name=VALUES(name), phone=VALUES(phone), gender=VALUES(gender), " +
                                    "birth_date=VALUES(birth_date), home_town=VALUES(home_town), email=VALUES(email), nationality=VALUES(nationality)";
                                    
            try (PreparedStatement psGuest = conn.prepareStatement(sqlUpsertGuest)) {
                psGuest.setString(1, updatedGuest.getCccd());
                psGuest.setString(2, updatedGuest.getName());
                psGuest.setString(3, updatedGuest.getPhone());
                psGuest.setString(4, updatedGuest.getGender());
                
                if (updatedGuest.getBirthDate() != null) {
                    psGuest.setDate(5, new java.sql.Date(updatedGuest.getBirthDate().getTime()));
                } else {
                    psGuest.setNull(5, java.sql.Types.DATE);
                }
                
                psGuest.setString(6, updatedGuest.getHomeTown());
                psGuest.setString(7, updatedGuest.getEmail());
                psGuest.setString(8, updatedGuest.getNationality());
                psGuest.executeUpdate();
            }

            if (!activeInvoice.getGuestCccd().equals(updatedGuest.getCccd())) {
                String sqlUpdateInvoice = "UPDATE invoices SET guest_cccd = ? WHERE invoice_id = ?";
                try (PreparedStatement psInvoice = conn.prepareStatement(sqlUpdateInvoice)) {
                    psInvoice.setString(1, updatedGuest.getCccd());
                    psInvoice.setString(2, activeInvoice.getInvoiceId());
                    psInvoice.executeUpdate();
                }
            }

            conn.commit(); 
            return true;

        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    @Override
    public void manageRoomServices(String roomId, Service service, int quantity, boolean isAddition) {
        Invoice activeInvoice = invoiceDAO.findActiveInvoiceByRoom(roomId);
        if (activeInvoice == null) {
            System.err.println("Lỗi: Không tìm thấy hóa đơn cho phòng " + roomId);
            return;
        }

        String sql;
        if (isAddition) {
            sql = "INSERT INTO service_usage (invoice_id, service_id, quantity, price_at_time) VALUES (?, ?, ?, ?) " +
                  "ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity)";
        } else {
            sql = "DELETE FROM service_usage WHERE invoice_id = ? AND service_id = ?";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, activeInvoice.getInvoiceId());
            ps.setString(2, service.getServiceId());

            if (isAddition) {
                ps.setInt(3, quantity);
                ps.setDouble(4, service.getPrice());
            }
            
            ps.executeUpdate();
            
            // TRỪ HOẶC HOÀN LẠI KHO SẢN PHẨM
            com.hotel.dao.IServiceDAO serviceDAO = new com.hotel.dao.impl.ServiceDAOImpl();
            serviceDAO.updateInventory(service.getServiceId(), isAddition ? -quantity : quantity);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Invoice processCheckOut(String roomId) {
        Invoice invoice = invoiceDAO.findActiveInvoiceByRoom(roomId);
        
        // GIẢI CỨU PHÒNG KẸT: Nếu phòng màu đỏ nhưng không có hóa đơn, tự động dọn phòng về "Chưa dọn"
        if (invoice == null) {
            System.err.println("Phát hiện phòng " + roomId + " bị kẹt ảo. Tiến hành dọn dẹp hệ thống!");
            roomDAO.updateStatus(roomId, "Chưa dọn");
            return null;
        }

        Room room = roomDAO.findByRoomId(roomId);
        if (room == null) return null;

        invoice.setCheckOutTime(LocalDateTime.now());
        
        long totalMinutes = 0;
        if (invoice.getCheckInTime() != null) {
            totalMinutes = Duration.between(invoice.getCheckInTime(), invoice.getCheckOutTime()).toMinutes();
        }
        
        long hours = totalMinutes / 60;
        if (totalMinutes % 60 > 15) hours++;
        if (hours == 0) hours = 1;

        double roomCost = ("Theo ngày".equalsIgnoreCase(invoice.getRentalType())) 
            ? Math.max(1, hours / 24 + (hours % 24 >= 4 ? 1 : 0)) * room.getDailyPrice() 
            : hours * room.getHourlyPrice();

        invoice.calculateActualAmount(roomCost);
        invoice.setStatus("PAID"); // Set trạng thái cho Object Java
        
        if (invoice.getPaymentMethod() == null) {
             invoice.setPaymentMethod("Tiền mặt");
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // FIX LỖI CHECK-OUT: Không gọi cột 'status' ở câu UPDATE này (vì CSDL không có cột status trong bảng invoices)
            // Thêm payment_date = NOW() để ghi nhận thời gian doanh thu vào Database
            String sqlUpdateInvoice = "UPDATE invoices SET check_out_time = ?, total_amount = ?, payment_method = ?, payment_date = NOW() WHERE invoice_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdateInvoice)) {
                ps.setTimestamp(1, java.sql.Timestamp.valueOf(invoice.getCheckOutTime()));
                ps.setDouble(2, invoice.getTotalAmount());
                ps.setString(3, invoice.getPaymentMethod());
                ps.setString(4, invoice.getInvoiceId());
                ps.executeUpdate();
            }

            // Đổi trạng thái phòng
            String sqlUpdateRoom = "UPDATE rooms SET status = 'Chưa dọn' WHERE room_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdateRoom)) {
                ps.setString(1, roomId);
                ps.executeUpdate();
            }

            conn.commit();
            return invoice;
        } catch (Exception e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return null;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    @Override
    public void printInvoice(String invoiceId) {
        Invoice invoice = invoiceDAO.findById(invoiceId);
        if (invoice == null) return;
        try {
            PaymentServiceImpl paymentService = new PaymentServiceImpl();
            paymentService.printInvoiceToFile(invoice);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}