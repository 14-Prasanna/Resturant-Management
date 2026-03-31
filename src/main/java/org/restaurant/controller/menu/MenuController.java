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

    public String selectMealTime() {
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
        System.out.println("\nView menu for which meal time?");
        System.out.println("1. Morning");
        System.out.println("2. Afternoon");
        System.out.println("3. Night");
        System.out.println("4. Snacks");
        System.out.println("5. All");
        System.out.print("Choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        String filter = switch (choice) {
            case 1 -> "Morning";
            case 2 -> "Afternoon";
            case 3 -> "Night";
            case 4 -> "Snacks";
            default -> "All";
        };

        Collection<MenuItem> items = service.getMenuItemsByMealTime(filter);
        if (items.isEmpty()) {
            System.out.println("No items available for " + filter);
            return;
        }

        System.out.println("\n--- " + filter.toUpperCase() + " MENU ---");
        int i = 1;
        for (MenuItem item : items) {
            System.out.println(i++ + ". [ID: " + item.getProductId() + "] "
                    + "[" + item.getMealTime() + "] "
                    + item.getName()
                    + " | " + item.getDescription()
                    + " | Price: ₹" + item.getPrice()
                    + " | Rating: " + item.getRating());
        }
    }

    private void addItemFlow() {
        // Step 1 - Product ID
        System.out.print("Enter Product ID: ");
        String productId = scanner.nextLine();

        // Step 2 - Item details
        System.out.print("Enter item name: ");
        String name = scanner.nextLine();
        System.out.print("Enter description: ");
        String desc = scanner.nextLine();
        System.out.print("Enter rating: ");
        double rating = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter price: ₹");
        double price = scanner.nextDouble();
        scanner.nextLine();

        // Step 3 - Meal time
        String mealTime = selectMealTime();

        // Step 4 - Check if same name exists in other meal times
        List<String> existingMealTimes = service.getMealTimesForName(name);
        if (!existingMealTimes.isEmpty()) {
            System.out.println("Note: '" + name + "' already exists in: " + existingMealTimes);
            System.out.print("Do you still want to add it for " + mealTime + "? (yes/no): ");
            String confirm = scanner.nextLine();
            if (!confirm.equalsIgnoreCase("yes")) {
                System.out.println("Add cancelled.");
                return;
            }
        }

        // Step 5 - Check duplicate name + mealTime combo
        if (service.existsByNameAndMealTime(name, mealTime)) {
            System.out.println("'" + name + "' already exists in " + mealTime + " menu!");
            return;
        }

        boolean added = service.addMenuItem(productId, name, desc, rating, price, mealTime);
        System.out.println(added ? "Item added successfully!" : "Product ID already exists!");
    }

    private void updateItemFlow() {
        // Step 1 - Ask for product ID
        System.out.print("Enter Product ID to update: ");
        String productId = scanner.nextLine();

        // Step 2 - Check if item exists
        MenuItem item = service.getItemByProductId(productId);
        if (item == null) {
            System.out.println("No item found with Product ID: " + productId);
            return;
        }

        // Step 3 - Show current details
        System.out.println("\nCurrent Details:");
        System.out.println("Name        : " + item.getName());
        System.out.println("Description : " + item.getDescription());
        System.out.println("Rating      : " + item.getRating());
        System.out.println("Price       : ₹" + item.getPrice());
        System.out.println("Meal Time   : " + item.getMealTime());

        // Step 4 - Ask what to update
        boolean updating = true;
        while (updating) {
            System.out.println("\nWhat do you want to update?");
            System.out.println("1. Name");
            System.out.println("2. Description");
            System.out.println("3. Rating");
            System.out.println("4. Price");
            System.out.println("5. Meal Time");
            System.out.println("0. Done");
            System.out.print("Choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter new name: ");
                    String newName = scanner.nextLine();
                    service.updateField(productId, "name", newName);
                    System.out.println("Name updated!");
                }
                case 2 -> {
                    System.out.print("Enter new description: ");
                    String newDesc = scanner.nextLine();
                    service.updateField(productId, "description", newDesc);
                    System.out.println("Description updated!");
                }
                case 3 -> {
                    System.out.print("Enter new rating: ");
                    String newRating = scanner.nextLine();
                    service.updateField(productId, "rating", newRating);
                    System.out.println("Rating updated!");
                }
                case 4 -> {
                    System.out.print("Enter new price: ₹");
                    String newPrice = scanner.nextLine();
                    service.updateField(productId, "price", newPrice);
                    System.out.println("Price updated!");
                }
                case 5 -> {
                    String newMealTime = selectMealTime();
                    service.updateField(productId, "mealTime", newMealTime);
                    System.out.println("Meal Time updated!");
                }
                case 0 -> updating = false;
                default -> System.out.println("Invalid choice");
            }
        }
        System.out.println("Item updated successfully!");
    }

    private void deleteItemFlow() {
        System.out.print("Enter Product ID to delete: ");
        String productId = scanner.nextLine();

        // Check if exists first
        MenuItem item = service.getItemByProductId(productId);
        if (item == null) {
            System.out.println("No item found with Product ID: " + productId);
            return;
        }

        System.out.println("Deleting: [" + item.getMealTime() + "] "
                + item.getName() + " | ₹" + item.getPrice());
        System.out.print("Confirm delete? (yes/no): ");
        String confirm = scanner.nextLine();

        if (!confirm.equalsIgnoreCase("yes")) {
            System.out.println("Delete cancelled.");
            return;
        }

        boolean deleted = service.deleteMenuItem(productId);
        System.out.println(deleted ? "Item deleted successfully!" : "Delete failed!");
    }
}