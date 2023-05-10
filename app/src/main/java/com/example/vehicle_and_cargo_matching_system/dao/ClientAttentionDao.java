package com.example.vehicle_and_cargo_matching_system.dao;

import com.example.vehicle_and_cargo_matching_system.bean.ClientAttention;

import java.sql.SQLException;
import java.util.List;

public interface ClientAttentionDao {
    List<String> getClientAttention(String id) throws SQLException;
    int setClientAttention(String clientKey,String id) throws SQLException;
    int cancelClientAttention(String clientKey,String id) throws SQLException;
}
