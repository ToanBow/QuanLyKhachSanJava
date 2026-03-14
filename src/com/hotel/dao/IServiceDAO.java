package com.hotel.dao;

import com.hotel.model.Service;
import java.util.List;

public interface IServiceDAO {
    // Lấy danh sách các dịch vụ hiện có (Mini bar, Giặt là...) 
    List<Service> getAllServices();

    // Tìm dịch vụ theo ID để tính giá hoặc lấy thuế suất VAT
    Service findById(String serviceId);

    // Tự động trừ hoặc cộng kho khi dịch vụ được sử dụng hoặc nhập thêm
    boolean updateInventory(String serviceId, int quantityChange);
}