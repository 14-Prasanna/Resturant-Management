package org.restaurant.controller.discount;

import org.restaurant.model.discount.Discount;
import org.restaurant.service.discount.DiscountService;

import java.sql.Date;
import java.util.List;
import java.util.Scanner;

public class DiscountController {
    private Scanner scanner;
    private DiscountService discountService = new DiscountService();

    public DiscountController(Scanner scanner) {
        this.scanner = scanner;
    }

    public void manageDiscounts() {
        while (true) {
            System.out.println("\n--- Manage Discounts ---");
            System.out.println("1. Create New Discount");
            System.out.println("2. View All Discounts");
            System.out.println("0. Back");
            System.out.print("Choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> createDiscount();
                case 2 -> viewAllDiscounts();
                case 0 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void createDiscount() {
        try {
            System.out.print("Enter Discount Code (ex: SUMMER20): ");
            String code = scanner.nextLine().toUpperCase();
            
            System.out.print("Enter Discount Percent (ex: 20 for 20%): ");
            double percent = scanner.nextDouble();
            
            System.out.print("Enter Minimum Order Amount to qualify (0 if none): ");
            double minAmount = scanner.nextDouble();
            
            System.out.print("Enter Max Uses (0 for unlimited): ");
            int maxUsesIn = scanner.nextInt();
            scanner.nextLine();
            Integer maxUses = maxUsesIn == 0 ? null : maxUsesIn;

            System.out.print("Enter Valid From Date (YYYY-MM-DD): ");
            Date fromDate = Date.valueOf(scanner.nextLine());
            
            System.out.print("Enter Valid Until Date (YYYY-MM-DD): ");
            Date untilDate = Date.valueOf(scanner.nextLine());

            if (discountService.createDiscount(code, percent, minAmount, maxUses, fromDate, untilDate)) {
                System.out.println("Discount created successfully!");
            } else {
                System.out.println("Failed to create discount.");
            }
        } catch (Exception e) {
            System.out.println("❌ Invalid input format! Please try again: " + e.getMessage());
            scanner.nextLine(); // clear buffer
        }
    }

    private void viewAllDiscounts() {
        System.out.println("\n--- All Discounts ---");
        List<Discount> all = discountService.getAllDiscounts();
        if (all.isEmpty()) {
            System.out.println("No discounts exist yet.");
            return;
        }
        for (Discount d : all) {
            System.out.println(d);
        }
    }
}
