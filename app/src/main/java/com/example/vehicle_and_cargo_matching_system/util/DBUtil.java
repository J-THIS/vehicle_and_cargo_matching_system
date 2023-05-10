package com.example.vehicle_and_cargo_matching_system.util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import android.util.Log;
import java.sql.ResultSet;
import java.sql.SQLException;



public class DBUtil {


    private static String user = "sa";//数据库登录账号
    private static String password = "heyang175116";//登录密码
    private static String DatabaseName = "VCMS";//数据库名称
    private static String IP = "192.168.28.118";//数据库IP（也可写本机ip）


    /**
     * 连接字符串
     */
    private static String connectDB = "jdbc:jtds:sqlserver://" + IP + ":1433/" + DatabaseName + ";useunicode=true;characterEncoding=UTF-8";
    private static Connection connection = null;
    protected static Statement statement = null;

    /**
     * 连接数据库
     *
     * @return
     */
    protected static void getDatabase() {
        try {
            //加载驱动
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            //连接数据库对象
            connection = DriverManager.getConnection(connectDB, user,
                    password);
            statement=connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 向服务器数据库插入数据
     */
    protected static int insertIntoData(String values) {
        int result = 0;
        try {
            //插入sql语句（Tb_Id 为表名，id为要插入的字段名）
            String sql = "INSERT INTO Tb_Id (id) VALUES ('"+values +"')";
            result = statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result ;
    }


    //数据查询方法
    protected static String search() {
        String result = "";
        try {
            String sqlStr = "select id,userName from Tb_Id";
            ResultSet rs = statement.executeQuery(sqlStr);

            while (rs.next()) {
                //将查出的内容读取出来，存入字符串中
                String idStr = rs.getString("id");
                String nameStr = rs.getString("userName");
                result += "\n"+ idStr +"----"+nameStr ;
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return result;
    }


    //数据更新和删除方法
    protected static int update() {
        int result = 0;
        try {
            //数据更新sql语句
            //String sqlStr = "update Tb_Id set id = '110' where id = '0001'";
            //数据删除sql语句
            String sqlStr = "delete from Tb_Id where id = '0001'";

            result = statement.executeUpdate(sqlStr);

        } catch (SQLException e) {
            e.printStackTrace();

        }
        return result;
    }



    /**
     * 关闭数据库链接
     */
    protected static void closeConnect() {
        if (statement != null) {
            try {
                statement.close();
                statement = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
