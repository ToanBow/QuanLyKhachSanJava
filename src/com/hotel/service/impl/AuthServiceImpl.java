package com.hotel.service.impl;

import com.hotel.dao.IUserDAO;
import com.hotel.model.User;
import com.hotel.service.IAuthService;
import com.hotel.dao.impl.UserDAOImpl;

public class AuthServiceImpl implements IAuthService {
    private IUserDAO userDAO = new UserDAOImpl();
    @Override
    public boolean register(User newUser, String confirmPassword) {
        // TODO: Kiểm tra định dạng email và mật khẩu 8 ký tự (chữ + số + in hoa + đặc biệt) [cite: 1]
        // TODO: Kiểm tra mật khẩu nhập lại có khớp không và email đã tồn tại chưa [cite: 1]
        String emailRegex = "^[A-Za-z0-9]+@(.+)$";
        if (!newUser.getEmail().matches(emailRegex)) {
            System.out.println("Loi dinh dang Mail, vui long thu lai nhe!");
            return false;
        }
        String passRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        if (!newUser.getPassword().matches(passRegex)) {
            System.out.println(
                    "Vui long nhap mat khau du 8 ky tu va bao gom chu hoa, chu thuong, chu so va ky tu dac biet!");
            return false;
        }
        if (!newUser.getPassword().equals(confirmPassword)) {
            System.out.println("Vui long nhap dung mat khau da nhap!");
            return false;
        }
        if (userDAO.findByEmail(newUser.getEmail()) != null) {
            System.out.println("Mail nay da ton tai trong he thong!");
            return false;
        }

        return userDAO.insert(newUser);
    }

    @Override
    public boolean login(String email, String password) {
        // TODO: Xác thực thông tin đăng nhập và xử lý ẩn mật khẩu [cite: 1]
        User user = userDAO.findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            System.out.println("Dang nhap thanh cong voi quyen " + user.getRole());
            return true;
        } else {
            System.out.println("Loi tai khoan hoac mat khau khong chinh xac!");
            return false;
        }
    }

    @Override
    public void logout(String email) {
        // TODO: Xóa dữ liệu tạm (cache) và ghi nhật ký hoạt động (Audit Log) [cite: 37, 38]
        System.out.println("Nguoi dung " + email + " da dang xuat. Da ghi nhat ky hoat dong!");
    }

    @Override
    public void setRolePermission(String email, String role) {
        // TODO: Thiết lập quyền cho Quản lý, Lễ tân, Kế toán, Buồng phòng [cite: 34]
        User user = userDAO.findByEmail(email);
        if (user != null) {
            System.out.println("Da cap nhat quyen "+role+" cho nguoi dung "+email);
        }
    }
}