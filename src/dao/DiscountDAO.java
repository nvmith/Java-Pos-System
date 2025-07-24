
package dao;

import util.DBUtil;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DiscountDAO
{
    public DiscountDAO() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists()
    {
        String sql = "CREATE TABLE IF NOT EXISTS discount_table (" +
                "barcode VARCHAR(50) PRIMARY KEY," +
                "type VARCHAR(10)" +
                ");";

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

    public boolean insertDiscount(String barcode, String type)
    {
        String sql = "REPLACE INTO discount_table (barcode, type) VALUES (?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, barcode);
            pstmt.setString(2, type);
            return pstmt.executeUpdate() > 0;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public String getDiscountType(String barcode)
    {
        String sql = "SELECT type FROM discount_table WHERE barcode = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, barcode);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("type");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return "NONE";
    }

    public Map<String, String> getAllDiscounts()
    {
        Map<String, String> map = new HashMap<>();
        String sql = "SELECT * FROM discount_table";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql))
        {
            while (rs.next()) {
                map.put(rs.getString("barcode"), rs.getString("type"));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return map;
    }

    public boolean deleteByBarcode(String barcode)
    {
        String sql = "DELETE FROM discount_table WHERE barcode = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, barcode);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean clearAll()
    {
        String sql = "DELETE FROM discount_table";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
