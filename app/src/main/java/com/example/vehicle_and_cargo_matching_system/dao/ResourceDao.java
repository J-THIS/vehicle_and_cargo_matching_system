package com.example.vehicle_and_cargo_matching_system.dao;

import com.example.vehicle_and_cargo_matching_system.bean.LineAttention;
import com.example.vehicle_and_cargo_matching_system.bean.Resource;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface ResourceDao {
    List<Resource> getResource(String load_region,String unload_region,int resource_quality,
           String cargo,String use_type,String car_length,String car_type) throws SQLException;
    Integer getResourceState(String resourceKey) throws SQLException;
    void setResourceState(String resourceKey,Integer resourceState) throws SQLException;
    List<Resource> getResourceByClient(String clientKey) throws SQLException;
}
