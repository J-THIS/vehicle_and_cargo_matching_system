package com.example.vehicle_and_cargo_matching_system.bean;

import java.math.BigDecimal;

public class LineAttention {
    private String lineKey;//线路id
    private String driverKey;//司机id
    private String loadPlace;//装货地
    private String unloadPlace;//卸货地
    private String useType;//用车类型
    private BigDecimal carLength;//车长
    private String carType;//车型

    public String getLineKey() {
        return lineKey;
    }

    public void setLineKey(String lineKey) {
        this.lineKey = lineKey;
    }

    public String getDriverKey() {
        return driverKey;
    }

    public void setDriverKey(String driverKey) {
        this.driverKey = driverKey;
    }

    public String getLoadPlace() {
        return loadPlace;
    }

    public void setLoadPlace(String loadPlace) {
        this.loadPlace = loadPlace;
    }

    public String getUnloadPlace() {
        return unloadPlace;
    }

    public void setUnloadPlace(String unloadPlace) {
        this.unloadPlace = unloadPlace;
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
}
