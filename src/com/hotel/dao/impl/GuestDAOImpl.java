package com.hotel.dao.impl;

import com.hotel.dao.IGuestDAO;
import com.hotel.model.Guest;
import com.hotel.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class GuestDAOImpl implements IGuestDAO {

    @Override
    public boolean insert(Guest guest) {
        String sql = "INSERT INTO guests (cccd, name, phone, email, gender, birth_date, home_town, nationality) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, guest.getCccd());
            ps.setString(2, guest.getName());
            ps.setString(3, guest.getPhone());
            ps.setString(4, guest.getEmail());
            ps.setString(5, guest.getGender());
            if (guest.getBirthDate() != null) ps.setDate(6, new java.sql.Date(guest.getBirthDate().getTime()));
            else ps.setNull(6, java.sql.Types.DATE);
            ps.setString(7, guest.getHomeTown());
            ps.setString(8, guest.getNationality());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean update(Guest guest) {
        String sql = "UPDATE guests SET name=?, phone=?, email=?, gender=?, birth_date=?, home_town=?, nationality=? WHERE cccd=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, guest.getName());
            ps.setString(2, guest.getPhone());
            ps.setString(3, guest.getEmail());
            ps.setString(4, guest.getGender());
            if (guest.getBirthDate() != null) ps.setDate(5, new java.sql.Date(guest.getBirthDate().getTime()));
            else ps.setNull(5, java.sql.Types.DATE);
            ps.setString(6, guest.getHomeTown());
            ps.setString(7, guest.getNationality());
            ps.setString(8, guest.getCccd());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    @Override
    public Guest findByCccd(String cccd) {
        String sql = "SELECT * FROM guests WHERE cccd = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cccd);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Guest g = new Guest();
                    g.setCccd(rs.getString("cccd"));
                    g.setName(rs.getString("name"));
                    g.setPhone(rs.getString("phone"));
                    g.setEmail(rs.getString("email"));
                    g.setGender(rs.getString("gender"));
                    g.setBirthDate(rs.getDate("birth_date"));
                    g.setHomeTown(rs.getString("home_town"));
                    g.setNationality(rs.getString("nationality"));
                    g.setRank(rs.getString("rank"));
                    return g;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public List<Guest> findAll() {
        List<Guest> list = new ArrayList<>();
        String sql = "SELECT * FROM guests";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Guest g = new Guest();
                g.setCccd(rs.getString("cccd"));
                g.setName(rs.getString("name"));
                g.setPhone(rs.getString("phone"));
                g.setEmail(rs.getString("email"));
                g.setGender(rs.getString("gender"));
                g.setBirthDate(rs.getDate("birth_date"));
                g.setHomeTown(rs.getString("home_town"));
                g.setNationality(rs.getString("nationality"));
                g.setRank(rs.getString("rank"));
                list.add(g);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public boolean updateRank(String cccd, String newRank) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateRank'");
    }

    @Override
    public List<Guest> getAllCustomers() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllCustomers'");
    }
}