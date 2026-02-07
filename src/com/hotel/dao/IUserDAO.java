package com.hotel.dao;

import com.hotel.model.User;
import java.util.List;

public interface IUserDAO {
    // Tìm người dùng theo email để kiểm tra đăng nhập hoặc trùng lặp khi đăng ký [cite: 1]
    User findByEmail(String email);

    // Lưu người dùng mới vào hệ thống (Đăng ký) [cite: 1]
    boolean insert(User user);

    // Cập nhật thông tin hoặc phân quyền (Quản lý, Lễ tân...) [cite: 34]
    boolean update(User user);

    // Lấy danh sách tất cả nhân viên để quản lý
    List<User> getAllUsers();
}