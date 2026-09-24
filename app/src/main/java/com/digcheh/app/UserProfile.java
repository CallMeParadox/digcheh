package com.digcheh.app;

public class UserProfile {
    private String name;
    private String email;
    private String gender; // "male" or "female"
    private int age;
    private double heightCm;
    private double weightKg;
    private double targetWeightKg;
    private String activityLevel; // "sedentary", "light", "moderate", "active"
    private String dietType; // "balanced", "keto", "high_protein", "mediterranean", "custom"

    private int bmr;
    private int tdee;
    private int targetCalories;
    private int targetProtein;
    private int targetCarbs;
    private int targetFat;

    public UserProfile() {
        // Defaults
        this.name = "کاربر دیگچه";
        this.email = "user@digcheh.ir";
        this.gender = "male";
        this.age = 26;
        this.heightCm = 178;
        this.weightKg = 75;
        this.targetWeightKg = 72;
        this.activityLevel = "moderate";
        this.dietType = "balanced";
        calculateMetabolism();
    }

    public void calculateMetabolism() {
        // 1. Mifflin-St Jeor BMR Equation
        if ("female".equalsIgnoreCase(gender)) {
            this.bmr = (int) Math.round((10 * weightKg) + (6.25 * heightCm) - (5 * age) - 161);
        } else {
            this.bmr = (int) Math.round((10 * weightKg) + (6.25 * heightCm) - (5 * age) + 5);
        }

        // 2. Activity Multiplier
        double factor = 1.2;
        if ("light".equalsIgnoreCase(activityLevel)) factor = 1.375;
        else if ("moderate".equalsIgnoreCase(activityLevel)) factor = 1.55;
        else if ("active".equalsIgnoreCase(activityLevel)) factor = 1.725;

        this.tdee = (int) Math.round(this.bmr * factor);

        // 3. Goal Adjustment (Loss, Maintenance, Gain)
        double diff = weightKg - targetWeightKg;
        if (diff > 1.0) {
            // Weight Loss
            this.targetCalories = Math.max("female".equalsIgnoreCase(gender) ? 1200 : 1500, this.tdee - 450);
        } else if (diff < -1.0) {
            // Muscle / Weight Gain
            this.targetCalories = this.tdee + 350;
        } else {
            // Maintenance
            this.targetCalories = this.tdee;
        }

        // 4. Macro Calculation based on Diet Type
        double protRatio = 0.20, carbRatio = 0.50, fatRatio = 0.30;

        if ("keto".equalsIgnoreCase(dietType)) {
            // Ketogenic: 70% Fat, 25% Protein, 5% Carbs
            protRatio = 0.25;
            carbRatio = 0.05;
            fatRatio = 0.70;
        } else if ("high_protein".equalsIgnoreCase(dietType)) {
            // High Protein / Fitness: 35% Protein, 40% Carbs, 25% Fat
            protRatio = 0.35;
            carbRatio = 0.40;
            fatRatio = 0.25;
        } else if ("mediterranean".equalsIgnoreCase(dietType)) {
            // Mediterranean: 20% Protein, 45% Carbs, 35% Fat
            protRatio = 0.20;
            carbRatio = 0.45;
            fatRatio = 0.35;
        }

        this.targetProtein = (int) Math.round((this.targetCalories * protRatio) / 4.0);
        this.targetCarbs = (int) Math.round((this.targetCalories * carbRatio) / 4.0);
        this.targetFat = (int) Math.round((this.targetCalories * fatRatio) / 9.0);
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public double getHeightCm() { return heightCm; }
    public void setHeightCm(double heightCm) { this.heightCm = heightCm; }
    public double getWeightKg() { return weightKg; }
    public void setWeightKg(double weightKg) { this.weightKg = weightKg; }
    public double getTargetWeightKg() { return targetWeightKg; }
    public void setTargetWeightKg(double targetWeightKg) { this.targetWeightKg = targetWeightKg; }
    public String getActivityLevel() { return activityLevel; }
    public void setActivityLevel(String activityLevel) { this.activityLevel = activityLevel; }
    public String getDietType() { return dietType; }
    public void setDietType(String dietType) { this.dietType = dietType; }

    public int getBmr() { return bmr; }
    public int getTdee() { return tdee; }
    public int getTargetCalories() { return targetCalories; }
    public int getTargetProtein() { return targetProtein; }
    public int getTargetCarbs() { return targetCarbs; }
    public int getTargetFat() { return targetFat; }

    public String getDietNameFa() {
        if ("keto".equalsIgnoreCase(dietType)) return "کتوژنیک (چربی بالا / کربو کم)";
        if ("high_protein".equalsIgnoreCase(dietType)) return "پروتئین بالا / فیتنس";
        if ("mediterranean".equalsIgnoreCase(dietType)) return "مدیترانه‌ای";
        if ("custom".equalsIgnoreCase(dietType)) return "شخصی‌سازی شده";
        return "بالانس استاندارد سالم";
    }
}
