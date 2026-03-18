package com.hotel.util;

import com.hotel.model.User;

public class SessionManager {
    private static User currentUser;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void clearSession() {
        currentUser = null; // Xóa dữ liệu tạm thời (cache)
    }
}