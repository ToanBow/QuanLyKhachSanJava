package com.hotel.dao;

import com.hotel.model.Service;
import java.util.List;

public interface IServiceDAO {
    
    // Lấy danh sách toàn bộ dịch vụ
    List<Service> getAllServices();
    
    // Tìm dịch vụ theo ID
    Service findById(String serviceId);
    
    // Hàm cập nhật tồn kho 
    boolean updateInventory(String serviceId, int quantityChange);
    
}