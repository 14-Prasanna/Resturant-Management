package org.restaurant.model.cart;

import java.util.ArrayList;
import java.util.List;

/**
 * Cart model — holds the customer's active shopping session.
 *
 * CartItem is defined as a static nested class inside Cart so that
 * both model classes live in a single file. CartItem has no meaning
 * outside the context of a Cart, making this a clean, cohesive design.
 */
public class Cart {

    // =========================================================================
    // INNER MODEL: CartItem
    // =========================================================================

    /**
     * Represents a single line-item inside a customer's cart.
     * Fields are set at construction time from the matching MenuItem;
     * only quantity is mutable (for update operations).
     */
    public static class CartItem {

        private String productId;
        private String name;
        private String mealTime;
        private double price;
        private int    quantity;

        public CartItem(String productId, String name, String mealTime,
                        double price, int quantity) {
            this.productId = productId;
            this.name      = name;
            this.mealTime  = mealTime;
            this.price     = price;
            this.quantity  = quantity;
        }

        // --- Getters ---
        public String getProductId() { return productId; }
        public String getName()      { return name; }
        public String getMealTime()  { return mealTime; }
        public double getPrice()     { return price; }
        public int    getQuantity()  { return quantity; }

        // --- Setter (quantity is the only field customers can change) ---
        public void setQuantity(int quantity) { this.quantity = quantity; }

        /** Convenience: price × quantity for this line item. */
        public double getTotalPrice() { return price * quantity; }
    }

    // =========================================================================
    // CART FIELDS
    // =========================================================================

    private String         customerId;   // username of the logged-in customer
    private List<CartItem> items;

    public Cart(String customerId) {
        this.customerId = customerId;
        this.items      = new ArrayList<>();
    }

    // --- Getters ---
    public String         getCustomerId() { return customerId; }
    public List<CartItem> getItems()      { return items; }
}
