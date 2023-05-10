package com.example.vehicle_and_cargo_matching_system.dao.impl;

import android.util.Log;

import com.example.vehicle_and_cargo_matching_system.bean.Driver;
import com.example.vehicle_and_cargo_matching_system.dao.DriverDao;
import com.example.vehicle_and_cargo_matching_system.util.DBUtil;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DriverDaoImpl extends DBUtil implements DriverDao {
    @Override
    public Driver getDriver(String id) throws SQLException {
        Driver driver = new Driver();
        getDatabase();
        String sql = "SELECT phone,userpassword,IDnumber,surname,givenname,licenseID,registrationID," +
                "cartype,equipment,uselength,innerlength,innerwidth,innerhigh "+
                "FROM driver " +
                "WHERE phone = '" + id +"'";
        ResultSet rs = statement.executeQuery(sql);
        if(rs == null) return null;
        while (rs.next()) {
            driver.setPhone(rs.getString("phone"));
            driver.setPassword(rs.getString("userpassword"));
            driver.setIdNumber(rs.getString("IDnumber"));
            driver.setSurName(rs.getString("surname"));
            driver.setGivenName(rs.getString("givenname"));
            driver.setLicenseId(rs.getString("licenseID"));
            driver.setRegistrationId(rs.getString("registrationID"));
            driver.setCarType(rs.getString("cartype"));
            driver.setEquipment(rs.getString("equipment"));
            driver.setUseLength(rs.getBigDecimal("uselength"));
            driver.setUseLength(rs.getBigDecimal("uselength"));
            driver.setInnerLength(rs.getBigDecimal("innerlength"));
            driver.setInnerWidth(rs.getBigDecimal("innerwidth"));
            driver.setInnerHigh(rs.getBigDecimal("innerhigh"));
        }
        closeConnect();
        return driver;
    }

    @Override
    public int addDriver(Driver driver) throws SQLException {
        int result = 0;
        getDatabase();
        String sql = "INSERT INTO driver VALUES ('"+driver.getPhone() +"','"+ driver.getPassword()
                +"','"+ driver.getIdNumber() +"','"+ driver.getSurName()+"','"+driver.getGivenName()
                +"','"+  driver.getLicenseId() +"','"+ driver.getRegistrationId() + "')";
        result = statement.executeUpdate(sql);
        closeConnect();
        return result;
    }

    @Override
    public int setCarInformation(String id, String car_type, String use_length, BigDecimal inner_length,
             BigDecimal inner_width, BigDecimal inner_high, String equipment) throws SQLException {
        int result = 0;
        getDatabase();
        String sql = "UPDATE driver SET cartype = '" + car_type + "', uselength = '" + use_length +
                "',equipment = '"+equipment+"',innerlength = '"+inner_length+"',innerwidth = '"+
                inner_width + "',innerhigh = '" + inner_high+"' " +
                "WHERE phone = '" + id + "'";
        Log.i("sql",sql);
        result = statement.executeUpdate(sql);
        closeConnect();
        return result;
    }

    @Override
    public int setPassword(String id,String password) throws SQLException {
        int result = 0;
        getDatabase();
        String sql = "UPDATE driver SET userpassword = '" + password + "' " +
                "WHERE phone = '" + id + "'";
        result = statement.executeUpdate(sql);
        closeConnect();
        return result;
    }
}
