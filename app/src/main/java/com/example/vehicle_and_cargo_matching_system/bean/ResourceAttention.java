package com.example.vehicle_and_cargo_matching_system.bean;

public class ResourceAttention {
    private String resourceKey;//货源id
    private String driverKey;//司机id

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
}
