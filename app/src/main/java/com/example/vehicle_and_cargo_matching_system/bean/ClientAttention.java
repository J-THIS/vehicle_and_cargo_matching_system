package com.example.vehicle_and_cargo_matching_system.bean;

public class ClientAttention {
    private String clientKey;//货主id
    private String driverKey;//司机id

    public String getClientKey() {
        return clientKey;
    }

    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

    public String getDriverKey() {
        return driverKey;
    }

    public void setDriverKey(String driverKey) {
        this.driverKey = driverKey;
    }
}
