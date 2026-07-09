package org.restaurant.service.menu;

import org.restaurant.model.menu.MenuItem;
import org.restaurant.repository.menu.MenuRepository;

import java.util.*;

public class MenuService {

    private MenuRepository repo = MenuRepository.getInstance();

    public boolean addMenuItem(String productId, String name, String desc,
                               double rating, double price, List<Integer> mealTimeIds) {
        // The mealTimes list inside MenuItem is for display, so we leave it empty here.
        // The mealTimeIds are what will be saved in the database mappings.
        return repo.addItem(new MenuItem(productId, name, desc, rating, price, new ArrayList<>()), mealTimeIds);
    }

    public Collection<MenuItem> getMenuItems() {
        return repo.getAllItems();
    }

    public Collection<MenuItem> getMenuItemsByMealTime(String mealTime) {
        return repo.getItemsByMealTime(mealTime);
    }

    public MenuItem getItemByProductId(String productId) {
        return repo.getItemByProductId(productId);
    }

    public boolean updateField(String productId, String field, String value) {
        return repo.updateField(productId, field, value);
    }

    public boolean updateMealTimes(String productId, List<Integer> mealTimeIds) {
        return repo.updateMealTimes(productId, mealTimeIds);
    }

    public boolean deleteMenuItem(String productId) {
        return repo.deleteItem(productId);
    }
}
