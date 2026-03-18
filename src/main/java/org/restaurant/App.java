package org.restaurant;

import org.restaurant.controller.login.AdminLoginController;
import org.restaurant.controller.login.ManagerLoginController;
import org.restaurant.controller.login.CustomerLoginController;
import org.restaurant.controller.login.DeliveryBoyLoginController;
import org.restaurant.service.login.CustomerLoginService;
import org.restaurant.service.login.DeliveryBoyLoginService;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Shared services
        CustomerLoginService customerLoginService = new CustomerLoginService();
        DeliveryBoyLoginService deliveryBoyLoginService = new DeliveryBoyLoginService();

        // Controllers
        CustomerLoginController customerLoginController
                = new CustomerLoginController(scanner, customerLoginService);

        DeliveryBoyLoginController deliveryBoyLoginController
                = new DeliveryBoyLoginController(scanner, deliveryBoyLoginService);

        AdminLoginController adminLoginController
                = new AdminLoginController(scanner, customerLoginService, deliveryBoyLoginService);

        ManagerLoginController managerLoginController
                = new ManagerLoginController(scanner, customerLoginService, deliveryBoyLoginService);

        while (true) {
            System.out.println("\n=============================");
            System.out.println("  Welcome to Restaurant App  ");
            System.out.println("=============================");
            System.out.println("1. Admin");
            System.out.println("2. Manager");
            System.out.println("3. Customer");
            System.out.println("4. Delivery Boy");
            System.out.println("0. Exit");
            System.out.print("Select Role: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> adminLoginController.start();
                case 2 -> managerLoginController.start();
                case 3 -> customerLoginController.start();
                case 4 -> deliveryBoyLoginController.start();
                case 0 -> {
                    System.out.println("Goodbye!");
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }
}