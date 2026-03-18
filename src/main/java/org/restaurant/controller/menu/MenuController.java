package org.restaurant.controller.menu;
import org.restaurant.service.menu.MenuService;
import org.restaurant.model.menu.MenuItem;
import java.util.*;

public class MenuController {
    private Scanner scanner;
    private MenuService service = new MenuService();

    public MenuController(Scanner scanner) {
        this.scanner = scanner;
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n--- MENU ---");
            System.out.println("1. View Menu");
            System.out.println("2. Add Item");
            System.out.println("3. Update Item");
            System.out.println("4. Delete Item");
            System.out.println("0. Back");
            System.out.print("Choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> displayMenu();
                case 2 -> addItemFlow();
                case 3 -> updateItemFlow();
                case 4 -> deleteItemFlow();
                case 0 -> { return; }
                default -> System.out.println("Invalid choice");
            }
        }
    }

    // Helper to select meal time
    private String selectMealTime() {
        System.out.println("\nSelect Meal Time:");
        System.out.println("1. Morning");
        System.out.println("2. Afternoon");
        System.out.println("3. Night");
        System.out.println("4. Snacks");
        System.out.print("Choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        return switch (choice) {
            case 1 -> "Morning";
            case 2 -> "Afternoon";
            case 3 -> "Night";
            case 4 -> "Snacks";
            default -> {
                System.out.println("Invalid choice, defaulting to Snacks.");
                yield "Snacks";
            }
        };
    }

    private void displayMenu() {
        Collection<MenuItem> items = service.getMenuItems();
        if (items.isEmpty()) {
            System.out.println("No items available");
            return;
        }
        int i = 1;
        for (MenuItem item : items) {
            System.out.println(i++ + ". [" + item.getMealTime() + "] "
                    + item.getName()
                    + " | " + item.getDescription()
                    + " | Rating: " + item.getRating());
        }
    }

    private void addItemFlow() {
        System.out.print("Enter item name: ");
        String name = scanner.nextLine();
        System.out.print("Enter description: ");
        String desc = scanner.nextLine();
        System.out.print("Enter rating: ");
        double rating = scanner.nextDouble();
        scanner.nextLine();
        String mealTime = selectMealTime(); // NEW

        boolean added = service.addMenuItem(name, desc, rating, mealTime);
        System.out.println(added ? "Item added successfully!" : "Item already exists!");
    }

    private void updateItemFlow() {
        System.out.print("Enter item name to update: ");
        String name = scanner.nextLine();
        System.out.print("Enter new description: ");
        String desc = scanner.nextLine();
        System.out.print("Enter new rating: ");
        double rating = scanner.nextDouble();
        scanner.nextLine();
        String mealTime = selectMealTime(); // NEW

        boolean updated = service.updateMenuItem(name, desc, rating, mealTime);
        System.out.println(updated ? "Item updated successfully!" : "Item not found!");
    }

    private void deleteItemFlow() {
        System.out.print("Enter item name to delete: ");
        String name = scanner.nextLine();
        boolean deleted = service.deleteMenuItem(name);
        System.out.println(deleted ? "Item deleted successfully!" : "Item not found!");
    }
}