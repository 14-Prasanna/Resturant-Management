package org.restaurant.model.checkout;

import org.restaurant.model.cart.Cart.CartItem;

import java.util.List;

/**
 * Checkout model — a lightweight snapshot of the cart items and
 * calculated totals produced during the checkout step.
 *
 * Fields only. No business logic (consistent with Order, Cart, MenuItem).
 */
public class Checkout {

    private static final double TAX_RATE            = 0.05;   // 5% GST
    private static final double DELIVERY_CHARGE     = 40.00;  // flat ₹40
    private static final double FREE_DELIVERY_ABOVE = 500.00; // free if subtotal > ₹500

    private String         customerId;
    private List<CartItem> items;
    private double         subtotal;
    private double         tax;
    private double         deliveryCharge;
    private double         grandTotal;

    public Checkout(String customerId, List<CartItem> items) {
        this.customerId = customerId;
        this.items      = items;

        // All totals are computed once at construction time
        this.subtotal       = items.stream().mapToDouble(CartItem::getTotalPrice).sum();
        this.tax            = subtotal * TAX_RATE;
        this.deliveryCharge = (subtotal > FREE_DELIVERY_ABOVE) ? 0.0 : DELIVERY_CHARGE;
        this.grandTotal     = subtotal + tax + deliveryCharge;
    }

    // --- Getters ---
    public String         getCustomerId()    { return customerId;    }
    public List<CartItem> getItems()         { return items;         }
    public double         getSubtotal()      { return subtotal;      }
    public double         getTax()           { return tax;           }
    public double         getDeliveryCharge(){ return deliveryCharge;}
    public double         getGrandTotal()    { return grandTotal;    }
}