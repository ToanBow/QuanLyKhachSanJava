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
import com.hotel.model.ServiceUsage;
import com.hotel.service.IStayService;
import com.hotel.util.DBConnection;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
public class StayServiceImpl implements IStayService {

    private IRoomDAO roomDAO = new RoomDAOImpl();
    private IInvoiceDAO invoiceDAO = new InvoiceDAOImpl();
    private IGuestDAO guestDAO = new GuestDAOImpl();

    @Override
    public boolean checkIn(String roomId, Guest guest, Invoice bookingDetails) {
        try {
            // 1. Kiểm tra phòng có tồn tại không
            Room room = roomDAO.findByRoomId(roomId);
            if (room == null || !"Sẵn sàng".equals(room.getStatus())) {
                System.out.println("Lỗi: Phòng không tồn tại hoặc chưa dọn xong!");
                return false;
            }

            // 2. Mở comment và kích hoạt DAO cho Khách Hàng
            // Nếu khách hàng chưa từng lưu trú, tiến hành lưu mới
            Guest existingGuest = guestDAO.findByCccd(guest.getCccd());
            if (existingGuest == null) {
                guestDAO.insert(guest);
            }

            // 3. Mở comment và kích hoạt DAO cho Hóa Đơn (Invoice)
            bookingDetails.setInvoiceId(UUID.randomUUID().toString());
            bookingDetails.setRoomId(roomId);
            bookingDetails.setGuestCccd(guest.getCccd());
            bookingDetails.setCheckInTime(LocalDateTime.now());
            bookingDetails.setStatus("CHECKED_IN");
            
            invoiceDAO.insert(bookingDetails);

            // 4. Cập nhật trạng thái của phòng thành "Có khách"
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
        // 1. Kiểm tra phòng có đang được thuê (có hóa đơn active) hay không
        Invoice activeInvoice = invoiceDAO.findActiveInvoiceByRoom(roomId);
        if (activeInvoice == null) {
            System.err.println("Lỗi: Phòng " + roomId + " hiện không có hóa đơn hoạt động (Chưa có khách).");
            return false;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            // 2. Cập nhật hoặc Thêm mới thông tin khách hàng (UPSERT)
            // Nếu khách đã có (trùng CCCD) thì cập nhật, nếu chưa có thì thêm mới
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

            // 3. Xử lý đổi người đại diện thuê phòng
            // Nếu lễ tân chỉnh sửa/cập nhật bằng một khách có CCCD khác với lúc Check-in
            if (!activeInvoice.getGuestCccd().equals(updatedGuest.getCccd())) {
                String sqlUpdateInvoice = "UPDATE invoices SET guest_cccd = ? WHERE invoice_id = ?";
                try (PreparedStatement psInvoice = conn.prepareStatement(sqlUpdateInvoice)) {
                    psInvoice.setString(1, updatedGuest.getCccd());
                    psInvoice.setString(2, activeInvoice.getInvoiceId());
                    psInvoice.executeUpdate();
                }
                System.out.println("Đã chuyển quyền đại diện phòng " + roomId + " cho khách có CCCD: " + updatedGuest.getCccd());
            }

            conn.commit(); // Hoàn tất Transaction
            System.out.println("Cập nhật thông tin lưu trú thành công cho phòng " + roomId);
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Giao dịch cập nhật thông tin thất bại, đã Rollback dữ liệu!");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public void manageRoomServices(String roomId, Service service, int quantity, boolean isAddition) {
        // Tìm hóa đơn đang hoạt động của phòng này
        Invoice activeInvoice = invoiceDAO.findActiveInvoiceByRoom(roomId);
        if (activeInvoice == null) {
            System.out.println("Lỗi: Phòng " + roomId + " hiện không có khách (không tìm thấy hóa đơn hoạt động).");
            return;
        }

        String sql = "INSERT INTO service_usage (invoice_id, service_id, quantity, price_at_time) VALUES (?, ?, ?, ?)";
        if (!isAddition) {
            // Xóa/Giảm dịch vụ (Giả định xóa đi các record vừa thêm nhầm)
            sql = "DELETE FROM service_usage WHERE invoice_id = ? AND service_id = ? LIMIT ?";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, activeInvoice.getInvoiceId());
            ps.setString(2, service.getServiceId());
            ps.setInt(3, quantity);

            if (isAddition) {
                ps.setDouble(4, service.getPrice()); // Lưu lại giá tại thời điểm gọi dịch vụ
            }
            
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Đã " + (isAddition ? "thêm" : "xóa") + " dịch vụ " + service.getName() + " cho phòng " + roomId);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public Invoice processCheckOut(String roomId) {
        // 1. Lấy thông tin hóa đơn đang Active (chưa check-out)
        Invoice invoice = invoiceDAO.findActiveInvoiceByRoom(roomId);
        if (invoice == null) {
            System.err.println("Lỗi: Không tìm thấy hóa đơn đang hoạt động cho phòng " + roomId);
            return null;
        }

        // 2. Lấy thông tin phòng để đối chiếu giá
        Room room = roomDAO.findByRoomId(roomId);
        if (room == null) {
            System.err.println("Lỗi: Không tìm thấy thông tin phòng " + roomId);
            return null;
        }

        // 3. Chốt giờ Check-out
        invoice.setCheckOutTime(LocalDateTime.now());

        // 4. Tính toán thời gian lưu trú
        Duration duration = Duration.between(invoice.getCheckInTime(), invoice.getCheckOutTime());
        long totalMinutes = duration.toMinutes();
        long hours = totalMinutes / 60;
        
        // Khách sạn thường có biên độ du di (VD: Lố 15 phút tính tròn 1 giờ)
        if (totalMinutes % 60 > 15) {
            hours++;
        }
        if (hours == 0) hours = 1; // Mặc định ở tối thiểu 1 giờ

        // 5. Tính giá tiền phòng linh hoạt (roomCost)
        double roomCost = 0;
        if ("Theo ngày".equalsIgnoreCase(invoice.getRentalType())) {
            long days = hours / 24;
            // Nếu ở qua số giờ quy định của ngày tiếp theo (VD: lố 4 giờ) thì làm tròn thành thêm 1 ngày
            if (hours % 24 >= 4) {
                days++;
            }
            if (days == 0) days = 1; // Tối thiểu 1 ngày
            roomCost = days * room.getDailyPrice();
        } else {
            // Tính theo giờ
            roomCost = hours * room.getHourlyPrice();
        }

        // 6. Gọi hàm tính toán tổng tiền có sẵn trong model Invoice
        // Hàm này sẽ tự động cộng roomCost, early/late Surcharge, trừ Deposit và Discount
        invoice.calculateActualAmount(roomCost);
        
        invoice.setStatus("PAID");
        // Giả định phương thức thanh toán, thực tế có thể truyền qua tham số hàm
        if (invoice.getPaymentMethod() == null) {
            invoice.setPaymentMethod("Tiền mặt"); 
        }

        // 7. Thực hiện Database Transaction để đảm bảo tính ACID
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Cập nhật Hóa Đơn (Đã có invoice_id từ lúc check-in)
            String sqlUpdateInvoice = "UPDATE invoices SET check_out_time = ?, total_amount = ?, status = ?, payment_method = ? WHERE invoice_id = ?";
            try (PreparedStatement psInvoice = conn.prepareStatement(sqlUpdateInvoice)) {
                psInvoice.setTimestamp(1, java.sql.Timestamp.valueOf(invoice.getCheckOutTime()));
                psInvoice.setDouble(2, invoice.getTotalAmount());
                psInvoice.setString(3, invoice.getStatus());
                psInvoice.setString(4, invoice.getPaymentMethod());
                psInvoice.setString(5, invoice.getInvoiceId());
                psInvoice.executeUpdate();
            }

            // Đổi trạng thái Phòng sang "Chưa dọn"
            String sqlUpdateRoom = "UPDATE rooms SET status = 'Chưa dọn' WHERE room_id = ?";
            try (PreparedStatement psRoom = conn.prepareStatement(sqlUpdateRoom)) {
                psRoom.setString(1, roomId);
                psRoom.executeUpdate();
            }

            // Trừ kho Dịch vụ nếu có sử dụng
            if (invoice.getServices() != null && !invoice.getServices().isEmpty()) {
                String sqlUpdateInventory = "UPDATE services SET inventory = inventory - ? WHERE service_id = ?";
                try (PreparedStatement psInventory = conn.prepareStatement(sqlUpdateInventory)) {
                    for (ServiceUsage usage : invoice.getServices()) {
                        psInventory.setInt(1, usage.getQuantity());
                        psInventory.setString(2, usage.getServiceId());
                        psInventory.addBatch();
                    }
                    psInventory.executeBatch();
                }
            }

            conn.commit();
            System.out.println("Check-out thành công. Hóa đơn cập nhật hoàn tất!");

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Giao dịch Check-out thất bại, đã Rollback dữ liệu!");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return null;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return invoice;
    }

    @Override
    
        // TODO: Xuất và in hóa đơn chi tiết [cite: 12]
    public void printInvoice(String invoiceId) {

        // TODO:
        // load invoice + services
        // export PDF / print
       Invoice invoice = invoiceDAO.findById(invoiceId);

    if (invoice == null) {
        System.out.println("Invoice not found!");
        return;
    }

    System.out.println("\n===== HOTEL INVOICE =====");
    System.out.println("Invoice ID : " + invoice.getInvoiceId());
    System.out.println("Room ID    : " + invoice.getRoomId());
    System.out.println("Check In   : " + invoice.getCheckInTime());
    System.out.println("Check Out  : " + invoice.getCheckOutTime());
    System.out.println("Total      : " + invoice.getTotalAmount());
    System.out.println("Status     : " + invoice.getStatus());
    System.out.println("==========================");

    // ===== EXPORT FILE TXT =====
    try (PrintWriter writer =
            new PrintWriter("invoice_" + invoiceId + ".txt")) {

        writer.println("===== HOTEL INVOICE =====");
        writer.println("Invoice ID : " + invoice.getInvoiceId());
        writer.println("Room ID    : " + invoice.getRoomId());
        writer.println("Check In   : " + invoice.getCheckInTime());
        writer.println("Check Out  : " + invoice.getCheckOutTime());
        writer.println("Total      : " + invoice.getTotalAmount());
        writer.println("Status     : " + invoice.getStatus());
        writer.println("==========================");

        System.out.println("Invoice exported successfully!");

    } catch (Exception e) {
        e.printStackTrace();
        }
    }

    public void checkIn(String guestId, String roomId) {

    }
}