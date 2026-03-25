package org.restaurant.model.order;

import org.restaurant.model.cart.Cart.CartItem;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Order {
    private String orderId;
    private String customerId;
    private List<CartItem> items;     // snapshot of cart at the moment of placing order
    private double totalAmount;
    private String status;
    private String placedAt;

    public Order(String orderId, String customerId, List<CartItem> items, double totalAmount) {
        this.orderId     = orderId;
        this.customerId  = customerId;
        this.items       = items;
        this.totalAmount = totalAmount;
        this.status      = "Placed";
        this.placedAt    = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getOrderId()      { return orderId; }
    public String getCustomerId()   { return customerId; }
    public List<CartItem> getItems(){ return items; }
    public double getTotalAmount()  { return totalAmount; }
    public String getStatus()       { return status; }
    public String getPlacedAt()     { return placedAt; }
    public double getTotal() { return totalAmount; }
}