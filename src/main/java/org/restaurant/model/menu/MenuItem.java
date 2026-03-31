package org.restaurant.model.menu;

public class MenuItem {
    private String productId;
    private String name;
    private String description;
    private double rating;
    private double price;
    private String mealTime;

    public MenuItem(String productId, String name, String description, double rating, double price, String mealTime) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.rating = rating;
        this.price = price;
        this.mealTime = mealTime;
    }

    public String getProductId() { return productId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getRating() { return rating; }
    public double getPrice() { return price; }
    public String getMealTime() { return mealTime; }

    // Setters for partial update
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setRating(double rating) { this.rating = rating; }
    public void setPrice(double price) { this.price = price; }
    public void setMealTime(String mealTime) { this.mealTime = mealTime; }
}