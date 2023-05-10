package com.example.vehicle_and_cargo_matching_system.dao.impl;

import android.annotation.SuppressLint;
import android.util.Log;

import com.example.vehicle_and_cargo_matching_system.bean.LineAttention;
import com.example.vehicle_and_cargo_matching_system.bean.Order;
import com.example.vehicle_and_cargo_matching_system.dao.OrderDao;
import com.example.vehicle_and_cargo_matching_system.util.DBUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderDaoImpl  extends DBUtil implements OrderDao {
    @Override
    public int createOrder(String resourceKey, String id) throws SQLException {
        int result = 0;
        getDatabase();
        java.util.Date today = new Date();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf1= new SimpleDateFormat("yyyyMMddHHmmss");
        String orderKey = resourceKey + sdf1.format(today);
        String sql = "INSERT INTO orderlist VALUES ('"+orderKey +"','"+resourceKey+"','"+id+"','0',"
                +"null,"+"null,'"+sdf.format(today)+"',"+"null,"+"null,"+"null,"+"null,'0')";
        result = statement.executeUpdate(sql);
        closeConnect();
        return result;
    }

    @Override
    public void setOrderState(String orderKey, Integer orderState, Integer ifReturn) throws SQLException {
        getDatabase();
        String str_update,str_where;
        if (orderState == 2 && ifReturn == 1){//若订金可退还且已送达，则订金退还给司机
            str_update = "UPDATE orderlist SET orderstate = '" + orderState + "' , depositstate" +
                    "= '1' " ;
        }else if(orderState == 1 && ifReturn == 0) {//若订金不可退且已装货，则订金支付给货主
            str_update = "UPDATE orderlist SET orderstate = '" + orderState + "' , depositstate" +
                    "= '2' " ;
        }else if (orderState == 3) {//若订单已取消，则订金退还给司机
            str_update = "UPDATE orderlist SET orderstate = '" + orderState + "' , depositstate" +
                    "= '1' " ;
        }else{
            str_update = "UPDATE orderlist SET orderstate = '" + orderState + "' " ;
        }
        if (orderState == 1){//点击“确认装货”
            java.util.Date today = new Date();
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            str_update = str_update + ", realloadtime = '" + sdf.format(today) + "' ";
        }
        if (orderState == 2){//点击“确认送达”
            java.util.Date today = new Date();
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            str_update = str_update + ", realunloadtime = '" + sdf.format(today) + "' ";
        }
        str_where = "WHERE orderkey = '" + orderKey + "'";
        String sql = str_update + str_where;
        Log.i("sql",sql);
        statement.executeUpdate(sql);
        closeConnect();
    }

    @Override
    public List<Order> getOrderList(String id,Integer orderState) throws SQLException {
        String sql;
        List<Order> orderList = new ArrayList<>();
        getDatabase();
        sql = "SELECT orderkey,resourcekey,driverkey,orderstate,content,evaluation,createtime," +
                "realloadtime,realunloadtime,defaultpaytime,realpaytime,depositstate "+
                "FROM orderlist "+
                "WHERE orderlist.driverkey = '" + id + "'";
        if (orderState != null){
            sql = sql + " and orderstate = '" + orderState + "'";
        }
        ResultSet rs = statement.executeQuery(sql);
        if(rs == null) return null;
        while (rs.next()) {
            //将结果集的内容读取出来，存入list中
            Order order = new Order();
            order.setOrderKey(rs.getString("orderkey"));
            order.setResourceKey(rs.getString("resourcekey"));
            order.setDriverKey(rs.getString("driverkey"));
            order.setOrderState(rs.getInt("orderstate"));
            order.setContent(rs.getString("content"));
            order.setEvaluation(rs.getInt("evaluation"));
            //为了保证时分秒不丢失，
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String str_date;
                Date date = null;
                if (rs.getDate("createtime")!=null){
                    str_date = rs.getString("createtime");
                    date = (Date)format.parse(str_date);
                    order.setCreateTime(date);
                }else{
                    order.setCreateTime(rs.getDate("createtime"));
                }

                if (rs.getDate("realloadtime")!=null){
                    str_date = rs.getString("realloadtime");
                    date = (Date)format.parse(str_date);
                    order.setRealLoadTime(date);
                }else{
                    order.setRealLoadTime(rs.getDate("realloadtime"));
                }

                if (rs.getDate("realunloadtime")!=null){
                    str_date = rs.getString("realunloadtime");
                    date = (Date)format.parse(str_date);
                    order.setRealUnloadTime(date);
                }else{
                    order.setRealUnloadTime(rs.getDate("realunloadtime"));
                }

                if (rs.getDate("defaultpaytime")!=null){
                    str_date = rs.getString("defaultpaytime");
                    date = (Date)format.parse(str_date);
                    order.setDefaultPayTime(date);
                }else{
                    order.setDefaultPayTime(rs.getDate("defaultpaytime"));
                }

                if (rs.getDate("realpaytime")!=null){
                    str_date = rs.getString("realpaytime");
                    date = (Date)format.parse(str_date);
                    order.setRealPayTime(date);
                }else{
                    order.setRealPayTime(rs.getDate("realpaytime"));
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
            order.setDepositState(rs.getInt("depositstate"));
            orderList.add(order);
        }
        closeConnect();
        return orderList;
    }

    @Override
    public List<Order> getOrderListByClient(String clientKey, Integer orderState) throws SQLException {
        String sql;
        List<Order> orderList = new ArrayList<>();
        getDatabase();
        sql = "SELECT orderkey,orderlist.resourcekey,driverkey,orderstate,content,evaluation," +
                "createtime,realloadtime,realunloadtime,defaultpaytime,realpaytime,depositstate "+
                "FROM orderlist,cargoresource "+
                "WHERE clientkey = '"+ clientKey +"' and orderstate = '" + orderState + "' and " +
                "orderlist.resourcekey = cargoresource.resourcekey";
        ResultSet rs = statement.executeQuery(sql);
        if(rs == null) return null;
        while (rs.next()) {
            //将结果集的内容读取出来，存入list中
            Order order = new Order();
            order.setOrderKey(rs.getString("orderkey"));
            order.setResourceKey(rs.getString("resourcekey"));
            order.setDriverKey(rs.getString("driverkey"));
            order.setOrderState(rs.getInt("orderstate"));
            order.setContent(rs.getString("content"));
            order.setEvaluation(rs.getInt("evaluation"));
            //为了保证时分秒不丢失，
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String str_date;
                Date date = null;
                if (rs.getDate("createtime")!=null){
                    str_date = rs.getString("createtime");
                    date = (Date)format.parse(str_date);
                    order.setCreateTime(date);
                }else{
                    order.setCreateTime(rs.getDate("createtime"));
                }

                if (rs.getDate("realloadtime")!=null){
                    str_date = rs.getString("realloadtime");
                    date = (Date)format.parse(str_date);
                    order.setRealLoadTime(date);
                }else{
                    order.setRealLoadTime(rs.getDate("realloadtime"));
                }

                if (rs.getDate("realunloadtime")!=null){
                    str_date = rs.getString("realunloadtime");
                    date = (Date)format.parse(str_date);
                    order.setRealUnloadTime(date);
                }else{
                    order.setRealUnloadTime(rs.getDate("realunloadtime"));
                }

                if (rs.getDate("defaultpaytime")!=null){
                    str_date = rs.getString("defaultpaytime");
                    date = (Date)format.parse(str_date);
                    order.setDefaultPayTime(date);
                }else{
                    order.setDefaultPayTime(rs.getDate("defaultpaytime"));
                }

                if (rs.getDate("realpaytime")!=null){
                    str_date = rs.getString("realpaytime");
                    date = (Date)format.parse(str_date);
                    order.setRealPayTime(date);
                }else{
                    order.setRealPayTime(rs.getDate("realpaytime"));
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
            order.setDepositState(rs.getInt("depositstate"));
            orderList.add(order);
        }
        closeConnect();
        return orderList;
    }

    @Override
    public void deleteOrder(String orderKey) throws SQLException {
        getDatabase();
        String sql = "DELETE FROM orderlist WHERE orderkey = '" + orderKey + "'";
        statement.executeUpdate(sql);
        closeConnect();
    }

    @Override
    public void setOrderEvaluation(String orderKey, Integer evaluation, String content) throws SQLException {
        getDatabase();
        String sql = "UPDATE orderlist SET orderstate = '4' , evaluation = '" + evaluation +
                "' , content = '" + content + "' " +
                "WHERE orderkey = '" + orderKey + "'";
        statement.executeUpdate(sql);
        closeConnect();
    }
}
