package org.restaurant.controller.login;

import org.restaurant.model.login.DeliveryBoyLogin;
import org.restaurant.service.login.DeliveryBoyLoginService;
import java.util.Collection;
import java.util.Scanner;

public class DeliveryBoyLoginController {

    private Scanner scanner;
    private DeliveryBoyLoginService deliveryBoyLoginService;

    public DeliveryBoyLoginController(Scanner scanner,
                                         DeliveryBoyLoginService deliveryBoyLoginService) {
        this.scanner = scanner;
        this.deliveryBoyLoginService = deliveryBoyLoginService;
    }

    public void start() {
        while (true) {
            System.out.println("\n--- Delivery Assignment Management ---");
            System.out.println("1. View All Delivery Boys");
            System.out.println("2. Assign Order to Delivery Boy");
            System.out.println("3. View Assigned Orders");
            System.out.println("4. Mark Order as Delivered");
            System.out.println("0. Back");
            System.out.print("Choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> viewAllDeliveryBoys();
                case 2 -> assignOrder();
                case 3 -> viewAssignedOrders();
                case 4 -> markAsDelivered();
                case 0 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void viewAllDeliveryBoys() {
        Collection<DeliveryBoyLogin> boys = deliveryBoyLoginService.getAllDeliveryBoys();
        if (boys.isEmpty()) {
            System.out.println("No delivery boys registered yet.");
            return;
        }
        System.out.println("\n--- All Delivery Boys ---");
        for (DeliveryBoyLogin boy : boys) {
            System.out.println("---------------------------");
            System.out.println("Username       : " + boy.getUsername());
            System.out.println("Assigned Orders: " + 
                (boy.getAssignedOrders().isEmpty() ? "None" : boy.getAssignedOrders()));
            System.out.println("Status         : " + 
                (boy.getAssignedOrders().isEmpty() ? "Available" : "Busy"));
        }
        System.out.println("---------------------------");
    }

    private void assignOrder() {
        // Step 1 — show available delivery boys
        Collection<DeliveryBoyLogin> boys = deliveryBoyLoginService.getAllDeliveryBoys();
        if (boys.isEmpty()) {
            System.out.println("No delivery boys available!");
            return;
        }

        System.out.println("\n--- Available Delivery Boys ---");
        for (DeliveryBoyLogin boy : boys) {
            if (boy.getAssignedOrders().isEmpty()) {
                System.out.println("- " + boy.getUsername());
            }
        }

        // Step 2 — enter delivery boy username
        System.out.print("\nEnter Delivery Boy Username: ");
        String username = scanner.nextLine();

        // Step 3 — enter order details
        System.out.print("Enter Order Details: ");
        String orderDetails = scanner.nextLine();

        // Step 4 — assign
        deliveryBoyLoginService.addAssignedOrder(username, orderDetails);
        System.out.println("Order assigned to " + username + " successfully!");
    }

    private void viewAssignedOrders() {
        Collection<DeliveryBoyLogin> boys = deliveryBoyLoginService.getAllDeliveryBoys();
        if (boys.isEmpty()) {
            System.out.println("No delivery boys found.");
            return;
        }
        System.out.println("\n--- All Assigned Orders ---");
        for (DeliveryBoyLogin boy : boys) {
            if (!boy.getAssignedOrders().isEmpty()) {
                System.out.println("Delivery Boy : " + boy.getUsername());
                for (String order : boy.getAssignedOrders()) {
                    System.out.println("  Order: " + order);
                }
            }
        }
    }

    private void markAsDelivered() {
        System.out.print("Enter Delivery Boy Username: ");
        String username = scanner.nextLine();

        System.out.print("Enter Order to mark as delivered: ");
        String order = scanner.nextLine();

        // Move from assigned to history
        deliveryBoyLoginService.addDeliveryHistory(username, order);
        System.out.println("Order marked as delivered and moved to history!");
    }
}