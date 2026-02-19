package com.hotel.service;
import com.hotel.model.User;

public interface IAuthService {
    /**
     * [cite_start]Đăng ký: Kiểm tra định dạng email và mật khẩu 8 ký tự (chữ + số + in hoa + đặc biệt). [cite: 1]
     * [cite_start]Một email chỉ được đăng ký duy nhất một lần. [cite: 1]
     */
    boolean register(User newUser, String confirmPassword);

    /**
     * [cite_start]Đăng nhập: Đúng định dạng email và mật khẩu, mật khẩu phải được ẩn khi nhập. [cite: 1]
     */
    boolean login(String email, String password);

    /**
     * [cite_start]Đăng xuất: Xóa dữ liệu tạm (cache) và ghi nhật ký hoạt động (Audit Log). [cite: 37, 38]
     */
    void logout(String email);

    /**
     * [cite_start]Phân quyền (RBAC): Thiết lập quyền cho Quản lý, Lễ tân, Kế toán, Buồng phòng. [cite: 34]
     */
    void setRolePermission(String email, String role);
}