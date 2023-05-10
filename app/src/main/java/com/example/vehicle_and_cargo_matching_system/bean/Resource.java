package com.example.vehicle_and_cargo_matching_system.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Resource implements Serializable,Comparable{
    private String resourceKey;//货源id
    private String clientKey;//货主id
    public BigDecimal freight;//运费
    private String loadPlace1;//装货地1
    private String unloadPlace1;//卸货地1
    private String loadPlace2;//装货地2
    private String unloadPlace2;//卸货地2
    private String loadPlace3;//装货地3
    private String unloadPlace3;//卸货地3
    private String loadRegion1;//装货地区1
    private String unloadRegion1;//卸货地区1
    private String loadRegion2;//装货地区2
    private String unloadRegion2;//卸货地区2
    private String loadRegion3;//装货地区3
    private String unloadRegion3;//卸货地区3
    private Date loadTime1;//装货时间1
    private Date unloadTime1;//卸货时间1
    private Date loadTime2;//装货时间2
    private Date unloadTime2;//卸货时间2
    private Date loadTime3;//装货时间3
    private Date unloadTime3;//卸货时间3
    private Integer loadNum;//装货次数
    private Integer unloadNum;//卸货次数
    private Integer resourceState;//货源状态
    public Date releaseTime;//发布时间
    private String useType;//用车类型
    private BigDecimal carLength;//车长
    private String carType;//车型
    private String cargo;//货物
    public BigDecimal deposit;//订金
    private Integer ifReturn;//是否退还订金 0--退还 1--不退还

    public Integer getIfReturn() {
        return ifReturn;
    }

    public void setIfReturn(Integer ifReturn) {
        this.ifReturn = ifReturn;
    }

    public String getResourceKey() {
        return resourceKey;
    }

    public void setResourceKey(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    public String getClientKey() {
        return clientKey;
    }

    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

    public BigDecimal getFreight() {
        return freight;
    }

    public void setFreight(BigDecimal freight) {
        this.freight = freight;
    }

    public String getLoadPlace1() {
        return loadPlace1;
    }

    public void setLoadPlace1(String loadPlace1) {
        this.loadPlace1 = loadPlace1;
    }

    public String getUnloadPlace1() {
        return unloadPlace1;
    }

    public void setUnloadPlace1(String unloadPlace1) {
        this.unloadPlace1 = unloadPlace1;
    }

    public String getLoadPlace2() {
        return loadPlace2;
    }

    public void setLoadPlace2(String loadPlace2) {
        this.loadPlace2 = loadPlace2;
    }

    public String getUnloadPlace2() {
        return unloadPlace2;
    }

    public void setUnloadPlace2(String unloadPlace2) {
        this.unloadPlace2 = unloadPlace2;
    }

    public String getLoadPlace3() {
        return loadPlace3;
    }

    public void setLoadPlace3(String loadPlace3) {
        this.loadPlace3 = loadPlace3;
    }

    public String getUnloadPlace3() {
        return unloadPlace3;
    }

    public void setUnloadPlace3(String unloadPlace3) {
        this.unloadPlace3 = unloadPlace3;
    }

    public String getLoadRegion1() {
        return loadRegion1;
    }

    public void setLoadRegion1(String loadRegion1) {
        this.loadRegion1 = loadRegion1;
    }

    public String getUnloadRegion1() {
        return unloadRegion1;
    }

    public void setUnloadRegion1(String unloadRegion1) {
        this.unloadRegion1 = unloadRegion1;
    }

    public String getLoadRegion2() {
        return loadRegion2;
    }

    public void setLoadRegion2(String loadRegion2) {
        this.loadRegion2 = loadRegion2;
    }

    public String getUnloadRegion2() {
        return unloadRegion2;
    }

    public void setUnloadRegion2(String unloadRegion2) {
        this.unloadRegion2 = unloadRegion2;
    }

    public String getLoadRegion3() {
        return loadRegion3;
    }

    public void setLoadRegion3(String loadRegion3) {
        this.loadRegion3 = loadRegion3;
    }

    public String getUnloadRegion3() {
        return unloadRegion3;
    }

    public void setUnloadRegion3(String unloadRegion3) {
        this.unloadRegion3 = unloadRegion3;
    }

    public Date getLoadTime1() {
        return loadTime1;
    }

    public void setLoadTime1(Date loadTime1) {
        this.loadTime1 = loadTime1;
    }

    public Date getUnloadTime1() {
        return unloadTime1;
    }

    public void setUnloadTime1(Date unloadTime1) {
        this.unloadTime1 = unloadTime1;
    }

    public Date getLoadTime2() {
        return loadTime2;
    }

    public void setLoadTime2(Date loadTime2) {
        this.loadTime2 = loadTime2;
    }

    public Date getUnloadTime2() {
        return unloadTime2;
    }

    public void setUnloadTime2(Date unloadTime2) {
        this.unloadTime2 = unloadTime2;
    }

    public Date getLoadTime3() {
        return loadTime3;
    }

    public void setLoadTime3(Date loadTime3) {
        this.loadTime3 = loadTime3;
    }

    public Date getUnloadTime3() {
        return unloadTime3;
    }

    public void setUnloadTime3(Date unloadTime3) {
        this.unloadTime3 = unloadTime3;
    }

    public Integer getLoadNum() {
        return loadNum;
    }

    public void setLoadNum(Integer loadNum) {
        this.loadNum = loadNum;
    }

    public Integer getUnloadNum() {
        return unloadNum;
    }

    public void setUnloadNum(Integer unloadNum) {
        this.unloadNum = unloadNum;
    }

    public Integer getResourceState() {
        return resourceState;
    }

    public void setResourceState(Integer resourceState) {
        this.resourceState = resourceState;
    }

    public Date getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Date releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getUseType() {
        return useType;
    }

    public void setUseType(String useType) {
        this.useType = useType;
    }

    public BigDecimal getCarLength() {
        return carLength;
    }

    public void setCarLength(BigDecimal carLength) {
        this.carLength = carLength;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
