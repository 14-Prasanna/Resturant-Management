package org.restaurant.controller.menu;

import org.restaurant.service.menu.MenuService;
import org.restaurant.service.inventory.InventoryService;
import org.restaurant.model.inventory.InventoryItem;
import org.restaurant.model.menu.MenuItem;
import java.util.*;

public class MenuController {
    private Scanner scanner;
    private MenuService service = new MenuService();
    private InventoryService inventoryService = new InventoryService();

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

    public String selectSingleMealTime() {
        System.out.println("\nSelect Meal Time Filter:");
        System.out.println("1. Morning");
        System.out.println("2. Afternoon");
        System.out.println("3. Evening");
        System.out.println("4. Night");
        System.out.println("5. Snacks");
        System.out.print("Choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        return switch (choice) {
            case 1 -> "Morning";
            case 2 -> "Afternoon";
            case 3 -> "Evening";
            case 4 -> "Night";
            case 5 -> "Snacks";
            default -> {
                System.out.println("Invalid choice, defaulting to Snacks.");
                yield "Snacks";
            }
        };
    }

    public List<Integer> selectMealTimes() {
        System.out.println("\nSelect Meal Times (Enter numbers separated by spaces, e.g., '1 2'):");
        System.out.println("1. Morning");
        System.out.println("2. Afternoon");
        System.out.println("3. Evening");
        System.out.println("4. Night");
        System.out.println("5. Snacks");
        System.out.print("Choices: ");
        String input = scanner.nextLine().trim();

        List<Integer> selectedTimes = new ArrayList<>();
        if (input.isEmpty()) return selectedTimes;

        String[] parts = input.split("\\s+");
        for (String part : parts) {
            try {
                int choice = Integer.parseInt(part);
                if (choice >= 1 && choice <= 5) {
                    selectedTimes.add(choice);
                } else {
                    System.out.println("Ignoring invalid choice: " + choice);
                }
            } catch (NumberFormatException e) {
                System.out.println("Ignoring non-number input: " + part);
            }
        }
        return selectedTimes;
    }

    public void displayMenu() {
        System.out.println("\nView menu for which meal time?");
        System.out.println("1. Morning");
        System.out.println("2. Afternoon");
        System.out.println("3. Evening");
        System.out.println("4. Night");
        System.out.println("5. Snacks");
        System.out.println("6. All");
        System.out.print("Choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        String filter = switch (choice) {
            case 1 -> "Morning";
            case 2 -> "Afternoon";
            case 3 -> "Evening";
            case 4 -> "Night";
            case 5 -> "Snacks";
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
                    + "[" + String.join(", ", item.getMealTimes()) + "] " // No quantity shown
                    + item.getName()
                    + " | " + item.getDescription()
                    + " | Price: ₹" + item.getPrice()
                    + " | Rating: " + item.getRating());
        }
    }

    private void addItemFlow() {
        System.out.println("\n--- Available Inventory Items ---");
        Collection<InventoryItem> invItems = inventoryService.getAllInventoryItems();
        if (invItems.isEmpty()) {
            System.out.println("Inventory is empty! Please add items to inventory first.");
            return;
        }
        
        for (InventoryItem iItem : invItems) {
            // Note: Not showing quantity here in Menu UI per user request, though this is inventory list. We'll show basic details.
            System.out.println("[ID: " + iItem.getProductId() + "] " + iItem.getName() + " | Price: ₹" + iItem.getPrice() + " | Unit: " + iItem.getUnit());
        }
        
        // Step 1 - Product ID
        System.out.print("\nEnter Product ID to add to Menu: ");
        String productId = scanner.nextLine().trim();

        // Step 1.5 - Validate Product ID
        InventoryItem selectedInventory = inventoryService.getItemByProductId(productId);
        if (selectedInventory == null) {
            System.out.println("Product ID not found in inventory!");
            return;
        }

        // Check if menu item already exists
        if (service.getItemByProductId(productId) != null) {
            System.out.println("This item is already in the menu!");
            return;
        }

        // Step 2 - Use inventory details automatically
        String name = selectedInventory.getName();
        double price = selectedInventory.getPrice();
        
        System.out.print("Enter Description for Menu Item: ");
        String desc = scanner.nextLine();
        
        System.out.print("Enter Initial Rating (0.0 to 5.0): ");
        double rating = scanner.nextDouble();
        scanner.nextLine();
        
        System.out.println("Details: Name=" + name + ", Desc=" + desc + ", Price=₹" + price);

        // Step 3 - Meal time
        List<Integer> mealTimeIds = selectMealTimes();
        if (mealTimeIds.isEmpty()) {
            System.out.println("No meal times selected. Adding item but it won't be available in any specific time.");
        }

        boolean added = service.addMenuItem(productId, name, desc, rating, price, mealTimeIds);
        System.out.println(added ? "Item automatically added out of inventory to Menu successfully!" : "Failed to add item!");
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
        System.out.println("Meal Times  : " + String.join(", ", item.getMealTimes()));

        // Step 4 - Ask what to update
        boolean updating = true;
        while (updating) {
            System.out.println("\nWhat do you want to update?");
            System.out.println("1. Name");
            System.out.println("2. Description");
            System.out.println("3. Rating");
            System.out.println("4. Price");
            System.out.println("5. Meal Times");
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
                    List<Integer> newMealTimeIds = selectMealTimes();
                    service.updateMealTimes(productId, newMealTimeIds);
                    System.out.println("Meal Times updated!");
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

        System.out.println("Deleting: [" + String.join(", ", item.getMealTimes()) + "] "
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