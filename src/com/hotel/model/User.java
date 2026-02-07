package com.hotel.model;
public class User {
    private String email;      // Định dạng email [cite: 1]
    private String password;   // 8 ký tự: chữ + số + in hoa + đặc biệt 
    private String role;       // Quản lý, Lễ tân, Kế toán, Buồng phòng [cite: 34]

    public User() {}

    public User(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getters và Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}