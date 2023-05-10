package com.example.vehicle_and_cargo_matching_system.dao;

import com.example.vehicle_and_cargo_matching_system.bean.Resource;

import java.sql.SQLException;
import java.util.List;

public interface ResourceAttentionDao {
    List<String> getResourceAttention(String id) throws SQLException;
    Resource getResource(String resourceKey) throws SQLException;
    int setResourceAttention(String resourceKey,String id) throws SQLException;
    int cancelResourceAttention(String resourceKey,String id) throws SQLException;
}
