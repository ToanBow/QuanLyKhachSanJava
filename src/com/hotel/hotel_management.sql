CREATE DATABASE hotel_management;
USE hotel_management;

-- 1. Bảng Người dùng (Phân quyền RBAC)
CREATE TABLE users (
    email VARCHAR(100) PRIMARY KEY,
    password VARCHAR(255) NOT NULL, -- Mật khẩu nên được mã hóa
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

-- 3. Bảng Khách hàng (CRM)
CREATE TABLE guests (
    cccd VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(15),
    gender VARCHAR(10),
    birth_date DATE,
    home_town VARCHAR(255),
    email VARCHAR(100),
    nationality VARCHAR(50),
    rank ENUM('Bạc', 'Vàng', 'Kim cương') DEFAULT 'Bạc'
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
    invoice_id VARCHAR(20) PRIMARY KEY,
    room_id VARCHAR(10),
    guest_cccd VARCHAR(20),
    check_in_time DATETIME,
    check_out_time DATETIME,
    rental_type ENUM('Theo ngày', 'Theo giờ'),
    deposit DOUBLE DEFAULT 0,
    early_surcharge DOUBLE DEFAULT 0,
    late_surcharge DOUBLE DEFAULT 0,
    discount DOUBLE DEFAULT 0,
    payment_method ENUM('Tiền mặt', 'Tín dụng', 'Nợ'),
    total_amount DOUBLE,
    FOREIGN KEY (room_id) REFERENCES rooms(room_id),
    FOREIGN KEY (guest_cccd) REFERENCES guests(cccd)
);

-- 6. Bảng Chi tiết sử dụng dịch vụ (Quan hệ n-n giữa Invoice và Service)
CREATE TABLE service_usage (
    usage_id INT AUTO_INCREMENT PRIMARY KEY,
    invoice_id VARCHAR(20),
    service_id VARCHAR(10),
    quantity INT,
    price_at_time DOUBLE,
    FOREIGN KEY (invoice_id) REFERENCES invoices(invoice_id),
    FOREIGN KEY (service_id) REFERENCES services(service_id)
);