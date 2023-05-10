package com.example.vehicle_and_cargo_matching_system.dao.impl;

import com.example.vehicle_and_cargo_matching_system.bean.Client;
import com.example.vehicle_and_cargo_matching_system.bean.ClientAttention;
import com.example.vehicle_and_cargo_matching_system.bean.Resource;
import com.example.vehicle_and_cargo_matching_system.dao.ClientAttentionDao;
import com.example.vehicle_and_cargo_matching_system.util.DBUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientAttentionDaoImpl extends DBUtil implements ClientAttentionDao {
    @Override
    public List<String> getClientAttention(String id) throws SQLException {
        List<String> clientList = new ArrayList<>();
        getDatabase();
        String sql = "SELECT clientkey " +
                "FROM clientattention " +
                "WHERE driverkey = '" + id +"'";
        ResultSet rs = statement.executeQuery(sql);
        if(rs == null) return null;
        while (rs.next()) {
            clientList.add(rs.getString("clientkey"));
        }
        closeConnect();
        return clientList;
    }

    @Override
    public int setClientAttention(String clientKey, String id) throws SQLException {
        int result = 0;
        getDatabase();
        String sql = "INSERT INTO clientattention VALUES ('"+clientKey +"','"+id + "')";
        result = statement.executeUpdate(sql);
        closeConnect();
        return result;
    }

    @Override
    public int cancelClientAttention(String clientKey, String id) throws SQLException {
        int result = 0;
        getDatabase();
        String sql = "DELETE FROM clientattention WHERE clientkey = '"+clientKey+
                "' and driverkey = '"+id+"'";
        result = statement.executeUpdate(sql);
        closeConnect();
        return result;
    }

}
