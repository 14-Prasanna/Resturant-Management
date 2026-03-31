package org.restaurant.service.checkout;

import org.restaurant.model.cart.Cart.CartItem;
import org.restaurant.service.cart.CartService;
import java.util.List;

public class CheckoutService {
    private CartService cartService;

    public CheckoutService(CartService cartService) {
        this.cartService = cartService;
    }

    public List<CartItem> getCartItems(String customerId) {
        return cartService.getCartItems(customerId);
    }

    public double calculateTotal(String customerId) {
        return cartService.getCartItems(customerId)
                .stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
    }

    public void clearCart(String customerId) {
        cartService.clearCart(customerId);
    }
}