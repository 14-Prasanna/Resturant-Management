package org.restaurant.controller.login;

import org.restaurant.model.order.Order;
import org.restaurant.repository.order.OrderRepository;
import org.restaurant.service.login.DeliveryBoyLoginService;

import java.util.List;
import java.util.Scanner;

public class DeliveryBoyDashboardController {

    private Scanner scanner;
    private String username;
    private DeliveryBoyLoginService loginService;
    private OrderRepository orderRepository = OrderRepository.getInstance();

    public DeliveryBoyDashboardController(Scanner scanner, String username, DeliveryBoyLoginService loginService) {
        this.scanner = scanner;
        this.username = username;
        this.loginService = loginService;
    }

    public void start() {
        while (true) {
            System.out.println("\n--- Delivery Boy Dashboard (" + username + ") ---");
            System.out.println("1. View My Assigned Orders");
            System.out.println("2. Pick Up Order (from READY state)");
            System.out.println("3. Update Order to 'Out For Delivery'");
            System.out.println("4. Mark Order as 'Delivered'");
            System.out.println("0. Logout");
            System.out.print("Choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> viewMyOrders();
                case 2 -> pickUpOrder();
                case 3 -> outForDelivery();
                case 4 -> markDelivered();
                case 0 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void viewMyOrders() {
        var boy = loginService.getAllDeliveryBoys().stream()
                .filter(b -> b.getUsername().equals(username))
                .findFirst().orElse(null);

        if (boy == null || boy.getAssignedOrders().isEmpty()) {
            System.out.println("You have no active orders assigned!");
            return;
        }

        System.out.println("\n--- My Tasks ---");
        for (String orderId : boy.getAssignedOrders()) {
            Order o = orderRepository.findById(orderId);
            if (o != null) {
                System.out.println("[Order ID: " + orderId + "] | Global Status: " + o.getStatus() + " | Final Amount: ₹" + o.getTotalAmount());
            } else {
                System.out.println("[Order ID: " + orderId + "] | Status: Unknown");
            }
        }
    }

    private void pickUpOrder() {
        System.out.print("Enter Order ID to pick up: ");
        String orderId = scanner.nextLine();

        Order order = orderRepository.findById(orderId);
        if (order == null) {
            System.out.println("❌ Order not found.");
            return;
        }

        if (!order.getStatus().equals("READY")) {
            System.out.println("❌ You cannot pick up this order. The Chef has not finished preparing it yet (Current Status: " + order.getStatus() + ").");
            return;
        }

        if (loginService.updateAssignmentStatus(username, orderId, "PICKED_UP")) {
            orderRepository.updateOrderStatus(orderId, "PICKED_UP");
            System.out.println("✅ Order " + orderId + " picked up successfully!");
        } else {
            System.out.println("❌ Failed. Ensure this order is strictly assigned to you.");
        }
    }

    private void outForDelivery() {
        System.out.print("Enter Order ID to mark as 'Out For Delivery': ");
        String orderId = scanner.nextLine();

        Order order = orderRepository.findById(orderId);
         if (order == null || !order.getStatus().equals("PICKED_UP")) {
            System.out.println("❌ You must PICK UP the order before you can set it to Out For Delivery.");
            return;
        }

        if (loginService.updateAssignmentStatus(username, orderId, "OUT_FOR_DELIVERY")) {
            orderRepository.updateOrderStatus(orderId, "OUT_FOR_DELIVERY");
            System.out.println("✅ Order " + orderId + " is now OUT FOR DELIVERY!");
        } else {
            System.out.println("❌ Failed to update status.");
        }
    }

    private void markDelivered() {
        System.out.print("Enter Order ID to mark as 'Delivered': ");
        String orderId = scanner.nextLine();

        Order order = orderRepository.findById(orderId);
        if (order == null || (!order.getStatus().equals("OUT_FOR_DELIVERY") && !order.getStatus().equals("PICKED_UP"))) {
            System.out.println("❌ Invalid order state for completion.");
            return;
        }

        loginService.addDeliveryHistory(username, orderId);
        orderRepository.updateOrderStatus(orderId, "DELIVERED");
        System.out.println("✅ Order " + orderId + " marked as DELIVERED!");
    }
}
