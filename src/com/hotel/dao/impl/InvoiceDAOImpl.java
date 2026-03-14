package com.hotel.dao.impl;

import com.hotel.dao.IInvoiceDAO;
import com.hotel.model.Invoice;
import com.hotel.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InvoiceDAOImpl implements IInvoiceDAO {

    // Hàm phụ trợ (Helper method) để map dữ liệu từ ResultSet sang đối tượng Invoice
    private Invoice mapRowToInvoice(ResultSet rs) throws SQLException {
        Invoice invoice = new Invoice();
        invoice.setInvoiceId(rs.getString("invoice_id"));
        invoice.setRoomId(rs.getString("room_id"));
        invoice.setGuestCccd(rs.getString("guest_cccd"));
        
        // Chuyển đổi java.sql.Timestamp sang java.time.LocalDateTime
        if (rs.getTimestamp("check_in_time") != null) {
            invoice.setCheckInTime(rs.getTimestamp("check_in_time").toLocalDateTime());
        }
        if (rs.getTimestamp("check_out_time") != null) {
            invoice.setCheckOutTime(rs.getTimestamp("check_out_time").toLocalDateTime());
        }
        
        invoice.setRentalType(rs.getString("rental_type"));
        invoice.setDeposit(rs.getDouble("deposit"));
        invoice.setEarlySurcharge(rs.getDouble("early_surcharge"));
        invoice.setLateSurcharge(rs.getDouble("late_surcharge"));
        invoice.setDiscount(rs.getDouble("discount"));
        invoice.setPaymentMethod(rs.getString("payment_method"));
        invoice.setTotalAmount(rs.getDouble("total_amount"));
        
        // Suy luận trạng thái: Nếu chưa có giờ check-out thì là CHECKED_IN, ngược lại là PAID
        if (rs.getTimestamp("check_out_time") == null) {
            invoice.setStatus("CHECKED_IN");
        } else {
            invoice.setStatus("PAID");
        }
        
        return invoice;
    }

    @Override
    public boolean insert(Invoice invoice) {
        String sql = "INSERT INTO invoices (invoice_id, room_id, guest_cccd, check_in_time, check_out_time, "
               + "rental_type, deposit, early_surcharge, late_surcharge, discount, payment_method, total_amount, payment_date) "
               + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) { 
            
            ps.setString(1, invoice.getInvoiceId());
            ps.setString(2, invoice.getRoomId());
            ps.setString(3, invoice.getGuestCccd());
            ps.setTimestamp(4, java.sql.Timestamp.valueOf(invoice.getCheckInTime()));
            ps.setTimestamp(5, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now())); // Check-out lúc này
            ps.setString(6, invoice.getRentalType());
            ps.setDouble(7, invoice.getDeposit());
            ps.setDouble(8, invoice.getEarlySurcharge());
            ps.setDouble(9, invoice.getLateSurcharge());
            ps.setDouble(10, invoice.getDiscount());
            ps.setString(11, invoice.getPaymentMethod());
            ps.setDouble(12, invoice.getTotalAmount());   
            
            return ps.executeUpdate() > 0;
        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public double getRevenueByShift(String employeeId, Date shiftTime) {
        String sql = "SELECT SUM(total_amount) FROM invoices WHERE staff_id = ? AND DATE(payment_date) = CURDATE()";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {            
            ps.setString(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }   

    @Override 
    public Invoice findActiveInvoiceByRoom(String roomId) { 
        // Hóa đơn "active" là hóa đơn của phòng có người nhưng chưa check-out
        String sql = "SELECT * FROM invoices WHERE room_id = ? AND check_out_time IS NULL ORDER BY check_in_time DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToInvoice(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; 
    }

    @Override 
    public List<Invoice> getInvoicesByPeriod(Date start, Date end) { 
        List<Invoice> list = new ArrayList<>();
        // Tìm kiếm theo ngày thanh toán (payment_date) để phục vụ báo cáo doanh thu
        String sql = "SELECT * FROM invoices WHERE payment_date BETWEEN ? AND ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setTimestamp(1, new java.sql.Timestamp(start.getTime()));
            ps.setTimestamp(2, new java.sql.Timestamp(end.getTime()));
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToInvoice(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list; 
    }

    @Override
    public Invoice findById(String invoiceId) {
        String sql = "SELECT * FROM invoices WHERE invoice_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToInvoice(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Invoice> findAll() {
        List<Invoice> list = new ArrayList<>();
        String sql = "SELECT * FROM invoices";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                list.add(mapRowToInvoice(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}