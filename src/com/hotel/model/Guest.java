package com.hotel.model;
import java.util.Date;

public class Guest {
    private String name;         // Tên khách hàng [cite: 3]
    private String cccd;         // CCCD/Passport [cite: 3, 27]
    private String phone;        // Số điện thoại [cite: 3]
    private String gender;       // Giới tính [cite: 3]
    private Date birthDate;      // Ngày sinh [cite: 3]
    private String homeTown;     // Quê quán [cite: 3]
    private String email;        // Email để gửi hóa đơn [cite: 3]
    private String nationality;  // Quốc tịch [cite: 3]
    private String rank;         // Bạc, Vàng, Kim cương [cite: 28]

    public Guest() {}
}