package org.restaurant.controller.checkout;

import org.restaurant.controller.payment.PaymentController;
import org.restaurant.model.cart.Cart.CartItem;
import org.restaurant.model.checkout.Checkout;
import org.restaurant.model.order.Order;
import org.restaurant.service.cart.CartService;
import org.restaurant.service.checkout.CheckoutService;
import org.restaurant.service.order.OrderService;

import java.util.List;
import java.util.Scanner;

/**
 * CheckoutController — handles the checkout flow.
 *
 * Flow it owns:
 *   1. Call CheckoutService.prepareCheckout() to build the summary.
 *   2. Display cart items + full price breakdown to the customer.
 *   3. Ask for confirmation.
 *   4. On confirmation → call OrderService.placeOrder() (reusing existing method).
 *   5. Hand the created Order to PaymentController to complete payment.
 *
 * This controller deliberately does NOT duplicate the cart display logic
 * already in CartController. It reuses the Checkout model (which already
 * contains the calculated totals) and displays it cleanly.
 */
public class CheckoutController {

    private static final double TAX_RATE            = 0.05;
    private static final double FREE_DELIVERY_ABOVE = 500.00;

    private Scanner           scanner;
    private CheckoutService   checkoutService;
    private OrderService      orderService;
    private PaymentController paymentController;

    public CheckoutController(Scanner scanner,
                              CheckoutService checkoutService,
                              OrderService orderService,
                              PaymentController paymentController) {
        this.scanner           = scanner;
        this.checkoutService   = checkoutService;
        this.orderService      = orderService;
        this.paymentController = paymentController;
    }

    // =========================================================================
    // CHECKOUT ENTRY POINT
    // =========================================================================

    /**
     * Full checkout flow: display summary → confirm → place order → pay.
     */
    public void startCheckout(String customerId) {

        // Step 1: Prepare checkout summary from cart
        Checkout checkout = checkoutService.prepareCheckout(customerId);

        if (checkout == null) {
            System.out.println("\nYour cart is empty. Please add items before checking out.");
            return;
        }

        // Step 2: Display checkout summary
        displayCheckoutSummary(checkout);

        // Step 3: Ask for confirmation
        System.out.print("\nProceed to checkout? (yes/no): ");
        String confirm = scanner.nextLine().trim();

        if (!confirm.equalsIgnoreCase("yes")) {
            System.out.println("Checkout cancelled. Returning to dashboard.");
            return;
        }

        // Step 4: Place the order — reuse existing OrderService.placeOrder()
        String orderResult = orderService.placeOrder(customerId);

        // OrderService returns "Order placed successfully" on success
        // or an error message if the cart became empty
        if (!orderResult.toLowerCase().contains("successfully")) {
            System.out.println(orderResult);
            return;
        }

        // Step 5: Retrieve the just-placed order (last order for this customer)
        List<Order> orders = orderService.getOrdersByCustomer(customerId);
        if (orders.isEmpty()) {
            System.out.println("Something went wrong retrieving your order. Please contact support.");
            return;
        }

        // The most recently placed order is the last one in the list
        Order placedOrder = orders.get(orders.size() - 1);

        System.out.println("\nOrder ID   : " + placedOrder.getOrderId());
        System.out.println("Order Total: ₹" + String.format("%.2f", placedOrder.getTotalAmount()));
        System.out.println("Status     : " + placedOrder.getStatus());

        // Step 6: Hand off to PaymentController
        paymentController.processPayment(placedOrder, customerId);
    }

    // =========================================================================
    // DISPLAY HELPERS
    // =========================================================================

    /**
     * Prints the checkout summary — item table + full cost breakdown.
     * Uses data from the Checkout model (totals already calculated).
     */
    private void displayCheckoutSummary(Checkout checkout) {
        System.out.println("\n========================================");
        System.out.println("           CHECKOUT SUMMARY             ");
        System.out.println("========================================");

        System.out.printf("%-12s %-20s %-12s %8s %5s %12s%n",
                "Product ID", "Name", "Meal Time", "Price", "Qty", "Subtotal");
        System.out.println("-".repeat(75));

        for (CartItem item : checkout.getItems()) {
            System.out.printf("%-12s %-20s %-12s %8.2f %5d %12.2f%n",
                    item.getProductId(),
                    item.getName(),
                    item.getMealTime(),
                    item.getPrice(),
                    item.getQuantity(),
                    item.getTotalPrice());
        }

        System.out.println("-".repeat(75));
        System.out.printf("%-60s ₹%10.2f%n", "Subtotal",          checkout.getSubtotal());
        System.out.printf("%-60s ₹%10.2f%n", "Tax (GST 5%)",      checkout.getTax());
        System.out.printf("%-60s ₹%10.2f%n",
                checkout.getDeliveryCharge() == 0.0
                        ? "Delivery Charge (FREE – order above ₹" + FREE_DELIVERY_ABOVE + ")"
                        : "Delivery Charge",
                checkout.getDeliveryCharge());
        System.out.println("=".repeat(75));
        System.out.printf("%-60s ₹%10.2f%n", "GRAND TOTAL",       checkout.getGrandTotal());
        System.out.println("=".repeat(75));
    }
}