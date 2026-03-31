package org.restaurant.model.checkout;

import org.restaurant.model.cart.Cart.CartItem;
import java.util.List;

public class Checkout {

    private static final double TAX_RATE            = 0.05;
    private static final double DELIVERY_CHARGE     = 40.00;
    private static final double FREE_DELIVERY_ABOVE = 500.00;

    private String         checkoutId;
    private String         customerId;
    private String         customerName;
    private String         email;
    private String         address;
    private List<CartItem> items;
    private double         subtotal;
    private double         tax;
    private double         deliveryCharge;
    private double         grandTotal;

    public Checkout(String checkoutId, String customerId, String customerName,
                    String email, String address, List<CartItem> items) {
        this.checkoutId   = checkoutId;
        this.customerId   = customerId;
        this.customerName = customerName;
        this.email        = email;
        this.address      = address;
        this.items        = items;

        this.subtotal       = items.stream().mapToDouble(CartItem::getTotalPrice).sum();
        this.tax            = subtotal * TAX_RATE;
        this.deliveryCharge = (subtotal > FREE_DELIVERY_ABOVE) ? 0.0 : DELIVERY_CHARGE;
        this.grandTotal     = subtotal + tax + deliveryCharge;
    }

    public String         getCheckoutId()    { return checkoutId;    }
    public String         getCustomerId()    { return customerId;    }
    public String         getCustomerName()  { return customerName;  }
    public String         getEmail()         { return email;         }
    public String         getAddress()       { return address;       }
    public List<CartItem> getItems()         { return items;         }
    public double         getSubtotal()      { return subtotal;      }
    public double         getTax()           { return tax;           }
    public double         getDeliveryCharge(){ return deliveryCharge;}
    public double         getGrandTotal()    { return grandTotal;    }
}