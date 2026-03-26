package com.hotel.view.room;

import com.hotel.model.Room;
import com.hotel.service.IRoomService;
import com.hotel.service.IStayService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RoomMapPanel extends JPanel {

    private JPanel floorsPanel;
    private JComboBox<String> filterBox;
    private List<RoomCard> allRooms = new ArrayList<>();

    // Tích hợp Service
    private IRoomService roomService;
    private IStayService stayService;

    public RoomMapPanel(IRoomService roomService, IStayService stayService) {
        this.roomService = roomService;
        this.stayService = stayService;

        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(15, 20, 10, 20));

        JLabel title = new JLabel("Sơ đồ phòng");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        // THÊM NÚT LÀM MỚI VÀ BỘ LỌC
        JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightControls.setOpaque(false);

        // Nút Làm mới
        JButton btnReload = new JButton("Làm mới");
        btnReload.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnReload.setBackground(new Color(238, 238, 238));
        btnReload.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReload.addActionListener(e -> loadRoomsData());

        // Cập nhật bộ lọc theo đúng các trạng thái trong CSDL
        String[] filters = {"Tất cả", "Sẵn sàng", "Có khách", "Chưa dọn", "Đang sửa chữa"};
        filterBox = new JComboBox<>(filters);
        filterBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        filterBox.addActionListener(e -> applyFilter());

        rightControls.add(btnReload);
        rightControls.add(filterBox);

        header.add(title, BorderLayout.WEST);
        header.add(rightControls, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        floorsPanel = new JPanel();
        floorsPanel.setLayout(new BoxLayout(floorsPanel, BoxLayout.Y_AXIS));
        floorsPanel.setBackground(new Color(245, 247, 250));
        floorsPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        JScrollPane scroll = new JScrollPane(floorsPanel);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        // Khởi chạy nạp dữ liệu từ CSDL
        loadRoomsData();
    }

    public void loadRoomsData() {
        floorsPanel.removeAll();
        allRooms.clear();

        // 1. Lấy dữ liệu thật từ DB thông qua Service
        List<Room> rooms = roomService.getRoomMap(); 
        if (rooms == null || rooms.isEmpty()) {
            floorsPanel.add(new JLabel("Chưa có dữ liệu phòng trong cơ sở dữ liệu."));
            revalidate();
            repaint();
            return;
        }

        // 2. Nhóm các phòng theo tầng (Sử dụng TreeMap để tự động sắp xếp Tầng 1 -> Tầng N)
        Map<Integer, List<Room>> roomsByFloor = new TreeMap<>();
        for (Room r : rooms) {
            roomsByFloor.computeIfAbsent(r.getFloor(), k -> new ArrayList<>()).add(r);
        }

        // 3. Render từng tầng ra giao diện
        for (Map.Entry<Integer, List<Room>> entry : roomsByFloor.entrySet()) {
            floorsPanel.add(createFloor("Tầng " + entry.getKey(), entry.getValue()));
        }

        // Áp dụng lại bộ lọc hiện tại sau khi reload để giao diện không bị giật
        applyFilter();
    }

    private JPanel createFloor(String floorName, List<Room> roomsInFloor) {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.setBorder(new EmptyBorder(10, 0, 25, 0));

        JLabel label = new JLabel(floorName);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        container.add(label, BorderLayout.NORTH);

        // Lưới hiển thị linh hoạt, tự động xuống dòng nếu vượt quá 5 phòng
        JPanel grid = new JPanel(new GridLayout(0, 5, 20, 20));
        grid.setOpaque(false);

        for (Room r : roomsInFloor) {
            // Nạp dữ liệu thực vào RoomCard
            RoomCard card = new RoomCard(
                    "Phòng " + r.getRoomId(),
                    r.getType(),
                    mapStatus(r.getStatus()) 
            );

            // Thêm sự kiện Click để thực hiện Check-in / Check-out
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(RoomMapPanel.this);
                    
                    // Mở Dialog chi tiết phòng (Truyền StayService để gọi logic Check-in/out)
                    RoomDetailDialog dialog = new RoomDetailDialog(parentFrame, r, stayService);
                    dialog.setVisible(true);
                    
                    // Tải lại sơ đồ phòng sau khi đóng Dialog để cập nhật trạng thái mới nhất
                    loadRoomsData();
                }
            });

            allRooms.add(card);
            grid.add(card);
        }

        container.add(grid, BorderLayout.CENTER);
        return container;
    }

    // Ánh xạ trạng thái chuỗi trong MySQL sang Enum của giao diện
    private RoomStatus mapStatus(String dbStatus) {
        if (dbStatus == null) return RoomStatus.AVAILABLE; 
        switch (dbStatus) {
            case "Sẵn sàng": return RoomStatus.AVAILABLE;
            case "Có khách": return RoomStatus.OCCUPIED;
            case "Chưa dọn": return RoomStatus.CLEANING;
            default: return RoomStatus.AVAILABLE;
        }
    }

    private void applyFilter() {
        String selected = (String) filterBox.getSelectedItem();

        for (RoomCard card : allRooms) {
            if ("Tất cả".equals(selected)) {
                card.setVisible(true);
                continue;
            }

            if (card.getStatus().getText().equalsIgnoreCase(selected)) {
                card.setVisible(true);
            } else {
                card.setVisible(false);
            }
        }
        revalidate();
        repaint();
    }
}