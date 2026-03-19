DROP DATABASE IF EXISTS hotel_management;
CREATE DATABASE hotel_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hotel_management;

-- 1. Bảng Người dùng
CREATE TABLE users (
    email VARCHAR(100) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    role ENUM('Quản lý', 'Lễ tân', 'Kế toán', 'Buồng phòng') NOT NULL
);

-- 2. Bảng Phòng
CREATE TABLE rooms (
    room_id VARCHAR(10) PRIMARY KEY,
    floor INT NOT NULL,
    type VARCHAR(50),
    beds INT,
    daily_price DOUBLE,
    hourly_price DOUBLE,
    status ENUM('Sẵn sàng', 'Có khách', 'Chưa dọn', 'Đang sửa chữa') DEFAULT 'Sẵn sàng'
);

-- 3. Bảng Khách hàng
CREATE TABLE guests (
    cccd VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(15),
    gender VARCHAR(10),
    birth_date DATE,
    home_town VARCHAR(255),
    email VARCHAR(100),
    nationality VARCHAR(50),
    `rank` ENUM('Bạc', 'Vàng', 'Kim cương') DEFAULT 'Bạc'
);

-- 4. Bảng Dịch vụ
CREATE TABLE services (
    service_id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100),
    category VARCHAR(50),
    inventory INT DEFAULT 0,
    price DOUBLE,
    unit VARCHAR(20),
    vat_rate DOUBLE DEFAULT 0.1
);

-- 5. Bảng Hóa đơn
CREATE TABLE invoices (
    invoice_id VARCHAR(50) PRIMARY KEY,    
    room_id VARCHAR(10),
    guest_cccd VARCHAR(20),
    check_in_time DATETIME,
    check_out_time DATETIME,
    rental_type ENUM('Theo ngày', 'Theo giờ'),
    deposit DOUBLE DEFAULT 0,
    early_surcharge DOUBLE DEFAULT 0,
    late_surcharge DOUBLE DEFAULT 0,
    discount DOUBLE DEFAULT 0,
    payment_method ENUM('Tiền mặt', 'Chuyển khoản', 'Thẻ tín dụng', 'Nợ'),
    total_amount DOUBLE,
    staff_id VARCHAR(100), 
    payment_date DATETIME, 
    FOREIGN KEY (staff_id) REFERENCES users(email),
    FOREIGN KEY (room_id) REFERENCES rooms(room_id),
    FOREIGN KEY (guest_cccd) REFERENCES guests(cccd)
);

-- 6. Bảng Chi tiết sử dụng dịch vụ
CREATE TABLE service_usage (
    usage_id INT AUTO_INCREMENT PRIMARY KEY,
    invoice_id VARCHAR(50),           
    service_id VARCHAR(10),
    quantity INT,
    price_at_time DOUBLE,
    FOREIGN KEY (invoice_id) REFERENCES invoices(invoice_id),
    FOREIGN KEY (service_id) REFERENCES services(service_id)
);

-- 7. Bảng Cấu hình hệ thống 
CREATE TABLE system_settings (
    setting_key VARCHAR(100) PRIMARY KEY, 
    setting_value TEXT NOT NULL            
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. Bảng Theo dõi công nợ đại lý
CREATE TABLE agency_debts (
    agency_id VARCHAR(50) PRIMARY KEY,
    debt_amount DOUBLE DEFAULT 0,
    record_date DATETIME
);
 
--  9. Bảng blacklists
CREATE TABLE blacklists (
    cccd VARCHAR(20) PRIMARY KEY,
    reason VARCHAR(255) NOT NULL,
    record_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cccd) REFERENCES guests(cccd)
);