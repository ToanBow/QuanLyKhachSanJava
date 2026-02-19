Hotel Management System (HMS)
Một giải pháp quản lý khách sạn toàn diện được xây dựng trên nền tảng Java Core, tối ưu hóa quy trình vận hành và quản lý tài chính.

📝 Giới thiệu
Hotel Management System (HMS) là hệ thống quản lý khách sạn cấp doanh nghiệp, được thiết kế để đơn giản hóa các hoạt động phức tạp từ tiếp tân, buồng phòng đến kế toán và quản lý cấp cao. Dự án tập trung vào tính chính xác của dữ liệu, phân quyền chặt chẽ và khả năng mở rộng linh hoạt.

Hệ thống cung cấp cái nhìn toàn diện về tình thái lưu trú, quản lý kho dịch vụ tự động và hệ thống báo cáo doanh thu chi tiết, giúp tối ưu hóa hiệu suất vận hành khách sạn.

✨ Tính năng chính
🔐 Quản lý người dùng & Phân quyền (RBAC)
Xác thực bảo mật: Đăng ký và đăng nhập với kiểm tra định dạng email và độ phức tạp mật khẩu.

Phân quyền chi tiết: Hệ thống phân quyền dựa trên vai trò (Role-Based Access Control) cho Quản lý, Lễ tân, Kế toán và Buồng phòng.

Nhật ký hoạt động: Ghi lại lịch sử hệ thống (Audit Log) khi người dùng đăng xuất.

🏨 Quản lý lưu trú & Phòng
Sơ đồ phòng động: Hiển thị danh sách phòng dưới dạng lưới với màu sắc phân biệt trạng thái: Sẵn sàng, Có khách, Chưa dọn, Đang sửa chữa.

Quy trình Check-in/Check-out: Tự động lấy dữ liệu thời gian hệ thống, nhập thông tin định danh khách hàng và hình thức thuê (theo giờ/ngày).

Chính sách giá linh hoạt: Thiết lập giá theo mùa, theo loại phòng hoặc theo khung giờ đặc biệt.

🛍️ Dịch vụ & Kho lưu trữ
Quản lý dịch vụ: Theo dõi việc sử dụng dịch vụ (Mini bar, Giặt là, Spa...) và tự động trừ tồn kho khi phát sinh giao dịch.

Định giá thông minh: Cấu hình giá bán, đơn vị tính và thuế suất VAT riêng biệt cho từng loại dịch vụ.

📊 Tài chính & Báo cáo
Hóa đơn tự động: Tính toán tổng tiền dựa trên giá phòng, phụ thu sớm/muộn, chi phí dịch vụ và các chương trình giảm giá.

Báo cáo thông minh: Thống kê doanh thu thuần, tỷ lệ lấp đầy phòng và gửi báo cáo trực tiếp qua Email cho nhà quản lý.

🏗️ Kiến trúc tổng quan
Dự án được xây dựng theo kiến trúc phân lớp (Layered Architecture) nhằm tách biệt rõ ràng giữa logic xử lý dữ liệu và logic nghiệp vụ.

Đoạn mã
graph TD
    UI[Giao diện người dùng] --> Service[Service Layer - Nghiệp vụ]
    Service --> DAO[DAO Layer - Truy xuất dữ liệu]
    DAO --> DB[(MySQL Database)]
    Service --> Model[Models - Đối tượng thực thể]
    DAO --> Model
Model Layer: Chứa các POJO (Plain Old Java Objects) như User, Room, Guest, Invoice đại diện cho các thực thể trong hệ thống.

DAO Layer (Data Access Object): Các Interface định nghĩa phương thức tương tác với CSDL.

Service Layer: Chứa Business Logic, xử lý các ràng buộc nghiệp vụ phức tạp trước khi gọi DAO.

Util Layer: Chứa cấu hình kết nối cơ sở dữ liệu JDBC.

🛠️ Cài đặt
Yêu cầu hệ thống
Java Development Kit (JDK): Phiên bản 17 trở lên.

Database: MySQL Server 8.0+.

IDE: IntelliJ IDEA, Eclipse hoặc NetBeans.

Các bước thiết lập
Clone dự án:

Bash
git clone https://github.com/your-repo/hotel-management.git
Thiết lập Cơ sở dữ liệu:

Mở MySQL Workbench hoặc Command Line.

Chạy file script JAVA/src/com/hotel/hotel_management.sql để tạo database và cấu trúc bảng.

Cấu hình thư viện:

Đảm bảo file mysql-connector-j-9.6.0.jar đã được thêm vào Classpath của dự án.

⚙️ Env Configuration
Thông tin kết nối cơ sở dữ liệu được cấu hình trong file DBConnection.java. Bạn cần thay đổi các tham số sau để phù hợp với môi trường local:

Java
// JAVA/src/com/hotel/util/DBConnection.java
private static final String URL = "jdbc:mysql://localhost:3306/hotel_management";
private static final String USER = "root"; // Tài khoản MySQL của bạn
private static final String PASSWORD = "your_password"; // Mật khẩu MySQL của bạn
🚀 Chạy project
Để kiểm tra kết nối giữa ứng dụng Java và MySQL, bạn có thể khởi chạy lớp kiểm thử:

Bash
# Chạy file TestConnection để xác nhận cấu hình DB
java com.hotel.test.TestConnection
Nếu thành công, màn hình sẽ hiển thị: "Chúc mừng! Bạn đã kết nối Database thành công.".

📂 Cấu trúc thư mục
Plaintext
com.hotel
├── dao          # Interfaces định nghĩa truy vấn CSDL (Guest, Invoice, Room...)
├── model        # Các lớp thực thể (User, Room, Guest, Service...)
├── service      # Logic nghiệp vụ (Auth, Stay, Inventory, Report...)
│   └── impl     # Lớp triển khai thực tế của các Service
├── test         # Các lớp kiểm thử hệ thống
├── util         # Tiện ích hệ thống (Kết nối DBConnection)
└── lib          # Thư viện ngoài (MySQL Connector)
🤝 Hướng dẫn đóng góp
Chúng tôi luôn hoan nghênh các đóng góp để hoàn thiện hệ thống:

Fork dự án.

Tạo nhánh tính năng mới (git checkout -b feature/AmazingFeature).

Commit các thay đổi (git commit -m 'Add some AmazingFeature').

Push lên nhánh (git push origin feature/AmazingFeature).

Mở một Pull Request.

🛣️ Roadmap
[ ] Xây dựng giao diện người dùng (GUI) bằng Java Swing/JavaFX.

[ ] Tích hợp thanh toán qua QR Code động.

[ ] Phát triển API REST để kết nối với ứng dụng di động.

[ ] Hệ thống dự báo tỷ lệ lấp đầy phòng dựa trên AI.

📄 Giấy phép
Phân phối dưới Giấy phép MIT. Xem LICENSE để biết thêm thông tin.

HMS Project - Professional Hospitality Management Solutions.