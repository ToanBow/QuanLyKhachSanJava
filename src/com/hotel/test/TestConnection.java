package com.hotel.test;

import com.hotel.util.DBConnection;
import java.sql.Connection;

public class TestConnection {
    public static void main(String[] args) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            System.out.println("Chúc mừng! Bạn đã kết nối Database thành công.");
            DBConnection.closeConnection(conn);
        } else {
            System.out.println("Kết nối thất bại. Vui lòng kiểm tra lại Password trong file DBConnection.");
        }
    }
}