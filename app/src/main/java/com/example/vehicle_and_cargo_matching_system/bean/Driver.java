package com.example.vehicle_and_cargo_matching_system.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class Driver implements Serializable {
    private String phone;//注册手机号
    private String password;//账户密码
    private String IdNumber;//身份证号
    private String surName;//姓
    private String givenName;//名
    private String licenseId;//驾驶证号
    private String registrationId;//行驶证号
    private String carType;//车种
    private String equipment;//整车装备
    private BigDecimal useLength;//载货长度
    private BigDecimal innerLength;//内长
    private BigDecimal innerWidth;//内宽
    private BigDecimal innerHigh;//内高

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIdNumber() {
        return IdNumber;
    }

    public void setIdNumber(String idNumber) {
        IdNumber = idNumber;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(String licenseId) {
        this.licenseId = licenseId;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public BigDecimal getUseLength() {
        return useLength;
    }

    public void setUseLength(BigDecimal useLength) {
        this.useLength = useLength;
    }

    public BigDecimal getInnerLength() {
        return innerLength;
    }

    public void setInnerLength(BigDecimal innerLength) {
        this.innerLength = innerLength;
    }

    public BigDecimal getInnerWidth() {
        return innerWidth;
    }

    public void setInnerWidth(BigDecimal innerWidth) {
        this.innerWidth = innerWidth;
    }

    public BigDecimal getInnerHigh() {
        return innerHigh;
    }

    public void setInnerHigh(BigDecimal innerHigh) {
        this.innerHigh = innerHigh;
    }
}
