package dao;

import util.DBUtil;

import java.sql.*;

public class OrderDAO
{
    public OrderDAO() {
        createTableIfNotExists();
    }

    // orders 테이블 생성 (존재하지 않을 경우만)
    private void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS orders (
                id INT AUTO_INCREMENT PRIMARY KEY,
                menu_name VARCHAR(100),
                price INT,
                quantity INT,
                total_price INT,
                order_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement())
        {
            stmt.execute(sql);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    // 주문 내역 추가
    public boolean insertOrder(String menuName, int price, int quantity)
    {
        String sql = "INSERT INTO orders (menu_name, price, quantity, total_price) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, menuName);
            pstmt.setInt(2, price);
            pstmt.setInt(3, quantity);
            pstmt.setInt(4, price * quantity);
            return pstmt.executeUpdate() > 0;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    // 총 시재(매출 총합) 조회
    public int getTotalRevenue()
    {
        int total = 0;
        String sql = "SELECT SUM(total_price) FROM orders";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery())
        {
            if (rs.next())
            {
                total = rs.getInt(1);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return total;
    }

    public boolean clearAll()
    {
        String sql = "DELETE FROM orders";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement())
        {
            stmt.executeUpdate(sql);
            return true;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }
}