package com.hotel.model;
import java.util.Date;

public class Guest {
    private String name;         // Tên khách hàng
    private String cccd;         // CCCD/Passport 
    private String phone;        // Số điện thoại 
    private String gender;       // Giới tính 
    private Date birthDate;      // Ngày sinh 
    private String homeTown;     // Quê quán
    private String email;        // Email để gửi hóa đơn 
    private String nationality;  // Quốc tịch
    private String rank;         // Bạc, Vàng, Kim cương
    private boolean blacklist;
    public Guest(String guestId, String name, String email, String phone, String cccd) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.cccd = cccd;
    }
    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public boolean isBlacklist() {
        return blacklist;
    }

    public void setBlacklist(boolean blacklist) {
        this.blacklist = blacklist;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHomeTown() {
        return homeTown;
    }

    public void setHomeTown(String homeTown) {
        this.homeTown = homeTown;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }
}
