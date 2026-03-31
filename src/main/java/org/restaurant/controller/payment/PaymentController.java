package org.restaurant.controller.payment;

import org.restaurant.model.cart.Cart.CartItem;
import org.restaurant.service.payment.PaymentService;
import java.util.List;
import java.util.Scanner;

public class PaymentController {
    private Scanner scanner;
    private PaymentService paymentService;

    public PaymentController(Scanner scanner, PaymentService paymentService) {
        this.scanner        = scanner;
        this.paymentService = paymentService;
    }

    public boolean processPayment(String customerId,
                                   List<CartItem> cartItems,
                                   double totalAmount) {
        System.out.println("\n--- PAYMENT ---");
        System.out.println("Total Amount: ₹" + totalAmount);
        System.out.println("Select Payment Method:");
        System.out.println("1. Cash");
        System.out.println("2. Card");
        System.out.println("3. UPI");
        System.out.print("Choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        return paymentService.processPayment(customerId, cartItems, totalAmount, choice);
    }
}