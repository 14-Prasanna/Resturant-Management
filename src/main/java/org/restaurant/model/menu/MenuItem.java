package org.restaurant.model.menu;

import java.util.List;

public class MenuItem {
    private String productId;
    private String name;
    private String description;
    private double rating;
    private double price;
    private List<String> mealTimes;

    public MenuItem(String productId, String name, String description, double rating, double price, List<String> mealTimes) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.rating = rating;
        this.price = price;
        this.mealTimes = mealTimes;
    }

    public String getProductId() { return productId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getRating() { return rating; }
    public double getPrice() { return price; }
    public List<String> getMealTimes() { return mealTimes; }

    // Setters for partial update
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setRating(double rating) { this.rating = rating; }
    public void setPrice(double price) { this.price = price; }
    public void setMealTimes(List<String> mealTimes) { this.mealTimes = mealTimes; }

    @Override
    public String toString() {
        return "[" + String.join(", ", mealTimes) + "]" +
                " " + name + " | " + description +
                " | Price: ₹" + price + " | Rating: " + rating;
    }
}