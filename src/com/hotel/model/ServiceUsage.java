package com.hotel.model;

/**
 * Lớp đại diện cho việc sử dụng dịch vụ của khách hàng, 
 * tương ứng với bảng service_usage trong cơ sở dữ liệu.
 */
public class ServiceUsage {
    private int usageId;          // usage_id INT AUTO_INCREMENT PRIMARY KEY
    private String invoiceId;     // invoice_id VARCHAR(20)
    private String serviceId;     // service_id VARCHAR(10)
    private int quantity;         // quantity INT
    private double priceAtTime;   // price_at_time DOUBLE (Giá tại thời điểm sử dụng)

    // Constructor không tham số
    public ServiceUsage() {}

    // Constructor đầy đủ tham số
    public ServiceUsage(int usageId, String invoiceId, String serviceId, int quantity, double priceAtTime) {
        this.usageId = usageId;
        this.invoiceId = invoiceId;
        this.serviceId = serviceId;
        this.quantity = quantity;
        this.priceAtTime = priceAtTime;
    }

    // Getters và Setters
    public int getUsageId() {
        return usageId;
    }

    public void setUsageId(int usageId) {
        this.usageId = usageId;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPriceAtTime() {
        return priceAtTime;
    }

    public void setPriceAtTime(double priceAtTime) {
        this.priceAtTime = priceAtTime;
    }

    /**
     * Phương thức bổ trợ để tính tổng tiền của dịch vụ này
     */
    public double getSubTotal() {
        return this.quantity * this.priceAtTime;
    }
}