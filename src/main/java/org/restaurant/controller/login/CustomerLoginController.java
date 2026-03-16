package org.restaurant.controller.login;

import org.restaurant.model.login.CustomerLogin;
import org.restaurant.service.login.CustomerLoginService;
import java.util.Scanner;

public class CustomerLoginController {
    private Scanner scanner;
    private CustomerLoginService customerLoginService;

    // Correct 2-argument constructor
    public CustomerLoginController(Scanner scanner, CustomerLoginService customerLoginService) {
        this.scanner = scanner;
        this.customerLoginService = customerLoginService;
    }

    public void start() {
        while (true) {
            System.out.println("\n--- Customer Portal ---");
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
        System.out.println("\n--- Customer Register ---");
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();
        customerLoginService.register(username, password);
    }

    private void login() {
        System.out.println("\n--- Customer Login ---");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        CustomerLogin customer = customerLoginService.login(username, password);

        if (customer != null) {
            System.out.println("Welcome, " + customer.getUsername() + "!");
            customerDashboard(customer);
        } else {
            System.out.println("Invalid credentials. Try again.");
        }
    }

    private void customerDashboard(CustomerLogin customer) {
        while (true) {
            System.out.println("\n--- Customer Dashboard ---");
            System.out.println("Logged in as: " + customer.getUsername());
            System.out.println("1. View Menu (coming soon)");
            System.out.println("2. Place Order (coming soon)");
            System.out.println("3. View My Past Orders");
            System.out.println("4. View My Reports");
            System.out.println("0. Logout");
            System.out.print("Choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> System.out.println("Menu coming soon...");
                case 2 -> System.out.println("Order placement coming soon...");
                case 3 -> viewPastOrders(customer);
                case 4 -> viewReports(customer);
                case 0 -> {
                    System.out.println("Logged out. Returning to main menu...");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void viewPastOrders(CustomerLogin customer) {
        System.out.println("\n--- My Past Orders ---");
        if (customer.getPastOrders().isEmpty()) {
            System.out.println("No orders yet.");
        } else {
            for (String order : customer.getPastOrders()) {
                System.out.println("- " + order);
            }
        }
    }

    private void viewReports(CustomerLogin customer) {
        System.out.println("\n--- My Reports ---");
        if (customer.getReports().isEmpty()) {
            System.out.println("No reports yet.");
        } else {
            for (String report : customer.getReports()) {
                System.out.println("- " + report);
            }
        }
    }
}
