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
                                    String address,
                                    String city,
                                    String state,
                                    String pincode,
                                    String phone,
                                    Integer discountId,
                                    double  discountAmount) {

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
        if (city == null || city.isBlank() || phone == null || phone.isBlank()) {
            System.out.println("❌ City and Phone are required.");
            return null;
        }

        List<CartItem> cartItems = cartService.getCartItems(customerId);
        if (cartItems == null || cartItems.isEmpty()) {
            System.out.println("❌ Cart is empty. Add items before checkout.");
            return null;
        }

        String   checkoutId = "CHK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new Checkout(checkoutId, customerId, customerName,
                email, address, city, state, pincode, phone, cartItems, discountId, discountAmount);
    }

    public boolean saveCheckout(Checkout checkout) {
        String savedId = checkoutRepository.save(checkout);
        if (savedId == null) {
            System.out.println("❌ Could not save checkout. Please try again.");
            return false;
        }
        checkout.setCheckoutId(savedId);
        return true;
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
