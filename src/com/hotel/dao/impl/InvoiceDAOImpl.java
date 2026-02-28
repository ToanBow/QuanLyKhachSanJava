/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hotel.dao.impl;
import com.hotel.dao.IInvoiceDAO;
import com.hotel.model.Invoice;
import com.hotel.util.DBConnection; 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;

/**
 *
 * @author DELL
 */
public class InvoiceDAOImpl implements IInvoiceDAO {
    @Override
    public boolean insert(Invoice invoice) {
        //luu hoa don moi khi khach checkout
        //ghi nhan doanh thu vao database
        String sql = "INSERT INTO invoices (invoice_id, room_id, guest_cccd, total_amount, payment_method, payment_date) "
                   + "VALUES (?, ?, ?, ?, ?, NOW())";
        try (Connection conn = DBConnection.getConnection(); // mo ket noi
            PreparedStatement ps = conn.prepareStatement(sql)) { // truy van
            
            ps.setString(1, invoice.getInvoiceId());
            ps.setString(2, invoice.getRoomId());
            ps.setString(3, invoice.getGuestCccd());
            ps.setDouble(4, invoice.getTotalAmount());
            ps.setString(5, invoice.getPaymentMethod());            
            return ps.executeUpdate() > 0;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public double getRevenueByShift(String employeeId, Date shiftTime) {
        //thong ke doanh thu theo ca, dung ham SUM de tinh tong tien ca cua nhan vien
        String sql = "SELECT SUM(total_amount) FROM invoices WHERE staff_id = ? AND DATE(payment_date) = CURDATE()";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {            
            ps.setString(1, employeeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }   

    @Override public Invoice findActiveInvoiceByRoom(String roomId) { 
        return null; 
    }
    @Override public List<Invoice> getInvoicesByPeriod(Date start, Date end) { 
        return null; 
    }

} /*

 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license

 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

 */

