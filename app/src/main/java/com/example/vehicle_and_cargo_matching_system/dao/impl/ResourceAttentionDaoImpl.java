package com.example.vehicle_and_cargo_matching_system.dao.impl;

import android.annotation.SuppressLint;

import com.example.vehicle_and_cargo_matching_system.bean.Resource;
import com.example.vehicle_and_cargo_matching_system.dao.ResourceAttentionDao;
import com.example.vehicle_and_cargo_matching_system.util.DBUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ResourceAttentionDaoImpl extends DBUtil implements ResourceAttentionDao {

    @Override
    public List<String> getResourceAttention(String id) throws SQLException {
        List<String> resourceList = new ArrayList<>();
        getDatabase();
        String sql = "SELECT resourcekey " +
                "FROM resourceattention " +
                "WHERE driverkey = '" + id +"'";
        ResultSet rs = statement.executeQuery(sql);
        if(rs == null) return null;
        while (rs.next()) {
            resourceList.add(rs.getString("resourcekey"));
        }
        closeConnect();
        return resourceList;
    }

    @Override
    public Resource getResource(String resourceKey) throws SQLException {
        Resource resource = new Resource();
        getDatabase();
        String sql = "SELECT resourcekey,cargoresource.clientkey,freight,loadplace1,unloadplace1," +
                "loadplace2,unloadplace2,loadplace3,unloadplace3,loadregion1,unloadregion1," +
                "loadregion2,unloadregion2,loadregion3,unloadregion3,loadtime1,unloadtime1," +
                "loadtime2,unloadtime2,loadtime3,unloadtime3,loadnum,unloadnum,resourcestate," +
                "releasetime,usetype,carlength,cartype,cargo,deposit,ifreturn " +
                "FROM cargoresource "+
                "WHERE resourcekey = '" + resourceKey + "'";
        ResultSet rs = statement.executeQuery(sql);
        if(rs == null) return null;
        while (rs.next()) {
            resource.setResourceKey(rs.getString("resourcekey"));
            resource.setClientKey(rs.getString("clientkey"));
            resource.setFreight(rs.getBigDecimal("freight"));
            resource.setLoadPlace1(rs.getString("loadplace1"));
            resource.setLoadPlace2(rs.getString("loadplace2"));
            resource.setLoadPlace3(rs.getString("loadplace3"));
            resource.setUnloadPlace1(rs.getString("unloadplace1"));
            resource.setUnloadPlace2(rs.getString("unloadplace2"));
            resource.setUnloadPlace3(rs.getString("unloadplace3"));
            resource.setLoadRegion1(rs.getString("loadregion1"));
            resource.setLoadRegion2(rs.getString("loadregion2"));
            resource.setLoadRegion3(rs.getString("loadregion3"));
            resource.setUnloadRegion1(rs.getString("unloadregion1"));
            resource.setUnloadRegion2(rs.getString("unloadregion2"));
            resource.setUnloadRegion3(rs.getString("unloadregion3"));
            //为了保证时分秒不丢失，
            try {
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String str_date;
                Date date = null;
                if (rs.getDate("loadtime1")!=null){
                    str_date = rs.getString("loadtime1");
                    date = (Date)format.parse(str_date);
                    resource.setLoadTime1(date);
                }else{
                    resource.setLoadTime1(rs.getDate("loadtime1"));
                }

                if (rs.getDate("loadtime2")!=null){
                    str_date = rs.getString("loadtime2");
                    date = (Date)format.parse(str_date);
                    resource.setLoadTime2(date);
                }else{
                    resource.setLoadTime2(rs.getDate("loadtime2"));
                }

                if (rs.getDate("loadtime3")!=null){
                    str_date = rs.getString("loadtime3");
                    date = (Date)format.parse(str_date);
                    resource.setLoadTime3(date);
                }else{
                    resource.setLoadTime3(rs.getDate("loadtime3"));
                }

                if (rs.getDate("unloadtime1")!=null){
                    str_date = rs.getString("unloadtime1");
                    date = (Date)format.parse(str_date);
                    resource.setUnloadTime1(date);
                }else{
                    resource.setUnloadTime1(rs.getDate("unloadtime1"));
                }

                if (rs.getDate("unloadtime2")!=null){
                    str_date = rs.getString("unloadtime2");
                    date = (Date)format.parse(str_date);
                    resource.setUnloadTime2(date);
                }else{
                    resource.setUnloadTime2(rs.getDate("unloadtime2"));
                }

                if (rs.getDate("unloadtime3")!=null){
                    str_date = rs.getString("unloadtime3");
                    date = (Date)format.parse(str_date);
                    resource.setUnloadTime3(date);
                }else{
                    resource.setUnloadTime3(rs.getDate("unloadtime3"));
                }
                if (rs.getDate("releasetime")!=null){
                    str_date = rs.getString("releasetime");
                    date = (Date)format.parse(str_date);
                    resource.setReleaseTime(date);
                }else{
                    resource.setReleaseTime(rs.getDate("releasetime"));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            resource.setLoadNum(rs.getInt("loadnum"));
            resource.setUnloadNum(rs.getInt("unloadnum"));
            resource.setResourceState(rs.getInt("resourcestate"));
            resource.setUseType(rs.getString("usetype"));
            resource.setCarLength(rs.getBigDecimal("carlength"));
            resource.setCarType(rs.getString("cartype"));
            resource.setCargo(rs.getString("cargo"));
            resource.setDeposit(rs.getBigDecimal("deposit"));
            resource.setIfReturn(rs.getInt("ifreturn"));
        }
        closeConnect();
        return resource;
    }

    @Override
    public int setResourceAttention(String resourceKey, String id) throws SQLException {
        int result = 0;
        getDatabase();
        String sql = "INSERT INTO resourceattention VALUES ('"+resourceKey +"','"+id + "')";
        result = statement.executeUpdate(sql);
        closeConnect();
        return result;
    }

    @Override
    public int cancelResourceAttention(String resourceKey, String id) throws SQLException {
        int result = 0;
        getDatabase();
        String sql = "DELETE FROM resourceattention WHERE resourcekey = '"+resourceKey+
                "' and driverkey = '"+id+"'";
        result = statement.executeUpdate(sql);
        closeConnect();
        return result;
    }
}
