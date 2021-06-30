package com.example.userzerde.modules;

import java.io.Serializable;

public class NewZakaz implements Serializable {
    String fullName, address, date, tovarCode, tovarSituation, tovarQuantity, phoneNum, email, comment, tovarName, tovarKey, checkDelivery, payment, paymentStyle;
    Long tovarPrice;

    public NewZakaz(){}

    public NewZakaz(String fullName, String phoneNum, String email, String address, String date, String tovarCode, String tovarSituation, Long tovarPrice, String tovarQuantity, String comment, String tovarName, String tovarKey,
                    String checkDelivery, String payment, String paymentStyle) {
        this.fullName = fullName;
        this.phoneNum = phoneNum;
        this.email = email;
        this.address = address;
        this.date = date;
        this.tovarCode = tovarCode;
        this.tovarSituation = tovarSituation;
        this.tovarPrice = tovarPrice;
        this.tovarQuantity = tovarQuantity;
        this.comment = comment;
        this.tovarName = tovarName;
        this.tovarKey = tovarKey;
        this.checkDelivery = checkDelivery;
        this.payment = payment;
        this.paymentStyle = paymentStyle;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTovarCode() {
        return tovarCode;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setTovarCode(String tovarName) {
        this.tovarCode = tovarName;
    }

    public String getTovarSituation() {
        return tovarSituation;
    }

    public void setTovarSituation(String tovarSituation) {
        this.tovarSituation = tovarSituation;
    }

    public Long getTovarPrice() {
        return tovarPrice;
    }

    public void setTovarPrice(Long tovarPrice) {
        this.tovarPrice = tovarPrice;
    }

    public String getTovarQuantity() {
        return tovarQuantity;
    }

    public void setTovarQuantity(String tovarQuantity) {
        this.tovarQuantity = tovarQuantity;
    }

    public String getTovarName() {
        return tovarName;
    }

    public void setTovarName(String tovarName) {
        this.tovarName = tovarName;
    }

    public String getTovarKey() {
        return tovarKey;
    }

    public void setTovarKey(String tovarKey) {
        this.tovarKey = tovarKey;
    }

    public String getCheckDelivery() {
        return checkDelivery;
    }

    public void setCheckDelivery(String checkDelivery) {
        this.checkDelivery = checkDelivery;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getPaymentStyle() {
        return paymentStyle;
    }

    public void setPaymentStyle(String paymentStyle) {
        this.paymentStyle = paymentStyle;
    }
}
