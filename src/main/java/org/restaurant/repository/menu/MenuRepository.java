package org.restaurant.repository.menu;
import org.restaurant.model.menu.MenuItem;
import java.util.*;
import java.util.stream.Collectors;

public class MenuRepository {
    private static MenuRepository instance;
    private Map<String, MenuItem> menu = new HashMap<>();

    private MenuRepository() {}

    public static MenuRepository getInstance() {
        if (instance == null) {
            instance = new MenuRepository();
        }
        return instance;
    }

    public boolean addItem(MenuItem item) {
        if (menu.containsKey(item.getProductId())) return false;
        menu.put(item.getProductId(), item);
        return true;
    }

    public Collection<MenuItem> getAllItems() {
        return menu.values();
    }

    public MenuItem getItemByProductId(String productId) {
        return menu.get(productId);
    }

    public boolean existsByNameAndMealTime(String name, String mealTime) {
        return menu.values().stream()
                .anyMatch(item -> item.getName().equalsIgnoreCase(name)
                        && item.getMealTime().equalsIgnoreCase(mealTime));
    }

    public List<String> getMealTimesForName(String name) {
        return menu.values().stream()
                .filter(item -> item.getName().equalsIgnoreCase(name))
                .map(MenuItem::getMealTime)
                .collect(Collectors.toList());
    }

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