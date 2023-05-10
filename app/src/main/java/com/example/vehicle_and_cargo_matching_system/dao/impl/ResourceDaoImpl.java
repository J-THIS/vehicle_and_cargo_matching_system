package com.example.vehicle_and_cargo_matching_system.dao.impl;

import android.annotation.SuppressLint;
import android.util.Log;

import com.example.vehicle_and_cargo_matching_system.bean.Resource;
import com.example.vehicle_and_cargo_matching_system.bean.Resource;
import com.example.vehicle_and_cargo_matching_system.dao.ResourceDao;
import com.example.vehicle_and_cargo_matching_system.util.DBUtil;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ResourceDaoImpl extends DBUtil implements ResourceDao {

    @Override
    public List<Resource> getResource(String load_region,String unload_region,int resource_quality,
              String cargo,String use_type,String car_length,String car_type) throws SQLException {
        String sql,str_select,str_from,str_where;
        List<Resource> ResourceList = new ArrayList<>();
        getDatabase();
        str_select = "SELECT resourcekey,cargoresource.clientkey,freight,loadplace1,unloadplace1," +
                "loadplace2,unloadplace2,loadplace3,unloadplace3,loadregion1,unloadregion1," +
                "loadregion2,unloadregion2,loadregion3,unloadregion3,loadtime1,unloadtime1," +
                "loadtime2,unloadtime2,loadtime3,unloadtime3,loadnum,unloadnum,resourcestate," +
                "releasetime,usetype,carlength,cartype,cargo,deposit,ifreturn ";
        str_from = "FROM cargoresource ";
        str_where = "WHERE ";
        if (!Objects.equals(load_region, "全国")){
            str_where = str_where+"(loadregion1 = '" + load_region + "' or loadregion2 = '" + load_region +
                    "' or loadregion3 = '" + load_region + "') and ";
        }
        if (!Objects.equals(unload_region, "全国")){
            str_where = str_where+"(unloadregion1 = '" + unload_region+"' or unloadregion2 = '"
                    + unload_region + "' or unloadregion3 = '" + unload_region + "') and ";
        }
        if (resource_quality == 2){
            str_from = "FROM cargoresource,client ";
            str_where = str_where+"cargoresource.clientkey = client.clientkey and credit >= 4 and ";
        }else if (resource_quality == 1){
            str_where = str_where + "ifreturn = 1 and ";
        }
        if (!Objects.equals(cargo, "不限")){
            str_where = str_where + "cargo = '" + cargo + "' and ";
        }
        if (use_type!=null){
            if (!Objects.equals(use_type, "不限")){
                str_where = str_where + "usetype = '" + use_type + "' and ";
            }
        }
        if (!Objects.equals(car_length, "不限")){
            str_where = str_where + "carlength = " + car_length + " and ";
        }
        if (!Objects.equals(car_type, "不限")){
            str_where = str_where + "cartype = '" + car_type + "' and ";
        }
        str_where = str_where + "resourcestate = 1";
        sql = str_select + str_from + str_where;
        Log.i("sql", sql);
        ResultSet rs = statement.executeQuery(sql);
        if(rs == null) return null;
        while (rs.next()) {
            //将结果集的内容读取出来，存入list中
            Resource resource = new Resource();
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
            ResourceList.add(resource);
        }
        closeConnect();
        return ResourceList;
    }

    @Override
    public Integer getResourceState(String resourceKey) throws SQLException {
        Integer resource_state = null;
        getDatabase();
        String sql = "SELECT resourcestate From cargoresource " +
                "WHERE resourcekey = '" + resourceKey + "'";
        ResultSet rs = statement.executeQuery(sql);
        if(rs == null) return null;
        while (rs.next()) {
            resource_state = rs.getInt("resourcestate");
        }
        closeConnect();
        return resource_state;
    }

    @Override
    public void setResourceState(String resourceKey,Integer resourceState) throws SQLException {
        getDatabase();
        String sql = "UPDATE cargoresource SET resourcestate = '" + resourceState + "' " +
                "WHERE resourcekey = '" + resourceKey + "'";
        statement.executeUpdate(sql);
        closeConnect();
    }

    @Override
    public List<Resource> getResourceByClient(String clientKey) throws SQLException {
        String sql,str_select,str_from,str_where;
        List<Resource> ResourceList = new ArrayList<>();
        getDatabase();
        str_select = "SELECT resourcekey,cargoresource.clientkey,freight,loadplace1,unloadplace1," +
                "loadplace2,unloadplace2,loadplace3,unloadplace3,loadregion1,unloadregion1," +
                "loadregion2,unloadregion2,loadregion3,unloadregion3,loadtime1,unloadtime1," +
                "loadtime2,unloadtime2,loadtime3,unloadtime3,loadnum,unloadnum,resourcestate," +
                "releasetime,usetype,carlength,cartype,cargo,deposit,ifreturn ";
        str_from = "FROM cargoresource ";
        str_where = "WHERE clientkey = '" + clientKey + "' and resourcestate = 1";
        sql = str_select + str_from + str_where;
        Log.i("sql", sql);
        ResultSet rs = statement.executeQuery(sql);
        if(rs == null) return null;
        while (rs.next()) {
            //将结果集的内容读取出来，存入list中
            Resource resource = new Resource();
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
            ResourceList.add(resource);
        }
        closeConnect();
        return ResourceList;
    }
}
