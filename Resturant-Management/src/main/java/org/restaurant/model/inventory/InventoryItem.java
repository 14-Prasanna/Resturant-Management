package org.restaurant.model.inventory;

public class InventoryItem {
    private String productId;
    private String name;
    private String unit;
    private double price;
    private int quantity;

    public InventoryItem(String productId, String name, String unit,
                         double price, int quantity) {
        this.productId   = productId;
        this.name        = name;
        this.unit        = unit;
        this.price       = price;
        this.quantity    = quantity;
    }

    public String getProductId()   { return productId; }
    public String getName()        { return name; }
    public String getUnit()        { return unit; }
    public double getPrice()       { return price; }
    public int getQuantity()       { return quantity; }

    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPrice(double price)    { this.price = price; }
    public void setName(String name)      { this.name = name; }
    public void setUnit(String unit)      { this.unit = unit; }
}