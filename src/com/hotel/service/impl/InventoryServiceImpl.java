package com.hotel.service.impl;

import com.hotel.model.Service;
import com.hotel.service.IInventoryService;
import com.hotel.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class InventoryServiceImpl implements IInventoryService {
    @Override
    public void updateStock(String serviceId, int quantityChange) {
        // TODO: Theo dõi số lượng tồn kho và tự động trừ kho khi sử dụng [cite: 22, 23]
        //mo ket noi qua DNConnection
        //tu dong tru ton kho khi su dung

        String sql = "UPDATE services SET inventory = inventory + ? WHERE service_id = ?";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            
            ps.setInt(1, quantityChange);
            ps.setString(2, serviceId);
            int rowsAffected = ps.executeUpdate();
            if(rowsAffected > 0){
                System.out.println("Đã trừ kho thành công cho dịch vụ "+ serviceId);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void configureService(Service service) {
        // TODO: Thiết lập giá bán, đơn vị tính và thuế suất VAT [cite: 24]
        System.out.println("--- CẤU HÌNH DỊCH VỤ ---");
        System.out.println("Tên dịch vụ: "+ service.getName());
        System.out.println("Đơn giá: "+ service.getPrice());
        System.out.println("Đơn vị tính: "+ service.getUnit());
        System.out.println("Thuế suất (vatRate): "+ service.getVatRate()); 
        System.out.println("------------------------");
    } 
}