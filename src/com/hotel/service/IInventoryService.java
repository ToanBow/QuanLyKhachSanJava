package com.hotel.service;

import com.hotel.model.Service;

public interface IInventoryService {
    /**
     * [cite_start]Theo dõi số lượng tồn kho (nước suối, mì gói, bàn chải...) và tự động trừ kho khi sử dụng. [cite: 22, 23]
     */
    void updateStock(String serviceId, int quantityChange);

    /**
     * [cite_start]Định giá dịch vụ: Thiết lập giá bán, đơn vị tính và thuế suất VAT. [cite: 24]
     */
    void configureService(Service service);
}