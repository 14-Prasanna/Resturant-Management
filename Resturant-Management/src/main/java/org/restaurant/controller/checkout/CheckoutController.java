package org.restaurant.controller.checkout;

import org.restaurant.controller.payment.PaymentController;
import org.restaurant.model.cart.Cart.CartItem;
import org.restaurant.model.checkout.Checkout;
import org.restaurant.model.order.Order;
import org.restaurant.model.payment.Payment;
import org.restaurant.model.payment.Payment.PaymentStatus;
import org.restaurant.model.discount.Discount;
import org.restaurant.service.cart.CartService;
import org.restaurant.service.checkout.CheckoutService;
import org.restaurant.service.discount.DiscountService;
import org.restaurant.service.order.OrderService;
import org.restaurant.service.payment.PaymentService;

import java.util.Scanner;
import java.util.List;

public class CheckoutController {

    private static final double FREE_DELIVERY_ABOVE = 500.00;

    private Scanner           scanner;
    private CheckoutService   checkoutService;
    private OrderService      orderService;
    private PaymentController paymentController;
    private PaymentService    paymentService;
    private CartService       cartService;
    private DiscountService   discountService;

    public CheckoutController(Scanner scanner,
                              CheckoutService checkoutService,
                              OrderService orderService,
                              PaymentController paymentController,
                              PaymentService paymentService,
                              CartService cartService,
                              DiscountService discountService) {
        this.scanner           = scanner;
        this.checkoutService   = checkoutService;
        this.orderService      = orderService;
        this.paymentController = paymentController;
        this.paymentService    = paymentService;
        this.cartService       = cartService;
        this.discountService   = discountService;
    }

    // =========================================================================
    // FULL CHECKOUT FLOW
    // =========================================================================
    public void startCheckout(String customerId) {

        List<CartItem> cartItems = cartService.getCartItems(customerId);
        if (cartItems == null || cartItems.isEmpty()) {
            System.out.println("\n❌ Your cart is empty! Please add some items before checking out.");
            return;
        }

        // Step 1 — Collect delivery details
        System.out.println("\n========================================");
        System.out.println("         DELIVERY DETAILS               ");
        System.out.println("========================================");
        System.out.print("Your Name    : ");
        String name    = scanner.nextLine().trim();
        System.out.print("Email        : ");
        String email   = scanner.nextLine().trim();
        System.out.print("Address      : ");
        String address = scanner.nextLine().trim();
        System.out.print("City         : ");
        String city = scanner.nextLine().trim();
        System.out.print("State        : ");
        String state = scanner.nextLine().trim();
        System.out.print("Pincode      : ");
        String pincode = scanner.nextLine().trim();
        System.out.print("Phone        : ");
        String phone = scanner.nextLine().trim();

        Integer discountId = null;
        double  discountAmt = 0.0;

        while (true) {
            System.out.print("Discount Code (leave blank to skip): ");
            String discountCode = scanner.nextLine().trim();
            if (discountCode.isEmpty()) {
                break;
            }

            double cartSub = cartService.getCartItems(customerId).stream().mapToDouble(CartItem::getTotalPrice).sum();
            Discount d = discountService.getValidDiscount(discountCode, cartSub);

            if (d != null) {
                discountId = d.getId();
                discountAmt = cartSub * (d.getDiscountPercent() / 100.0);
                System.out.printf("✅ Discount applied: ₹%.2f OFF%n", discountAmt);
                break;
            } else {
                System.out.println("❌ Invalid, expired, or inapplicable discount code.");
                System.out.println("Please try again or leave blank to skip.");
            }
        }

        // Step 2 — Validate but do NOT save yet
        Checkout checkout = checkoutService.prepareCheckout(customerId, name, email, address, city, state, pincode, phone, discountId, discountAmt);
        if (checkout == null) return;

        // Step 3 — Show full summary
        displayCheckoutSummary(checkout);

        // Step 4 — Confirm
        System.out.print("\nProceed to checkout? (yes/no): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("yes")) {
            System.out.println("Checkout cancelled.");
            // Not saved yet, so no need to mark failed
            return;
        }

        // Save now that it is confirmed
        if (!checkoutService.saveCheckout(checkout)) {
            return;
        }

        // Step 5 — Place order (saves to orders + order_items in DB transaction)
        String result = orderService.placeOrder(customerId, checkout.getCheckoutId());
        if (!result.startsWith("Order placed successfully")) {
            System.out.println(result);
            checkoutService.markFailed(checkout.getCheckoutId());
            return;
        }

        String orderId = result.split("\\|")[1];
        Order  order   = orderService.getOrderById(orderId);
        if (order == null) {
            System.out.println("Something went wrong. Please contact support.");
            return;
        }

        System.out.println("\nOrder ID   : " + order.getOrderId());
        System.out.printf ("Order Total: ₹%.2f%n", order.getTotalAmount());
        System.out.println("Status     : " + order.getStatus());

        // ✅ Clear cart immediately when order status is PLACED (removed from DB + session)
        if ("PLACED".equalsIgnoreCase(order.getStatus())) {
            cartService.clearCart(customerId);
            System.out.println("🛒 Cart cleared — order has been placed successfully.");
        }

        // Step 6 — Payment
        paymentController.processPayment(order, customerId);

        // Step 7 — Mark checkout COMPLETED or FAILED based on payment outcome
        Payment payment = paymentService.getPaymentByOrderId(orderId);
        if (payment != null && payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            checkoutService.markCompleted(checkout.getCheckoutId());

            if (checkout.getDiscountId() != null) {
                discountService.recordDiscountUsed(checkout.getDiscountId());
            }
        } else {
            checkoutService.markFailed(checkout.getCheckoutId());
        }
    }

