package com.scistor.tab.auth.util;

import java.sql.*;

/**
 * User: CaoJian
 * Date: 2015/7/10
 * Time: 17:42
 */
public class DBUtils {
    private Connection conn;

    /**
     * 获取数据库连接
     */
    public static Connection getConnection(String driver, String url, String username, String password) {
        Connection conn = null;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  
        } catch (SQLException e) {
            e.printStackTrace();  
        }
        return conn;  
    }

    /**
     * 获取表的记录数
     */
    public static Long getTableRecordNum(String driver, String url,  String userName, String password, String tableName) throws SQLException {
        Connection connection = getConnection(driver, url, userName, password);
        Statement  stmt = connection.createStatement();
        String sql = "select * from " + tableName;
        ResultSet rs = stmt.executeQuery(sql);
        Long count = 0L;
        for(;rs.next(); count++);
        close(connection);
        return count;
    }

    /**
     * 关闭数据库连接
     */
    public static void close(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            conn = null;
            e.printStackTrace();  // To change body of catch statement use File | Settings | File Templates.
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                conn = null;
                e.printStackTrace();  // To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

}
