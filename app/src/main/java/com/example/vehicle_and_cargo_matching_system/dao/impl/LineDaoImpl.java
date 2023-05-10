package com.example.vehicle_and_cargo_matching_system.dao.impl;

import android.util.Log;

import com.example.vehicle_and_cargo_matching_system.bean.LineAttention;
import com.example.vehicle_and_cargo_matching_system.dao.LineDao;
import com.example.vehicle_and_cargo_matching_system.util.DBUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LineDaoImpl extends DBUtil implements LineDao {

    @Override
    public List<LineAttention> getLineAttention(String driverKey) throws SQLException {
        String sql;
        List<LineAttention> lineAttentionList = new ArrayList<>();
        getDatabase();
        sql = "SELECT linekey,driverkey,loadplace,unloadplace,usetype,carlength,cartype "+
                "FROM lineattention "+
                "WHERE lineattention.driverkey = '" + driverKey + "'";
        ResultSet rs = statement.executeQuery(sql);
        if(rs == null) return null;
        while (rs.next()) {
            //将结果集的内容读取出来，存入list中
            LineAttention lineAttention = new LineAttention();
            lineAttention.setLineKey(rs.getString("linekey"));
            lineAttention.setDriverKey(rs.getString("driverkey"));
            lineAttention.setLoadPlace(rs.getString("loadplace"));
            lineAttention.setUnloadPlace(rs.getString("unloadplace"));
            lineAttention.setUseType(rs.getString("usetype"));
            lineAttention.setCarLength(rs.getBigDecimal("carlength"));
            lineAttention.setCarType(rs.getString("cartype"));
            lineAttentionList.add(lineAttention);
        }
        closeConnect();
        return lineAttentionList;
    }

    @Override
    public int addLineAttention(String id,int attention_num,String load_place,String unload_place,
                            String use_type,String car_length,String car_type) throws SQLException {
        int result = 0;
        getDatabase();
        String line_key = id + attention_num;
        String sql = "INSERT INTO lineattention VALUES ('" + line_key + "','" + id + "','" +
                load_place + "','" + unload_place + "',";
        if (Objects.equals(use_type, "不限")){
            sql = sql + "null,";
        }else {
            sql = sql + "'" + use_type + "',";
        }
        if (Objects.equals(car_length, "不限")){
            sql = sql + "null,";
        }else {
            sql = sql + "'" + car_length + "',";
        }
        if (Objects.equals(car_type, "不限")){
            sql = sql + "null)";
        }else {
            sql = sql + "'" + car_type + "')";
        }
        result = statement.executeUpdate(sql);
        closeConnect();
        return result;
    }

    @Override
    public void deleteLineAttention(String lineKey) throws SQLException {
        getDatabase();
        String sql = "DELETE FROM lineattention WHERE linekey = '" + lineKey + "'";
        statement.executeUpdate(sql);
        closeConnect();
    }
}
