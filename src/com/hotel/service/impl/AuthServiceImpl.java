package com.hotel.service.impl;

import com.hotel.model.User;
import com.hotel.service.IAuthService;

public class AuthServiceImpl implements IAuthService {
    @Override
    public boolean register(User newUser, String confirmPassword) {
        // TODO: Kiểm tra định dạng email và mật khẩu 8 ký tự (chữ + số + in hoa + đặc biệt) [cite: 1]
        // TODO: Kiểm tra mật khẩu nhập lại có khớp không và email đã tồn tại chưa [cite: 1]
        return false;
    }

    @Override
    public boolean login(String email, String password) {
        // TODO: Xác thực thông tin đăng nhập và xử lý ẩn mật khẩu [cite: 1]
        return false;
    }

    @Override
    public void logout(String email) {
        // TODO: Xóa dữ liệu tạm (cache) và ghi nhật ký hoạt động (Audit Log) [cite: 37, 38]
    }

    @Override
    public void setRolePermission(String email, String role) {
        // TODO: Thiết lập quyền cho Quản lý, Lễ tân, Kế toán, Buồng phòng [cite: 34]
    }
}