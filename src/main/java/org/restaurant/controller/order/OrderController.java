package org.restaurant.controller.order;

import org.restaurant.model.order.Order;
import org.restaurant.model.cart.Cart.CartItem;
import org.restaurant.service.order.OrderService;

import java.util.List;
import java.util.Scanner;

public class OrderController {

    private Scanner scanner;
    private OrderService orderService;

    public OrderController(Scanner scanner, OrderService orderService) {
        this.scanner      = scanner;
        this.orderService = orderService;
    }

    // -------------------------------------------------------------------------
    // PLACE ORDER
    // -------------------------------------------------------------------------

    /**
     * Asks for confirmation, then delegates to OrderService.placeOrder().
     * Prints the result message returned by the service.
     */
    public void placeOrder(String customerId) {
        System.out.print("\nAre you sure you want to place the order? (yes/no): ");
        String confirm = scanner.nextLine().trim();

        if (!confirm.equalsIgnoreCase("yes")) {
            System.out.println("Order cancelled. Returning to dashboard.");
            return;
        }

        String result = orderService.placeOrder(customerId);
        System.out.println(result);
    }

    // -------------------------------------------------------------------------
    // VIEW PAST ORDERS
    // -------------------------------------------------------------------------

    /** Displays all past order for the customer. */
    public void viewMyOrders(String customerId) {
        List<Order> orders = orderService.getOrdersByCustomer(customerId);

        if (orders.isEmpty()) {
            System.out.println("\nYou have no past orders.");
            return;
        }

        System.out.println("\n--- YOUR PAST ORDERS ---");
        for (Order order : orders) {
            System.out.println("\nOrder ID  : " + order.getOrderId());
            System.out.println("Placed At : " + order.getPlacedAt());
            System.out.println("Status    : " + order.getStatus());
            System.out.println("Items     :");
            for (CartItem item : order.getItems()) {
                System.out.printf("  - %-20s x%d  @ ₹%.2f  = ₹%.2f%n",
                        item.getName(), item.getQuantity(), item.getPrice(), item.getTotalPrice());
            }
            System.out.printf("Total     : ₹%.2f%n", order.getTotalAmount());
            System.out.println("-".repeat(50));
        }
    }
}