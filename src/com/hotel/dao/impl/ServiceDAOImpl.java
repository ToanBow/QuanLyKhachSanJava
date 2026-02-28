/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hotel.dao.impl;

import com.hotel.dao.IServiceDAO;
import com.hotel.model.Service;
import com.hotel.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author DELL
 */
public class ServiceDAOImpl implements IServiceDAO{
    @Override
    public boolean updateInventory(String serviceId, int quantityChange) {
        //tu doong tru kho khi su dung
        String sql = "UPDATE services SET inventory = inventory + ? WHERE service_id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantityChange);
            ps.setString(2, serviceId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    @Override
    public Service findById(String serviceId) {
        //tim gia va thue de tinh hoa don
        String sql = "SELECT * FROM services WHERE service_id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, serviceId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Service s = new Service();
                s.setServiceId(rs.getString("service_id"));
                s.setName(rs.getString("name"));
                s.setPrice(rs.getDouble("price"));
                s.setVatRate(rs.getDouble("vat_rate"));
                return s;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public List<Service> getAllServices(){ 
        List<Service> list = new ArrayList<>();
        String sql = "SELECT * FROM services";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                Service s = new Service();
                s.setServiceId(rs.getString("service_id"));
                s.setName(rs.getString("name"));
                s.setCategory(rs.getString("category")); // phan loai: giat la,...
                s.setPrice(rs.getDouble("price"));
                s.setInventory(rs.getInt("inventory"));
                s.setVatRate(rs.getDouble("vat_rate"));
                list.add(s);
            }
        } catch (SQLException e){ 
            e.printStackTrace(); 
        }
        return new ArrayList<>(); 
    }
}
