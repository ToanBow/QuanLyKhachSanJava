package com.hotel.view.setting;

import com.hotel.model.Room;
import com.hotel.service.impl.RoomServiceImpl;
import com.hotel.util.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public class SettingPanel extends JPanel {

    private RoomServiceImpl roomService;
    private JTable table;
    private DefaultTableModel model;

    public SettingPanel(RoomServiceImpl roomService) {
        this.roomService = roomService;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Cấu hình Cơ sở hạ tầng (Quản lý Phòng)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"Mã Phòng", "Tầng", "Loại Phòng", "Giá Ngày", "Giá Giờ", "Trạng thái"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(30);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setOpaque(false);
        JButton btnAdd = new JButton("Thêm Phòng Mới");
        JButton btnDelete = new JButton("Xóa Phòng");
        actionPanel.add(btnAdd);
        actionPanel.add(btnDelete);
        add(actionPanel, BorderLayout.SOUTH);

        loadRoomData();

        btnAdd.addActionListener(e -> insertNewRoom());
        btnDelete.addActionListener(e -> deleteSelectedRoom());
    }

    private void loadRoomData() {
        model.setRowCount(0);
        List<Room> rooms = roomService.getRoomMap();
        for (Room r : rooms) {
            model.addRow(new Object[]{r.getRoomId(), r.getFloor(), r.getType(), r.getDailyPrice(), r.getHourlyPrice(), r.getStatus()});
        }
    }

    private void insertNewRoom() {
        JTextField txtId = new JTextField();
        JTextField txtFloor = new JTextField();
        JTextField txtType = new JTextField();
        JTextField txtDaily = new JTextField("500000");
        JTextField txtHourly = new JTextField("100000");
        
        Object[] fields = {
            "Mã Phòng (VD: P401):", txtId, "Tầng:", txtFloor, 
            "Loại phòng:", txtType, "Giá theo ngày:", txtDaily, "Giá theo giờ:", txtHourly
        };
        
        if (JOptionPane.showConfirmDialog(this, fields, "Thêm phòng mới", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            String sql = "INSERT INTO rooms (room_id, floor, type, daily_price, hourly_price, status) VALUES (?, ?, ?, ?, ?, 'Sẵn sàng')";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, txtId.getText().trim());
                ps.setInt(2, Integer.parseInt(txtFloor.getText().trim()));
                ps.setString(3, txtType.getText().trim());
                ps.setDouble(4, Double.parseDouble(txtDaily.getText().trim()));
                ps.setDouble(5, Double.parseDouble(txtHourly.getText().trim()));
                ps.executeUpdate();
                loadRoomData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedRoom() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        String roomId = String.valueOf(model.getValueAt(row, 0));
        
        if (JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa phòng " + roomId + "?", "Cảnh báo", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM rooms WHERE room_id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, roomId);
                ps.executeUpdate();
                loadRoomData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Không thể xóa phòng đang có ràng buộc dữ liệu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}