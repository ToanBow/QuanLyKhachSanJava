package com.hotel.service;

import com.hotel.model.Service;

public interface IInventoryService {
    /**
    Theo dõi số lượng tồn kho (nước suối, mì gói, bàn chải...) và tự động trừ kho khi sử dụng. 
     */
    void updateStock(String serviceId, int quantityChange);

    /**
    Định giá dịch vụ: Thiết lập giá bán, đơn vị tính và thuế suất VAT.
     */
    void configureService(Service service);
}