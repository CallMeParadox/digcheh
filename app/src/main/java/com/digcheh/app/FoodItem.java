package com.digcheh.app;

import java.util.ArrayList;
import java.util.List;

public class FoodItem {
    private String id;
    private String name;
    private String category;
    private int caloriesPerUnit;
    private int protein;
    private int carbs;
    private int fat;
    private String defaultUnit;
    private List<String> availableUnits;

    public FoodItem(String id, String name, String category, int caloriesPerUnit, int protein, int carbs, int fat, String defaultUnit, List<String> availableUnits) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.caloriesPerUnit = caloriesPerUnit;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.defaultUnit = defaultUnit;
        this.availableUnits = availableUnits != null ? availableUnits : new ArrayList<>();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public int getCaloriesPerUnit() { return caloriesPerUnit; }
    public int getProtein() { return protein; }
    public int getCarbs() { return carbs; }
    public int getFat() { return fat; }
    public String getDefaultUnit() { return defaultUnit; }
    public List<String> getAvailableUnits() { return availableUnits; }
}
