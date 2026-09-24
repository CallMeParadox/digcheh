package com.digcheh.app;

import java.util.UUID;

public class LoggedFood {
    private String id;
    private String foodId;
    private String foodName;
    private double quantity;
    private String unit;
    private int calories;
    private int protein;
    private int carbs;
    private int fat;
    private String mealType;

    public LoggedFood(String foodId, String foodName, double quantity, String unit, int calories, int protein, int carbs, int fat, String mealType) {
        this.id = UUID.randomUUID().toString();
        this.foodId = foodId;
        this.foodName = foodName;
        this.quantity = quantity;
        this.unit = unit;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.mealType = mealType;
    }

    public String getId() { return id; }
    public String getFoodId() { return foodId; }
    public String getFoodName() { return foodName; }
    public double getQuantity() { return quantity; }
    public String getUnit() { return unit; }
    public int getCalories() { return calories; }
    public int getProtein() { return protein; }
    public int getCarbs() { return carbs; }
    public int getFat() { return fat; }
    public String getMealType() { return mealType; }
}
