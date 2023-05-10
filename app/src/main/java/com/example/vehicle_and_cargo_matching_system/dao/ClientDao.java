package com.example.vehicle_and_cargo_matching_system.dao;

import com.example.vehicle_and_cargo_matching_system.bean.Client;
import com.example.vehicle_and_cargo_matching_system.bean.LineAttention;

import java.sql.SQLException;
import java.util.List;

public interface ClientDao {
    Client getClient(String clientKey) throws SQLException;
    void setApplauseNum(String clientKey) throws SQLException;
    void setComplaintNum(String clientKey) throws SQLException;
}
