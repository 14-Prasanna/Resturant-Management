package org.restaurant.controller.login;

import org.restaurant.model.login.DeliveryBoyLogin;
import org.restaurant.service.login.DeliveryBoyLoginService;
import java.util.Scanner;

public class DeliveryBoyLoginController {
    private Scanner scanner;
    private DeliveryBoyLoginService deliveryBoyLoginService;

    public DeliveryBoyLoginController(Scanner scanner, DeliveryBoyLoginService deliveryBoyLoginService) {
        this.scanner = scanner;
        this.deliveryBoyLoginService = deliveryBoyLoginService;
    }

    // Called from App.java
    public void start() {
        while (true) {
            System.out.println("\n--- Delivery Boy Portal ---");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> register();
                case 2 -> login();
                case 0 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void register() {
        System.out.println("\n--- Delivery Boy Register ---");
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();
        deliveryBoyLoginService.register(username, password);
    }

    private void login() {
        System.out.println("\n--- Delivery Boy Login ---");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        DeliveryBoyLogin boy = deliveryBoyLoginService.login(username, password);

        if (boy != null) {
            System.out.println("Welcome, " + boy.getUsername() + "!");
            deliveryBoyDashboard(boy);
        } else {
            System.out.println("Invalid credentials. Try again.");
        }
    }

    private void deliveryBoyDashboard(DeliveryBoyLogin boy) {
        while (true) {
            System.out.println("\n--- Delivery Boy Dashboard ---");
            System.out.println("Logged in as: " + boy.getUsername());
            System.out.println("1. View Assigned Orders");
            System.out.println("2. View Delivery History");
            System.out.println("0. Logout");
            System.out.print("Choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> viewAssignedOrders(boy);
                case 2 -> viewDeliveryHistory(boy);
                case 0 -> {
                    System.out.println("Logged out. Returning to main menu...");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void viewAssignedOrders(DeliveryBoyLogin boy) {
        System.out.println("\n--- Assigned Orders ---");
        if (boy.getAssignedOrders().isEmpty()) {
            System.out.println("No assigned orders yet.");
        } else {
            for (String order : boy.getAssignedOrders()) {
                System.out.println("- " + order);
            }
        }
    }

    private void viewDeliveryHistory(DeliveryBoyLogin boy) {
        System.out.println("\n--- Delivery History ---");
        if (boy.getDeliveryHistory().isEmpty()) {
            System.out.println("No delivery history yet.");
        } else {
            for (String history : boy.getDeliveryHistory()) {
                System.out.println("- " + history);
            }
        }
    }

    // Called by Admin & Manager
    public DeliveryBoyLoginService getDeliveryBoyLoginService() {
        return deliveryBoyLoginService;
    }
}
