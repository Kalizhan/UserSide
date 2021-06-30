package com.example.userzerde.modules;

public class User {
    String fullName, phone, email, password, ruqsat, imgUri, city, address, tochka, userId, token;

    public User() {}

    public User(String fullName, String phone, String email, String password, String ruqsat, String imgUri, String city, String address, String tochka, String userId, String token) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.ruqsat = ruqsat;
        this.imgUri = imgUri;
        this.city = city;
        this.address = address;
        this.tochka = tochka;
        this.userId = userId;
        this.token = token;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRuqsat() {
        return ruqsat;
    }

    public void setRuqsat(String ruqsat) {
        this.ruqsat = ruqsat;
    }

    public String getImgUri() {
        return imgUri;
    }

    public void setImgUri(String imgUri) {
        this.imgUri = imgUri;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTochka() {
        return tochka;
    }

    public void setTochka(String tochka) {
        this.tochka = tochka;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
