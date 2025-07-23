package model;

public class Item {
    private String menu;
    private int price;
    private String barcode;
    private int date;

    public Item(String menu, int price, String barcode, int date) {
        this.menu = menu;
        this.price = price;
        this.barcode = barcode;
        this.date = date;
    }

    // Getter/Setter
    public String getMenu() { return menu; }
    public void setMenu(String menu) { this.menu = menu; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public int getDate() { return date; }
    public void setDate(int date) { this.date = date; }

    @Override
    public String toString() {
        return String.format("%s / %d / %s / %d", menu, price, barcode, date);
    }
}

