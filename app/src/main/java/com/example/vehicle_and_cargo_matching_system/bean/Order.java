package com.example.vehicle_and_cargo_matching_system.bean;

import java.io.Serializable;
import java.util.Date;

public class Order implements Serializable {
    private String orderKey;//订单id
    private String resourceKey;//货源id
    private String driverKey;//司机id
    private Integer orderState;//订单状态(0未装货，1运输中，2待评价，3已取消, 4已完成（已评价）)
    private String content;//评价内容
    private Integer evaluation;//评价(0差评, 1好评)
    private Date createTime;//创建时间
    private Date realLoadTime;//实际完成装货时间
    private Date realUnloadTime;//实际完成卸货时间
    private Date defaultPayTime;//运费到账默认时间
    private Date realPayTime;//运费到账实际时间
    private Integer depositState;//订金状态(0未退还，1已退还(若订金可退), 2已支付给货主(若订金不可退),)

    public String getOrderKey() {
        return orderKey;
    }

    public void setOrderKey(String orderKey) {
        this.orderKey = orderKey;
    }

    public String getResourceKey() {
        return resourceKey;
    }

    public void setResourceKey(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    public String getDriverKey() {
        return driverKey;
    }

    public void setDriverKey(String driverKey) {
        this.driverKey = driverKey;
    }

    public Integer getOrderState() {
        return orderState;
    }

    public void setOrderState(Integer orderState) {
        this.orderState = orderState;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(Integer evaluation) {
        this.evaluation = evaluation;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getRealLoadTime() {
        return realLoadTime;
    }

    public void setRealLoadTime(Date realLoadTime) {
        this.realLoadTime = realLoadTime;
    }

    public Date getRealUnloadTime() {
        return realUnloadTime;
    }

    public void setRealUnloadTime(Date realUnloadTime) {
        this.realUnloadTime = realUnloadTime;
    }

    public Date getDefaultPayTime() {
        return defaultPayTime;
    }

    public void setDefaultPayTime(Date defaultPayTime) {
        this.defaultPayTime = defaultPayTime;
    }

    public Date getRealPayTime() {
        return realPayTime;
    }

    public void setRealPayTime(Date realPayTime) {
        this.realPayTime = realPayTime;
    }

    public Integer getDepositState() {
        return depositState;
    }

    public void setDepositState(Integer depositState) {
        this.depositState = depositState;
    }
}
