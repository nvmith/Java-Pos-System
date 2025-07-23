package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil
{
    public static Connection getConnection() throws SQLException
    {
        String url = "jdbc:mysql://localhost:3306/posdb";
        String user = "root";
        String password = "1234";
        return DriverManager.getConnection(url, user, password);
    }
}
