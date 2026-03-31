package org.restaurant.controller.checkout;

import org.restaurant.controller.payment.PaymentController;
import org.restaurant.model.cart.Cart.CartItem;
import org.restaurant.service.cart.CartService;
import org.restaurant.service.checkout.CheckoutService;
import org.restaurant.service.order.OrderService;
import java.util.List;
import java.util.Scanner;

public class CheckoutController {
    private Scanner scanner;
    private CheckoutService checkoutService;
    private CartService cartService;
    private OrderService orderService;
    private PaymentController paymentController;

    public CheckoutController(Scanner scanner,
                               CheckoutService checkoutService,
                               CartService cartService,
                               OrderService orderService,
                               PaymentController paymentController) {
        this.scanner           = scanner;
        this.checkoutService   = checkoutService;
        this.cartService       = cartService;
        this.orderService      = orderService;
        this.paymentController = paymentController;
    }

    public void startCheckout(String customerId) {
        List<CartItem> cartItems = checkoutService.getCartItems(customerId);

        if (cartItems.isEmpty()) {
            System.out.println("Your cart is empty! Add items first.");
            return;
        }

        // Show order summary
        System.out.println("\n--- ORDER SUMMARY ---");
        for (CartItem item : cartItems) {
            System.out.println("- " + item.getName()
                    + " x" + item.getQuantity()
                    + " | ₹" + item.getTotalPrice());
        }

        double total = checkoutService.calculateTotal(customerId);
        System.out.println("---------------------");
        System.out.println("Total: ₹" + total);

        // Step 1 - Process Payment
        boolean paid = paymentController.processPayment(customerId, cartItems, total);

        if (paid) {
            // Step 2 - Place Order using placeOrder() ✅
            String result = orderService.placeOrder(customerId);
            System.out.println(result);
            System.out.println("Thank you for your order!");
        } else {
            System.out.println("Payment failed. Please try again.");
        }
    }
}