package com.hotel.service.impl;

import com.hotel.dao.IRoomDAO;
import com.hotel.dao.impl.RoomDAOImpl;
import com.hotel.model.Guest;
import com.hotel.model.Invoice;
import com.hotel.model.Service;
import com.hotel.service.IStayService;
import com.hotel.dao.impl.InvoiceDAOImpl;
import java.time.LocalDateTime;
import com.hotel.dao.IInvoiceDAO;
import java.util.UUID;
import java.io.PrintWriter;
public class StayServiceImpl implements IStayService {

    private IRoomDAO roomDAO = new RoomDAOImpl();
    private IInvoiceDAO invoiceDAO = new InvoiceDAOImpl();

    @Override
    public boolean checkIn(String roomId, Guest guest, Invoice bookingDetails) {
        // Lưu thông tin khách và hình thức thuê, tự động lấy giờ từ hệ thống [cite: 3, 4]
        try {
            // tự động giờ checkin
            bookingDetails.setCheckInTime(
                    LocalDateTime.now());

            bookingDetails.setRoomId(roomId);
            bookingDetails.setStatus("CHECKED_IN");

            // TODO:
            // guestDAO.save(guest);
            // invoiceDAO.create(bookingDetails);
            roomDAO.updateStatus(roomId, "Có khách");

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        return false;
        }
    }

    @Override
    public boolean updateStayInformation(String roomId, Guest updatedGuest) {
        // TODO: Chỉnh sửa hoặc thêm thông tin khách hàng/phòng thuê sau khi đã đặt [cite: 7]

        // TODO:
        // guestDAO.update(updatedGuest);

        return true;
    }

    @Override
    public void manageRoomServices(String roomId, Service service, int quantity, boolean isAddition) {
        // TODO: Thêm hoặc xóa dịch vụ cho phòng đang có khách [cite: 5, 6]
         if (isAddition) {
            // serviceUsageDAO.add(...)
        } else {
            // serviceUsageDAO.remove(...)
        }
    }

    @Override
    public Invoice processCheckOut(String roomId) {
        // TODO: Tính tiền thực tế, phụ thu sớm/muộn, áp dụng giảm giá và đổi trạng thái phòng [cite: 8-12]
         Invoice invoice = new Invoice();

        invoice.setInvoiceId(
                UUID.randomUUID().toString());

        invoice.setCheckOutTime(
                LocalDateTime.now());

        // ===== TÍNH TIỀN =====
        double roomCost = 500000; // demo
        double serviceCost = 100000;

        invoice.setTotalAmount(
                roomCost + serviceCost);

        invoice.setStatus("PAID");

        // TODO:
        // invoiceDAO.update(invoice);
        roomDAO.updateStatus(roomId, "Đang dọn");
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
}