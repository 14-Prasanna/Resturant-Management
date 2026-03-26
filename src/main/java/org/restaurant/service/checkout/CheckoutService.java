package org.restaurant.service.checkout;

import org.restaurant.model.cart.Cart.CartItem;
import org.restaurant.model.checkout.Checkout;
import org.restaurant.service.cart.CartService;

import java.util.List;

/**
 * CheckoutService — orchestrates the checkout step.
 *
 * Responsibilities:
 *   1. Retrieve cart items from CartService (reusing existing method).
 *   2. Build a Checkout model (calculates totals).
 *   3. Return the Checkout to the controller for display and confirmation.
 *
 * CheckoutService does NOT place an order itself. That responsibility
 * belongs to OrderService — CheckoutService only hands back the
 * Checkout object. The controller then calls OrderService after the
 * customer confirms.
 */
public class CheckoutService {

    private CartService cartService;

    public CheckoutService(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Prepares a Checkout summary for the given customer.
     *
     * @return Checkout object if the cart has items, null if the cart is empty.
     */
    public Checkout prepareCheckout(String customerId) {
        // Reuse existing CartService.getCartItems() — no duplication
        List<CartItem> cartItems = cartService.getCartItems(customerId);

        if (cartItems == null || cartItems.isEmpty()) {
            return null;
        }

        return new Checkout(customerId, cartItems);
    }
}