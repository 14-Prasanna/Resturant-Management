package org.restaurant.controller.payment;

import org.restaurant.model.order.Order;
import org.restaurant.model.payment.Payment;
import org.restaurant.model.payment.Payment.PaymentMethod;
import org.restaurant.model.payment.Payment.PaymentStatus;
import org.restaurant.service.payment.PaymentService;

import java.util.Scanner;

/**
 * PaymentController — handles payment method selection and displays the result.
 *
 * Flow it owns:
 *   1. Receives the placed Order from CheckoutController.
 *   2. Prompts the customer to choose a payment method.
 *   3. Delegates to PaymentService.processPayment().
 *   4. Prints the payment confirmation or failure message.
 */
public class PaymentController {

    private Scanner        scanner;
    private PaymentService paymentService;

    public PaymentController(Scanner scanner, PaymentService paymentService) {
        this.scanner        = scanner;
        this.paymentService = paymentService;
    }

    // =========================================================================
    // PAYMENT ENTRY POINT  (called by CheckoutController)
    // =========================================================================

    /**
     * Prompts the customer to choose a payment method, then processes payment.
     *
     * @param order      The order that was just placed by CheckoutController.
     * @param customerId The logged-in customer's username.
     */
    public void processPayment(Order order, String customerId) {

        System.out.println("\n--- PAYMENT ---");
        System.out.println("Order ID : " + order.getOrderId());
        System.out.printf ("Amount   : ₹%.2f%n", order.getTotalAmount());
        System.out.println();

        PaymentMethod method = selectPaymentMethod();
        if (method == null) {
            System.out.println("Payment cancelled. Your order is still placed (COD will be assumed at delivery).");
            return;
        }

        System.out.println("\nProcessing " + formatMethodName(method) + " payment...");

        // Simulate a brief processing delay for realism
        simulateProcessingDelay(method);

        // Delegate to PaymentService
        Payment payment = paymentService.processPayment(order.getOrderId(), customerId, method);

        if (payment == null) {
            System.out.println("Payment failed: Order not found. Please contact support.");
            return;
        }

        // Display result
        displayPaymentResult(payment, method);
    }

    // =========================================================================
    // PAYMENT METHOD SELECTION
    // =========================================================================

    /**
     * Displays payment options and returns the chosen PaymentMethod.
     * Returns null if the customer cancels.
     */
    private PaymentMethod selectPaymentMethod() {
        while (true) {
            System.out.println("Select Payment Method:");
            System.out.println("  1. Cash on Delivery");
            System.out.println("  2. Card ");
            System.out.println("  3. UPI ");
            System.out.println("  0. Cancel Payment");
            System.out.print("Choice: ");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> { return PaymentMethod.CASH_ON_DELIVERY; }
                case "2" -> { return handleCardPayment(); }
                case "3" -> { return handleUpiPayment();  }
                case "0" -> { return null; }
                default  -> System.out.println("Invalid option. Please enter 1, 2, 3, or 0.");
            }
        }
    }

    // =========================================================================
    // SIMULATED CARD PAYMENT
    // =========================================================================

    private PaymentMethod handleCardPayment() {
        System.out.println("\n--- Card Payment (Simulated) ---");
        System.out.print("Enter Card Number (16 digits): ");
        String cardNumber = scanner.nextLine().trim();

        System.out.print("Enter Card Holder Name       : ");
        scanner.nextLine(); // consume input (not validated in simulation)

        System.out.print("Enter Expiry Date (MM/YY)    : ");
        scanner.nextLine();

        System.out.print("Enter CVV (3 digits)         : ");
        scanner.nextLine();

        if (cardNumber.length() != 16 || !cardNumber.matches("\\d+")) {
            System.out.println("Invalid card number format. Payment cancelled.");
            return null;
        }

        return PaymentMethod.CARD;
    }

    // =========================================================================
    // SIMULATED UPI PAYMENT
    // =========================================================================

    private PaymentMethod handleUpiPayment() {
        System.out.println("\n--- UPI Payment (Simulated) ---");
        System.out.print("Enter UPI ID (e.g., name@bank): ");
        String upiId = scanner.nextLine().trim();

        if (!upiId.contains("@")) {
            System.out.println("Invalid UPI ID format. Payment cancelled.");
            return null;
        }

        return PaymentMethod.UPI;
    }

    // =========================================================================
    // RESULT DISPLAY
    // =========================================================================

    private void displayPaymentResult(Payment payment, PaymentMethod method) {
        System.out.println("\n" + "=".repeat(50));

        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            System.out.println("   ✅  Payment Successful!");
            System.out.println("   ✅  Order Placed Successfully!");
            System.out.println("=".repeat(50));
            System.out.println("  Payment ID   : " + payment.getPaymentId());
            System.out.println("  Order ID     : " + payment.getOrderId());
            System.out.printf ("  Amount Paid  : ₹%.2f%n", payment.getAmountPaid());
            System.out.println("  Method       : " + formatMethodName(method));
            System.out.println("  Paid At      : " + payment.getPaidAt());

            if (method == PaymentMethod.CASH_ON_DELIVERY) {
                System.out.println("\n  Please keep ₹" + String.format("%.2f", payment.getAmountPaid())
                        + " ready for the delivery person.");
            }

            System.out.println("\n  Thank you for ordering with us! 🍽️");

        } else {
            System.out.println("   ❌  Payment Failed!");
            System.out.println("=".repeat(50));
            System.out.println("  Your order is saved. Please retry payment or contact support.");
            System.out.println("  Order ID : " + payment.getOrderId());
        }

        System.out.println("=".repeat(50));
    }

    // =========================================================================
    // HELPERS
    // =========================================================================

    private String formatMethodName(PaymentMethod method) {
        return switch (method) {
            case CASH_ON_DELIVERY -> "Cash on Delivery";
            case CARD             -> "Card";
            case UPI              -> "UPI";
        };
    }

    /**
     * Simulates a brief processing delay so the experience feels realistic.
     * COD needs no processing so skips the delay.
     */
    private void simulateProcessingDelay(PaymentMethod method) {
        if (method == PaymentMethod.CASH_ON_DELIVERY) return;
        try {
            System.out.print(".");
            Thread.sleep(400);
            System.out.print(".");
            Thread.sleep(400);
            System.out.println(".");
            Thread.sleep(300);
        } catch (InterruptedException ignored) {}
    }
}
