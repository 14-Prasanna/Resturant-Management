package org.restaurant.service.menu;
import org.restaurant.model.menu.MenuItem;
import org.restaurant.repository.menu.MenuRepository;
import java.util.*;

public class MenuService {
    private MenuRepository repo = new MenuRepository();

    public boolean addMenuItem(String name, String desc, double rating, String mealTime) {
        return repo.addItem(new MenuItem(name, desc, rating, mealTime));
    }

    public Collection<MenuItem> getMenuItems() {
        return repo.getAllItems();
    }

    public boolean updateMenuItem(String name, String desc, double rating, String mealTime) {
        return repo.updateItem(name, desc, rating, mealTime);
    }

    public boolean deleteMenuItem(String name) {
        return repo.deleteItem(name);
    }
}