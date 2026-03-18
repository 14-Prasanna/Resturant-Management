package org.restaurant.model.menu;

public class MenuItem {
    private String name;
    private String description;
    private double rating;
    private String mealTime; 

    public MenuItem(String name, String description, double rating, String mealTime) {
        this.name = name;
        this.description = description;
        this.rating = rating;
        this.mealTime = mealTime;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getRating() { return rating; }
    public String getMealTime() { return mealTime; } // NEW
}