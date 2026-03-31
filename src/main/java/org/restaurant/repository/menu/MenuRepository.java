package org.restaurant.repository.menu;
import org.restaurant.model.menu.MenuItem;
import java.util.*;
import java.util.stream.Collectors;

public class MenuRepository {
    // Key = productId, allows same name in different meal times
    private Map<String, MenuItem> menu = new HashMap<>();

    public boolean addItem(MenuItem item) {
        if (menu.containsKey(item.getProductId())) return false;
        menu.put(item.getProductId(), item);
        return true;
    }

    public Collection<MenuItem> getAllItems() {
        return menu.values();
    }

    // Find item by productId
    public MenuItem getItemByProductId(String productId) {
        return menu.get(productId);
    }

    // Check if same name exists in same meal time
    public boolean existsByNameAndMealTime(String name, String mealTime) {
        return menu.values().stream()
                .anyMatch(item -> item.getName().equalsIgnoreCase(name)
                        && item.getMealTime().equalsIgnoreCase(mealTime));
    }

    // Get all meal times where this name already exists
    public List<String> getMealTimesForName(String name) {
        return menu.values().stream()
                .filter(item -> item.getName().equalsIgnoreCase(name))
                .map(MenuItem::getMealTime)
                .collect(Collectors.toList());
    }

    public boolean updateItem(String productId, String name, String desc, double rating, double price, String mealTime) {
        if (!menu.containsKey(productId)) return false;
        MenuItem item = menu.get(productId);
        item.setName(name);
        item.setDescription(desc);
        item.setRating(rating);
        item.setPrice(price);
        item.setMealTime(mealTime);
        return true;
    }

    // Partial update - only update specific field
    public boolean updateField(String productId, String field, String value) {
        if (!menu.containsKey(productId)) return false;
        MenuItem item = menu.get(productId);
        switch (field) {
            case "name"        -> item.setName(value);
            case "description" -> item.setDescription(value);
            case "rating"      -> item.setRating(Double.parseDouble(value));
            case "price"       -> item.setPrice(Double.parseDouble(value));
            case "mealTime"    -> item.setMealTime(value);
        }
        return true;
    }

    public boolean deleteItem(String productId) {
        if (!menu.containsKey(productId)) return false;
        menu.remove(productId);
        return true;
    }

    public Collection<MenuItem> getItemsByMealTime(String mealTime) {
        if (mealTime.equals("All")) return menu.values();
        return menu.values().stream()
                .filter(item -> item.getMealTime().equalsIgnoreCase(mealTime))
                .collect(Collectors.toList());
    }
}