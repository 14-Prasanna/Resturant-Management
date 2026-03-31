package org.restaurant.service.checkout;

import org.restaurant.model.cart.Cart.CartItem;
import org.restaurant.model.checkout.Checkout;
import org.restaurant.repository.checkout.CheckoutRepository;
import org.restaurant.service.cart.CartService;

import java.util.List;
import java.util.UUID;

public class CheckoutService {

    private CartService        cartService;
    private CheckoutRepository checkoutRepository = new CheckoutRepository();

    public CheckoutService(CartService cartService) {
        this.cartService = cartService;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PREPARE CHECKOUT  –  validate → build → persist as PENDING
    // ─────────────────────────────────────────────────────────────────────────
    public Checkout prepareCheckout(String customerId,
                                    String customerName,
                                    String email,
                                    String address) {

        // Input validation
        if (customerName == null || customerName.isBlank()) {
            System.out.println("❌ Name cannot be empty.");
            return null;
        }
        if (email == null || !email.contains("@") || !email.contains(".")) {
            System.out.println("❌ Invalid email address.");
            return null;
        }
        if (address == null || address.isBlank()) {
            System.out.println("❌ Delivery address cannot be empty.");
            return null;
        }

        List<CartItem> cartItems = cartService.getCartItems(customerId);
        if (cartItems == null || cartItems.isEmpty()) {
            System.out.println("❌ Cart is empty. Add items before checkout.");
            return null;
        }

        String   checkoutId = "CHK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Checkout checkout   = new Checkout(checkoutId, customerId, customerName,
                email, address, cartItems);

        boolean saved = checkoutRepository.save(checkout);
        if (!saved) {
            System.out.println("❌ Could not save checkout. Please try again.");
            return null;
        }

        return checkout;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STATUS UPDATES  –  called by CheckoutController after payment result
    // ─────────────────────────────────────────────────────────────────────────
    public void markCompleted(String checkoutId) {
        checkoutRepository.updateStatus(checkoutId, "COMPLETED");
    }

    public void markFailed(String checkoutId) {
        checkoutRepository.updateStatus(checkoutId, "FAILED");
    }
}