    // =========================================================================
    // DISPLAY
    // =========================================================================
    private void displayCheckoutSummary(Checkout checkout) {
        System.out.println("\n========================================");
        System.out.println("           CHECKOUT SUMMARY             ");
        System.out.println("========================================");
        System.out.println("Name    : " + checkout.getCustomerName());
        System.out.println("Email   : " + checkout.getEmail());
        System.out.println("Address : " + checkout.getAddress() + ", " + checkout.getCity() + " " + checkout.getState() + " - " + checkout.getPincode());
        System.out.println("Phone   : " + checkout.getPhone());
        System.out.println();

        System.out.printf("%-12s %-20s %-12s %8s %5s %12s%n",
                "Product ID", "Name", "Meal Time", "Price", "Qty", "Subtotal");
        System.out.println("-".repeat(75));

        for (CartItem item : checkout.getItems()) {
            System.out.printf("%-12s %-20s %-12s %8.2f %5d %12.2f%n",
                    item.getProductId(), item.getName(), item.getMealTime(),
                    item.getPrice(), item.getQuantity(), item.getTotalPrice());
        }

        System.out.println("-".repeat(75));
        System.out.printf("%-60s ₹%10.2f%n", "Subtotal",     checkout.getSubtotal());
        System.out.printf("%-60s ₹%10.2f%n", "Tax (GST 5%)", checkout.getTax());
        System.out.printf("%-60s ₹%10.2f%n",
                checkout.getDeliveryCharge() == 0.0
                        ? "Delivery (FREE – order above ₹" + FREE_DELIVERY_ABOVE + ")"
                        : "Delivery Charge",
                checkout.getDeliveryCharge());

        if (checkout.getDiscountId() != null) {
            System.out.printf("%-60s-₹%10.2f%n", "Discount Applied", checkout.getDiscountAmount());
        }
        System.out.println("=".repeat(75));
        System.out.printf("%-60s ₹%10.2f%n", "GRAND TOTAL (Final Amount)",  checkout.getFinalAmount());
        System.out.println("=".repeat(75));
    }
}