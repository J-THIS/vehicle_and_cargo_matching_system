package com.example.vehicle_and_cargo_matching_system.dao;

import com.example.vehicle_and_cargo_matching_system.bean.Order;

import java.sql.SQLException;
import java.util.List;

public interface OrderDao {
    int createOrder(String resourceKey,String id) throws  SQLException;
    void setOrderState(String orderKey,Integer orderState, Integer ifReturn) throws SQLException;
    List<Order> getOrderList(String id,Integer orderState) throws SQLException;
    List<Order> getOrderListByClient(String clientKey,Integer orderState) throws SQLException;
    void deleteOrder(String orderKey) throws SQLException;
    void setOrderEvaluation(String orderKey,Integer evaluation,String content) throws SQLException;
}
