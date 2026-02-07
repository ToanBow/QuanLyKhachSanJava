package com.hotel.service.impl;

import com.hotel.model.Service;
import com.hotel.service.IInventoryService;

public class InventoryServiceImpl implements IInventoryService {
    @Override
    public void updateStock(String serviceId, int quantityChange) {
        // TODO: Theo dõi số lượng tồn kho và tự động trừ kho khi sử dụng [cite: 22, 23]
    }

    @Override
    public void configureService(Service service) {
        // TODO: Thiết lập giá bán, đơn vị tính và thuế suất VAT [cite: 24]
    }
}