package com.example.vehicle_and_cargo_matching_system.bean;

import java.io.Serializable;

public class Client implements Serializable {
    private String clientKey;//货主id
    private String surname;//货主姓
    private String givenName;//货主名
    private Integer credit;//信用星级
    private Integer cancelNum;//取消单数
    private Integer complaintNum;//投诉单数
    private Integer applauseNum;//好评单数
    private Integer finishNum;//完单量
    private Integer closeNum;//成交数
    private String phone;//手机

    public String getClientKey() {
        return clientKey;
    }

    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }

    public Integer getCancelNum() {
        return cancelNum;
    }

    public void setCancelNum(Integer cancelNum) {
        this.cancelNum = cancelNum;
    }

    public Integer getComplaintNum() {
        return complaintNum;
    }

    public void setComplaintNum(Integer complaintNum) {
        this.complaintNum = complaintNum;
    }

    public Integer getApplauseNum() {
        return applauseNum;
    }

    public void setApplauseNum(Integer applauseNum) {
        this.applauseNum = applauseNum;
    }

    public Integer getFinishNum() {
        return finishNum;
    }

    public void setFinishNum(Integer finishNum) {
        this.finishNum = finishNum;
    }

    public Integer getCloseNum() {
        return closeNum;
    }

    public void setCloseNum(Integer closeNum) {
        this.closeNum = closeNum;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
