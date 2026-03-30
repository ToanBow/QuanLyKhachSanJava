package com.hotel.service;
import com.hotel.model.User;

public interface IAuthService {
    /**
    ]Đăng ký: Kiểm tra định dạng email và mật khẩu 8 ký tự (chữ + số + in hoa + đặc biệt). 
    Một email chỉ được đăng ký duy nhất một lần. 
     */
    boolean register(User newUser, String confirmPassword);

    /**
    Đăng nhập: Đúng định dạng email và mật khẩu, mật khẩu phải được ẩn khi nhập. 
     */
    boolean login(String email, String password);

    /**
    Đăng xuất: Xóa dữ liệu tạm (cache) và ghi nhật ký hoạt động (Audit Log). 
     */
    void logout(String email);

    /**
    Phân quyền (RBAC): Thiết lập quyền cho Quản lý, Lễ tân, Kế toán, Buồng phòng.
     */
    void setRolePermission(String email, String role);
}