package dao;

import model.Item;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO
{
    public ItemDAO() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = """
        CREATE TABLE IF NOT EXISTS item_table (
            name VARCHAR(50),
            price INT,
            barcode VARCHAR(50) PRIMARY KEY,
            date INT
        );
    """;

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean insertItem(Item item)
    {
        String sql = "INSERT INTO item_table (name, price, barcode, date) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, item.getMenu());     // getMenu()
            pstmt.setInt(2, item.getPrice());
            pstmt.setString(3, item.getBarcode());
            pstmt.setInt(4, item.getDate());        // getDate()
            return pstmt.executeUpdate() > 0;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateItem(Item item)
    {
        String sql = "UPDATE item_table SET name = ?, price = ?, date = ? WHERE barcode = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, item.getMenu());     // getMenu()
            pstmt.setInt(2, item.getPrice());
            pstmt.setInt(3, item.getDate());        // getDate()
            pstmt.setString(4, item.getBarcode());
            return pstmt.executeUpdate() > 0;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteItem(String barcode)
    {
        String sql = "DELETE FROM item_table WHERE barcode = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, barcode);
            return pstmt.executeUpdate() > 0;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public List<Item> getAllItems()
    {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM item_table";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql))
        {
            while (rs.next())
            {
                Item item = new Item(
                        rs.getString("name"),            // menu
                        rs.getInt("price"),
                        rs.getString("barcode"),
                        rs.getInt("date")                // date
                );
                items.add(item);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return items;
    }

    public boolean isDuplicate(String name, String barcode)
    {
        String sql = "SELECT COUNT(*) FROM item_table WHERE name = ? OR barcode = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, name);
            pstmt.setString(2, barcode);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                return rs.getInt(1) > 0;
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return false;
    }
}
