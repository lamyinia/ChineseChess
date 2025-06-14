package org.com.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLTool {
    private static final String URL = "jdbc:mysql://localhost:3306/chinese_chess?useSSL=false&useUnicode=true&characterEncoding=utf8";
    private static final String USER = "chess_user";
    private static final String PASSWORD = "@Ly135790";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
