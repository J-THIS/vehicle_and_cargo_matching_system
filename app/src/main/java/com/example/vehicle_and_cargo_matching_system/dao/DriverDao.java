package com.example.vehicle_and_cargo_matching_system.dao;

import com.example.vehicle_and_cargo_matching_system.bean.Client;
import com.example.vehicle_and_cargo_matching_system.bean.Driver;

import java.math.BigDecimal;
import java.sql.SQLException;

public interface DriverDao {
    Driver getDriver(String id) throws SQLException;
    int addDriver(Driver driver) throws SQLException;
    int setCarInformation(String id, String car_type, String use_length, BigDecimal inner_length,
                          BigDecimal inner_width, BigDecimal inner_high, String equipment) throws SQLException;
    int setPassword(String id,String password) throws SQLException;
}
