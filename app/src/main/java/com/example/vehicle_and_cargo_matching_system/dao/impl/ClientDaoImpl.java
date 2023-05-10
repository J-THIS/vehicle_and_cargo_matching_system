package com.example.vehicle_and_cargo_matching_system.dao.impl;

import com.example.vehicle_and_cargo_matching_system.bean.Client;
import com.example.vehicle_and_cargo_matching_system.dao.ClientDao;
import com.example.vehicle_and_cargo_matching_system.util.DBUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientDaoImpl extends DBUtil implements ClientDao {

    @Override
    public Client getClient(String clientKey) throws SQLException {
        Client client = new Client();
        getDatabase();
        String sql = "SELECT clientkey,surname,givenname,credit,cancelnum,complaintnum,applausenum," +
                "finishnum,closenum,phone " +
                "FROM client " +
                "WHERE clientkey = '" + clientKey +"'";
        ResultSet rs = statement.executeQuery(sql);
        if(rs == null) return null;
        while (rs.next()) {
            client.setClientKey(rs.getString("clientkey"));
            client.setSurname(rs.getString("surname"));
            client.setGivenName(rs.getString("givenname"));
            client.setCredit(rs.getInt("credit"));
            client.setCancelNum(rs.getInt("cancelnum"));
            client.setComplaintNum(rs.getInt("complaintnum"));
            client.setApplauseNum(rs.getInt("applausenum"));
            client.setFinishNum(rs.getInt("finishnum"));
            client.setCloseNum(rs.getInt("closenum"));
            client.setPhone(rs.getString("phone"));
        }
        closeConnect();
        return client;
    }

    @Override
    public void setApplauseNum(String clientKey) throws SQLException {
        getDatabase();
        int applause_num = 0;
        String sql1 = "SELECT applausenum From client " +
                "WHERE clientkey = '" + clientKey + "'";
        ResultSet rs = statement.executeQuery(sql1);
        if(rs == null) return;
        while (rs.next()) {
            applause_num = rs.getInt("applausenum");
        }
        String sql2 = "UPDATE client SET applausenum = '" + (applause_num+1) + "' " +
                "WHERE clientkey = '" + clientKey + "'";
        statement.executeUpdate(sql2);
        closeConnect();
    }

    @Override
    public void setComplaintNum(String clientKey) throws SQLException {
        getDatabase();
        int complaint_num = 0;
        String sql1 = "SELECT complaintnum From client " +
                "WHERE clientkey = '" + clientKey + "'";
        ResultSet rs = statement.executeQuery(sql1);
        if(rs == null) return;
        while (rs.next()) {
            complaint_num = rs.getInt("complaintnum");
        }
        String sql2 = "UPDATE client SET complaintnum = '" + (complaint_num+1) + "' " +
                "WHERE clientkey = '" + clientKey + "'";
        statement.executeUpdate(sql2);
        closeConnect();
    }
}
