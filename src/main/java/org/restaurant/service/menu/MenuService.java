package org.restaurant.service.menu;

import org.restaurant.model.menu.MenuItem;
import org.restaurant.repository.menu.MenuRepository;

import java.util.*;

public class MenuService {

    private MenuRepository repo = MenuRepository.getInstance();

    public boolean addMenuItem(String productId, String name, String desc,
                               double rating, double price, String mealTime) {
        return repo.addItem(new MenuItem(productId, name, desc, rating, price, mealTime));
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

    public boolean existsByNameAndMealTime(String name, String mealTime) {
        return repo.existsByNameAndMealTime(name, mealTime);
    }

    public List<String> getMealTimesForName(String name) {
        return repo.getMealTimesForName(name);
    }

    public boolean updateField(String productId, String field, String value) {
        return repo.updateField(productId, field, value);
    }

    public boolean deleteMenuItem(String productId) {
        return repo.deleteItem(productId);
    }
}
