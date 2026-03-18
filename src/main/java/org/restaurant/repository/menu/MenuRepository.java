package org.restaurant.repository.menu;
import org.restaurant.model.menu.MenuItem;
import java.util.*;

public class MenuRepository {
    private Map<String, MenuItem> menu = new HashMap<>();

    public boolean addItem(MenuItem item) {
        if (menu.containsKey(item.getName())) return false;
        menu.put(item.getName(), item);
        return true;
    }

    public Collection<MenuItem> getAllItems() {
        return menu.values();
    }

    public boolean updateItem(String name, String desc, double rating, String mealTime) {
        if (!menu.containsKey(name)) return false;
        menu.put(name, new MenuItem(name, desc, rating, mealTime));
        return true;
    }

    public boolean deleteItem(String name) {
        if (!menu.containsKey(name)) return false;
        menu.remove(name);
        return true;
    }
}