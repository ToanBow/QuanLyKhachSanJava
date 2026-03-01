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

    public Guest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getHomeTown() {
        return homeTown;
    }

    public void setHomeTown(String homeTown) {
        this.homeTown = homeTown;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }
    
}