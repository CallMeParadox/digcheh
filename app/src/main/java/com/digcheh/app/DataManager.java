package com.digcheh.app;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataManager {
    private static final String PREF_NAME = "digcheh_prefs";
    private static final String KEY_LOGGED_FOODS = "logged_foods";
    private static final String KEY_CHEAT_DAYS = "cheat_days";
    private static final String KEY_BURNED_CALS = "burned_cals";

    private static DataManager instance;
    private final SharedPreferences prefs;
    private final Gson gson;
    private final List<FoodItem> foodCatalog = new ArrayList<>();
    private final List<LoggedFood> loggedFoods = new ArrayList<>();
    private int cheatCycleDays = 3;
    private int burnedCalories = 150;

    private DataManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        loadFoodCatalog(context);
        loadSavedData();
    }

    public static synchronized DataManager getInstance(Context context) {
        if (instance == null) {
            instance = new DataManager(context);
        }
        return instance;
    }

    private void loadFoodCatalog(Context context) {
        foodCatalog.clear();
        // Fallback robust Iranian foods catalog
        addFoodToCatalog("1", "قورمه‌سبزی با برنج", "غذای سنتی", 480, 22, 58, 18, "بشقاب", Arrays.asList("بشقاب", "قاشق برنج + خورش", "پرس"));
        addFoodToCatalog("2", "قیمه سیب‌زمینی با برنج", "غذای سنتی", 495, 20, 62, 19, "بشقاب", Arrays.asList("بشقاب", "قاشق", "پرس"));
        addFoodToCatalog("3", "زرشک‌پلو با مرغ", "غذای سنتی", 530, 32, 65, 14, "پرس", Arrays.asList("پرس", "ران کامل با برنج", "سینه با برنج"));
        addFoodToCatalog("4", "چلو کباب کوبیده", "کباب", 640, 36, 52, 28, "سیخ با برنج", Arrays.asList("سیخ با برنج", "یک سیخ تنها", "دو سیخ با برنج"));
        addFoodToCatalog("5", "جوجه‌کباب با برنج", "کباب", 490, 38, 50, 12, "سیخ با برنج", Arrays.asList("سیخ با برنج", "یک سیخ بدون برنج"));
        addFoodToCatalog("6", "نان سنگک", "نان", 80, 3, 16, 1, "کف دست", Arrays.asList("کف دست", "تکه (نصف نان)", "یک نان کامل"));
        addFoodToCatalog("7", "نان بربری", "نان", 85, 3, 17, 1, "کف دست", Arrays.asList("کف دست", "تکه", "یک نان کامل"));
        addFoodToCatalog("8", "نان لواش", "نان", 35, 1, 7, 0, "کف دست", Arrays.asList("کف دست", "یک نان کامل"));
        addFoodToCatalog("9", "پنیر تبریزی / فتا", "لبنیات", 65, 4, 1, 5, "قوطی کبریت", Arrays.asList("قوطی کبریت (۳۰ گرم)", "گرم"));
        addFoodToCatalog("10", "گردو", "مغزیجات", 35, 1, 1, 3, "عدد (نصفه)", Arrays.asList("عدد (نصفه)", "گرم"));
        addFoodToCatalog("11", "تخم‌مرغ آب‌پز", "صبحانه", 75, 6, 1, 5, "عدد", Arrays.asList("عدد", "دو عدد"));
        addFoodToCatalog("12", "تخم‌مرغ نیمرو", "صبحانه", 115, 6, 1, 10, "عدد", Arrays.asList("عدد", "دو عدد با روغن"));
        addFoodToCatalog("13", "املت گوجه‌فرنگی", "صبحانه", 220, 10, 12, 14, "پرس متوسط", Arrays.asList("پرس متوسط", "بشقاب"));
        addFoodToCatalog("14", "آش رشته با کشک", "غذای سنتی", 320, 12, 45, 10, "کاسه", Arrays.asList("کاسه", "کاسه بزرگ", "ملاقه"));
        addFoodToCatalog("15", "عدس‌پلو با کشمش", "پلوها", 420, 14, 68, 8, "بشقاب", Arrays.asList("بشقاب", "قاشق"));
        addFoodToCatalog("16", "لوبیاپلو با گوشت", "پلوها", 460, 18, 64, 14, "بشقاب", Arrays.asList("بشقاب", "قاشق"));
        addFoodToCatalog("17", "کتلت گوشت خانگی", "غذای خانگی", 140, 8, 9, 8, "عدد", Arrays.asList("عدد", "ساندویچ با نان"));
        addFoodToCatalog("18", "کوکو سبزی", "غذای خانگی", 120, 5, 8, 8, "برش", Arrays.asList("برش", "ساندویچ"));
        addFoodToCatalog("19", "سالاد شیرازی", "سالاد", 45, 1, 8, 1, "کاسه", Arrays.asList("کاسه", "پیش‌دستی"));
        addFoodToCatalog("20", "ماست کم‌چرب", "لبنیات", 60, 4, 6, 2, "پیاله", Arrays.asList("پیاله", "لیوان"));
        addFoodToCatalog("21", "سیب درختی", "میوه", 70, 0, 18, 0, "عدد متوسط", Arrays.asList("عدد متوسط", "عدد بزرگ"));
        addFoodToCatalog("22", "موز", "میوه", 105, 1, 27, 0, "عدد", Arrays.asList("عدد", "عدد کوچک"));
        addFoodToCatalog("23", "چای با خرما", "میان‌وعده", 45, 0, 11, 0, "فنجان با ۲ خرما", Arrays.asList("فنجان با ۲ خرما", "استکان با ۱ خرما"));
        addFoodToCatalog("24", "بادام درختی", "مغزیجات", 8, 0, 0, 1, "عدد", Arrays.asList("عدد", "مشت (۳۰ گرم)"));
    }

    private void addFoodToCatalog(String id, String name, String category, int cals, int prot, int carbs, int fat, String defaultUnit, List<String> units) {
        foodCatalog.add(new FoodItem(id, name, category, cals, prot, carbs, fat, defaultUnit, units));
    }

    private void loadSavedData() {
        cheatCycleDays = prefs.getInt(KEY_CHEAT_DAYS, 3);
        burnedCalories = prefs.getInt(KEY_BURNED_CALS, 150);
        String json = prefs.getString(KEY_LOGGED_FOODS, null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<LoggedFood>>() {}.getType();
            List<LoggedFood> saved = gson.fromJson(json, type);
            if (saved != null) {
                loggedFoods.addAll(saved);
            }
        } else {
            // Initial realistic demo state
            loggedFoods.add(new LoggedFood("6", "نان سنگک", 2, "کف دست", 160, 6, 32, 2, "breakfast"));
            loggedFoods.add(new LoggedFood("9", "پنیر تبریزی / فتا", 1, "قوطی کبریت", 65, 4, 1, 5, "breakfast"));
            loggedFoods.add(new LoggedFood("23", "چای با خرما", 1, "فنجان با ۲ خرما", 45, 0, 11, 0, "snack"));
            loggedFoods.add(new LoggedFood("1", "قورمه‌سبزی با برنج", 0.8, "بشقاب", 380, 18, 46, 14, "lunch"));
            saveFoods();
        }
    }

    public List<FoodItem> getFoodCatalog() {
        return foodCatalog;
    }

    public List<LoggedFood> getLoggedFoodsForMeal(String mealType) {
        List<LoggedFood> result = new ArrayList<>();
        for (LoggedFood f : loggedFoods) {
            if (f.getMealType().equalsIgnoreCase(mealType)) {
                result.add(f);
            }
        }
        return result;
    }

    public void addLoggedFood(LoggedFood food) {
        loggedFoods.add(food);
        saveFoods();
    }

    public void removeLoggedFood(String id) {
        for (int i = 0; i < loggedFoods.size(); i++) {
            if (loggedFoods.get(i).getId().equals(id)) {
                loggedFoods.remove(i);
                break;
            }
        }
        saveFoods();
    }

    private void saveFoods() {
        String json = gson.toJson(loggedFoods);
        prefs.edit().putString(KEY_LOGGED_FOODS, json).apply();
    }

    public int getTotalConsumedCalories() {
        int sum = 0;
        for (LoggedFood f : loggedFoods) {
            sum += f.getCalories();
        }
        return sum;
    }

    public int getTotalProtein() {
        int sum = 0;
        for (LoggedFood f : loggedFoods) {
            sum += f.getProtein();
        }
        return sum;
    }

    public int getTotalCarbs() {
        int sum = 0;
        for (LoggedFood f : loggedFoods) {
            sum += f.getCarbs();
        }
        return sum;
    }

    public int getTotalFat() {
        int sum = 0;
        for (LoggedFood f : loggedFoods) {
            sum += f.getFat();
        }
        return sum;
    }

    public int getMealCalories(String mealType) {
        int sum = 0;
        for (LoggedFood f : loggedFoods) {
            if (f.getMealType().equalsIgnoreCase(mealType)) {
                sum += f.getCalories();
            }
        }
        return sum;
    }

    public int getCheatCycleDays() {
        return cheatCycleDays;
    }

    public void setCheatCycleDays(int days) {
        this.cheatCycleDays = days;
        prefs.edit().putInt(KEY_CHEAT_DAYS, days).apply();
    }

    public int getBurnedCalories() {
        return burnedCalories;
    }

    public void addBurnedCalories(int cals) {
        this.burnedCalories += cals;
        prefs.edit().putInt(KEY_BURNED_CALS, burnedCalories).apply();
    }
}
