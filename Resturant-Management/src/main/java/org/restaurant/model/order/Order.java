package org.restaurant.model.order;

import org.restaurant.model.cart.Cart.CartItem;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Order {

    private String         orderId;
    private String         customerId;
    private String         checkoutId;
    private List<CartItem> items;
    private double         totalAmount;
    private String         status;
    private String         placedAt;

    // Constructor for creating a NEW order
    public Order(String orderId, String customerId, List<CartItem> items, double totalAmount) {
        this.orderId     = orderId;
        this.customerId  = customerId;
        this.checkoutId  = null;
        this.items       = items;
        this.totalAmount = totalAmount;
        this.status      = "PLACED";
        this.placedAt    = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // Constructor for reading back FROM DB
    public Order(String orderId, String customerId, List<CartItem> items,
                 double totalAmount, String status, String checkoutId, String placedAt) {
        this.orderId     = orderId;
        this.customerId  = customerId;
        this.checkoutId  = checkoutId;
        this.items       = items;
        this.totalAmount = totalAmount;
        this.status      = status;
        this.placedAt    = placedAt;
    }

    public String         getOrderId()    { return orderId;     }
    public String         getCustomerId() { return customerId;  }
    public String         getCheckoutId() { return checkoutId;  }
    public List<CartItem> getItems()      { return items;       }
    public double         getTotalAmount(){ return totalAmount; }
    public double         getTotal()      { return totalAmount; }
    public String         getStatus()     { return status;      }
    public String         getPlacedAt()   { return placedAt;    }

    public void setCheckoutId(String checkoutId) { this.checkoutId = checkoutId; }
    public void setStatus(String status)         { this.status = status;         }
}
