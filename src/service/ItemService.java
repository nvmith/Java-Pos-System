package service;

import dao.ItemDAO;
import model.Item;

import java.util.List;

public class ItemService
{
    private final ItemDAO dao;

    public ItemService()
    {
        this.dao = new ItemDAO();  // 내부에서 DBUtil 통해 Connection 사용
    }

    public boolean addItem(Item item)
    {
        if (dao.isDuplicate(item.getMenu(), item.getBarcode()))
        {
            return false;
        }
        return dao.insertItem(item);
    }

    public boolean updateItem(Item item)
    {
        return dao.updateItem(item);
    }

    public boolean deleteItem(String barcode)
    {
        return dao.deleteItem(barcode);
    }

    public List<Item> getAllItems()
    {
        return dao.getAllItems();
    }
}
