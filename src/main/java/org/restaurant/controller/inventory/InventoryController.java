package org.restaurant.controller.inventory;

import org.restaurant.model.inventory.InventoryItem;
import org.restaurant.service.inventory.InventoryService;
import java.util.Collection;
import java.util.Scanner;

public class InventoryController {
    private Scanner scanner;
    private InventoryService inventoryService = new InventoryService();

    public InventoryController(Scanner scanner) {
        this.scanner = scanner;
    }

    public void showInventory() {
        while (true) {
            System.out.println("\n--- INVENTORY MANAGEMENT ---");
            System.out.println("1. View All Inventory");
            System.out.println("2. Add Inventory Item");
            System.out.println("3. Update Inventory Item");
            System.out.println("4. Delete Inventory Item");
            System.out.println("0. Back");
            System.out.print("Choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> viewInventory();
                case 2 -> addItem();
                case 3 -> updateItem();
                case 4 -> deleteItem();
                case 0 -> { return; }
                default -> System.out.println("Invalid choice");
            }
        }
    }

    private void viewInventory() {
        Collection<InventoryItem> items = inventoryService.getAllInventoryItems();
        if (items.isEmpty()) {
            System.out.println("No items in inventory.");
            return;
        }
        System.out.println("\n--- INVENTORY ---");
        int i = 1;
        for (InventoryItem item : items) {
            System.out.println(i++ + ". [ID: " + item.getProductId() + "] "
                    + item.getName()
                    + " | ₹" + item.getPrice()
                    + " | Qty: " + item.getQuantity()
                    + " | Rating: " + item.getRating());
        }
    }

    private void addItem() {
        System.out.println("\n--- Add Inventory Item ---");
        System.out.print("Enter Product ID: ");
        String productId = scanner.nextLine();
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Description: ");
        String desc = scanner.nextLine();
        System.out.print("Enter Rating: ");
        double rating = scanner.nextDouble();
        System.out.print("Enter Price: ₹");
        double price = scanner.nextDouble();
        System.out.print("Enter Quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine();

        boolean added = inventoryService.addInventoryItem(productId, name, desc, rating, price, quantity);
        System.out.println(added ? "Item added to inventory!" : "Failed to add item!");
    }

    private void updateItem() {
        System.out.print("Enter Product ID to update: ");
        String productId = scanner.nextLine();

        InventoryItem item = inventoryService.getItemByProductId(productId);
        if (item == null) {
            System.out.println("Item not found!");
            return;
        }

        System.out.println("\nCurrent: " + item.getName()
                + " | ₹" + item.getPrice()
                + " | Qty: " + item.getQuantity());

        System.out.println("1. Name  2. Price  3. Quantity  4. Description");
        System.out.print("Choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> { System.out.print("New name: "); inventoryService.updateItem(productId, "name", scanner.nextLine()); }
            case 2 -> { System.out.print("New price: ₹"); inventoryService.updateItem(productId, "price", scanner.nextLine()); }
            case 3 -> { System.out.print("New quantity: "); inventoryService.updateItem(productId, "quantity", scanner.nextLine()); }
            case 4 -> { System.out.print("New description: "); inventoryService.updateItem(productId, "description", scanner.nextLine()); }
            default -> System.out.println("Invalid choice");
        }
        System.out.println("Updated successfully!");
    }

    private void deleteItem() {
        System.out.print("Enter Product ID to delete: ");
        String productId = scanner.nextLine();

        InventoryItem item = inventoryService.getItemByProductId(productId);
        if (item == null) {
            System.out.println("Item not found!");
            return;
        }

        System.out.print("Confirm delete " + item.getName() + "? (yes/no): ");
        String confirm = scanner.nextLine();
        if (!confirm.equalsIgnoreCase("yes")) {
            System.out.println("Delete cancelled.");
            return;
        }

        boolean deleted = inventoryService.deleteItem(productId);
        System.out.println(deleted ? "Item deleted!" : "Delete failed!");
    }
}