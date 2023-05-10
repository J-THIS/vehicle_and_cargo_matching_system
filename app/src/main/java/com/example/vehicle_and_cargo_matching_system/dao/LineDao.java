package com.example.vehicle_and_cargo_matching_system.dao;

import com.example.vehicle_and_cargo_matching_system.bean.LineAttention;

import java.sql.SQLException;
import java.util.List;

public interface LineDao {
    List<LineAttention> getLineAttention(String driverKey) throws SQLException;
    int addLineAttention(String id,int attention_num,String load_place,String unload_place,
                         String use_type,String car_length,String car_type) throws SQLException;
    void deleteLineAttention(String lineKey) throws SQLException;
}
