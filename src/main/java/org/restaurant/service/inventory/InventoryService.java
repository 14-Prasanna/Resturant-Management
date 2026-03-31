package org.restaurant.service.inventory;

import org.restaurant.model.inventory.InventoryItem;
import org.restaurant.repository.inventory.InventoryRepository;
import java.util.Collection;

public class InventoryService {
    private InventoryRepository repo = InventoryRepository.getInstance();

    public boolean addInventoryItem(String productId, String name,
                                    String desc, double rating,
                                    double price, int quantity) {
        // Validations
        if (productId == null || productId.trim().isEmpty()) {
            System.out.println("Product ID cannot be empty!");
            return false;
        }
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Name cannot be empty!");
            return false;
        }
        if (price <= 0) {
            System.out.println("Price must be greater than 0!");
            return false;
        }
        if (quantity < 0) {
            System.out.println("Quantity cannot be negative!");
            return false;
        }

        return repo.addItem(new InventoryItem(productId, name, desc, rating, price, quantity));
    }

    public Collection<InventoryItem> getAllInventoryItems() {
        return repo.getAllItems();
    }

    public InventoryItem getItemByProductId(String productId) {
        return repo.getByProductId(productId);
    }

    public boolean reduceQuantity(String productId, int qty) {
        return repo.reduceQuantity(productId, qty);
    }

    public boolean updateItem(String productId, String field, String value) {
        return repo.updateItem(productId, field, value);
    }

    public boolean deleteItem(String productId) {
        return repo.deleteItem(productId);
    }
}