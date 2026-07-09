package org.restaurant.controller.login;

import java.util.List;
import java.util.Scanner;

import org.restaurant.controller.menu.MenuController;
import org.restaurant.model.login.CustomerLogin;
import org.restaurant.model.login.DeliveryBoyLogin;
import org.restaurant.model.order.Order;
import org.restaurant.service.login.CustomerLoginService;
import org.restaurant.service.login.DeliveryBoyLoginService;
import org.restaurant.service.login.ManagerLoginService;
import org.restaurant.service.order.OrderService;

public class ManagerLoginController {
    private Scanner scanner;
    private ManagerLoginService managerLoginService = new ManagerLoginService();
    private CustomerLoginService customerLoginService;
    private DeliveryBoyLoginService deliveryBoyLoginService;
    private OrderService orderService;

    public ManagerLoginController(Scanner scanner,
                                  CustomerLoginService customerLoginService,
                                  DeliveryBoyLoginService deliveryBoyLoginService,
                                  OrderService orderService) {
        this.scanner                 = scanner;
        this.customerLoginService    = customerLoginService;
        this.deliveryBoyLoginService = deliveryBoyLoginService;
        this.orderService            = orderService;
    }

    public void start() {
        System.out.println("\n--- Manager Login ---");
        String username = null;
        String password = null;

        // Get and validate username
        while (username == null || username.isEmpty()) {
            System.out.print("Username: ");
            username = scanner.nextLine().trim();
            
            if (username.isEmpty()) {
                System.out.println("❌ Error: Username cannot be empty");
                continue;
            }
            
            // Check if username contains only numbers
            if (username.matches("^[0-9]+$")) {
                System.out.println("❌ Error: Only string value is allowed. Numbers are not permitted as username.");
                username = null;
                continue;
            }
        }

        // Get and mask password
        password = getHiddenPassword();

        // Perform login with validation
        boolean success = managerLoginService.login(username, password);

        if (success) {
            System.out.println("✓ Login successful! Welcome, " + username);
            managerDashboard(username);
        } else {
            System.out.println("❌ Invalid credentials. Returning to main menu...");
        }
    }

    /**
     * Reads password from console while masking input
     * Demonstrates encapsulation of password input logic
     * @return the password entered (masked while typing)
     */
    private String getHiddenPassword() {
        String password = "";
        
        System.out.print("Password: ");
        try {
            // Use Console.readPassword() for secure password input (masks characters)
            char[] passwordChars = System.console().readPassword();
            if (passwordChars != null) {
                password = new String(passwordChars);
                // Clear sensitive data from memory
                java.util.Arrays.fill(passwordChars, ' ');
            } else {
                // Fallback if console is not available (IDE environment)
                System.out.println("[Note: Password input masking not available in this environment]");
                password = scanner.nextLine();
            }
        } catch (Exception e) {
            // Fallback to regular input
            password = scanner.nextLine();
        }
        
        return password;
    }

    private void managerDashboard(String username) {
        while (true) {
            System.out.println("\n--- Manager Dashboard ---");
            System.out.println("Logged in as: " + username);
            System.out.println("1. Manage Menu");
            System.out.println("2. View All Customers");
            System.out.println("3. View All Delivery Boys");
            System.out.println("4. View All Orders");
            System.out.println("0. Logout");
            System.out.print("Choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> {
                    MenuController menuController = new MenuController(scanner);
                    menuController.showMenu();
                }
                case 2 -> viewAllCustomers();
                case 3 -> viewAllDeliveryBoys();
                case 4 -> viewAllOrders();
                case 0 -> {
                    System.out.println("Logged out successfully. Returning to main menu...");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void viewAllCustomers() {
        System.out.println("\n--- All Customers ---");
        if (customerLoginService.getAllCustomers().isEmpty()) {
            System.out.println("No customers registered yet.");
            return;
        }
        for (CustomerLogin c : customerLoginService.getAllCustomers()) {
            System.out.println("---------------------------");
            System.out.println("Username : " + c.getUsername());

            List<Order> orders = orderService.getOrdersByCustomer(c.getUsername());
            if (orders.isEmpty()) {
                System.out.println("Orders   : No orders");
            } else {
                System.out.println("Orders   :");
                for (Order o : orders) {
                    System.out.println("  Order ID : " + o.getOrderId()
                            + " | Total: ₹" + o.getTotalAmount()
                            + " | Status: "  + o.getStatus()
                            + " | Placed At: " + o.getPlacedAt());
                }
            }
            System.out.println("Reports  : " + (c.getReports().isEmpty() ? "No reports" : c.getReports()));
        }
        System.out.println("---------------------------");
    }

    private void viewAllDeliveryBoys() {
        System.out.println("\n--- All Delivery Boys ---");
        if (deliveryBoyLoginService.getAllDeliveryBoys().isEmpty()) {
            System.out.println("No delivery boys registered yet.");
            return;
        }
        for (DeliveryBoyLogin d : deliveryBoyLoginService.getAllDeliveryBoys()) {
            System.out.println("---------------------------");
            System.out.println("Username         : " + d.getUsername());
            System.out.println("Assigned Orders  : " + (d.getAssignedOrders().isEmpty()  ? "No assigned orders"  : d.getAssignedOrders()));
            System.out.println("Delivery History : " + (d.getDeliveryHistory().isEmpty() ? "No delivery history" : d.getDeliveryHistory()));
        }
        System.out.println("---------------------------");
    }

    private void viewAllOrders() {
        System.out.println("\n--- All Orders ---");
        List<Order> allOrders = orderService.getAllOrders();
        if (allOrders.isEmpty()) {
            System.out.println("No orders placed yet.");
            return;
        }
        for (Order o : allOrders) {
            System.out.println("---------------------------");
            System.out.println("Order ID   : " + o.getOrderId());
            System.out.println("Customer   : " + o.getCustomerId());
            System.out.println("Total      : ₹" + o.getTotalAmount());
            System.out.println("Status     : " + o.getStatus());
            System.out.println("Placed At  : " + o.getPlacedAt());
            System.out.println("Items      :");
            o.getItems().forEach(item ->
                    System.out.println("  - " + item.getName()
                            + " x" + item.getQuantity()
                            + " | ₹" + item.getTotalPrice()));
        }
        System.out.println("---------------------------");
    }
}