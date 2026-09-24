package com.digcheh.app;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DataManager dataManager;
    private Calendar currentCalendar = Calendar.getInstance();

    // ==================== TAB CONTAINERS ====================
    private View tabViewDiary, tabViewTrends, tabViewRecipes, tabViewProfile;

    // ==================== BOTTOM NAVIGATION WIDGETS ====================
    private LinearLayout navTabDiary, navTabTrends, navTabRecipes, navTabProfile;
    private ImageView ivNavDiary, ivNavTrends, ivNavRecipes, ivNavProfile;
    private TextView tvNavDiary, tvNavTrends, tvNavRecipes, tvNavProfile;

    // ==================== TAB 1: DIARY VIEWS ====================
    private TextView tvHeaderGreeting, tvDietSubtitle, tvHeroDietBadge;
    private TextView tvCheatMealStatus, tvDateDisplay;
    private TextView tvTotalCalories, tvCalEaten, tvCalPercent, tvCalTarget;
    private ProgressBar pbMainCalories;
    private TextView tvProteinCount, tvProteinTarget, tvCarbCount, tvCarbTarget, tvFatCount, tvFatTarget;
    private ProgressBar pbProtein, pbCarb, pbFat;
    private TextView tvBurnedCalories;

    private TextView tvBreakfastCalories, tvLunchCalories, tvDinnerCalories, tvSnacksCalories;
    private LinearLayout llBreakfastItems, llLunchItems, llDinnerItems, llSnacksItems;
    private TextView tvBreakfastEmpty, tvLunchEmpty, tvDinnerEmpty, tvSnacksEmpty;

    // ==================== TAB 2: TRENDS VIEWS ====================
    private TextView tvConsistencyStat, tvWeightTrendText, tvBmiDisplay;
    private ProgressBar pbWeightProgress;

    // ==================== TAB 4: PROFILE VIEWS ====================
    private TextView tvProfileName, tvProfileEmail, tvProfileBMR, tvProfileTDEE;
    private TextView tvProfileDietName, tvProfileDietMacros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataManager = DataManager.getInstance(this);

        if (!dataManager.isUserOnboarded()) {
            startActivity(new Intent(this, OnboardingActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        initViews();
        setupNavigation();
        setupListeners();
        updateDateDisplay();
        refreshAllData();

        FontHelper.applyVazirmatn(getWindow().getDecorView(), this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dataManager != null && dataManager.isUserOnboarded()) {
            refreshAllData();
            FontHelper.applyVazirmatn(getWindow().getDecorView(), this);
        }
    }

    private void initViews() {
        // Tab Containers
        tabViewDiary = findViewById(R.id.tabViewDiary);
        tabViewTrends = findViewById(R.id.tabViewTrends);
        tabViewRecipes = findViewById(R.id.tabViewRecipes);
        tabViewProfile = findViewById(R.id.tabViewProfile);

        // Bottom Nav Items
        navTabDiary = findViewById(R.id.navTabDiary);
        navTabTrends = findViewById(R.id.navTabTrends);
        navTabRecipes = findViewById(R.id.navTabRecipes);
        navTabProfile = findViewById(R.id.navTabProfile);

        ivNavDiary = findViewById(R.id.ivNavDiary);
        ivNavTrends = findViewById(R.id.ivNavTrends);
        ivNavRecipes = findViewById(R.id.ivNavRecipes);
        ivNavProfile = findViewById(R.id.ivNavProfile);

        tvNavDiary = findViewById(R.id.tvNavDiary);
        tvNavTrends = findViewById(R.id.tvNavTrends);
        tvNavRecipes = findViewById(R.id.tvNavRecipes);
        tvNavProfile = findViewById(R.id.tvNavProfile);

        // Diary Header & Bento Matrix
        tvHeaderGreeting = findViewById(R.id.tvHeaderGreeting);
        tvDietSubtitle = findViewById(R.id.tvDietSubtitle);
        tvHeroDietBadge = findViewById(R.id.tvHeroDietBadge);
        tvCheatMealStatus = findViewById(R.id.tvCheatMealStatus);
        tvDateDisplay = findViewById(R.id.tvDateDisplay);

        tvTotalCalories = findViewById(R.id.tvTotalCalories);
        tvCalEaten = findViewById(R.id.tvCalEaten);
        tvCalPercent = findViewById(R.id.tvCalPercent);
        tvCalTarget = findViewById(R.id.tvCalTarget);
        pbMainCalories = findViewById(R.id.pbMainCalories);

        tvProteinCount = findViewById(R.id.tvProteinCount);
        tvProteinTarget = findViewById(R.id.tvProteinTarget);
        tvCarbCount = findViewById(R.id.tvCarbCount);
        tvCarbTarget = findViewById(R.id.tvCarbTarget);
        tvFatCount = findViewById(R.id.tvFatCount);
        tvFatTarget = findViewById(R.id.tvFatTarget);

        pbProtein = findViewById(R.id.pbProtein);
        pbCarb = findViewById(R.id.pbCarb);
        pbFat = findViewById(R.id.pbFat);

        // Meals
        tvBreakfastCalories = findViewById(R.id.tvBreakfastCalories);
        tvLunchCalories = findViewById(R.id.tvLunchCalories);
        tvDinnerCalories = findViewById(R.id.tvDinnerCalories);
        tvSnacksCalories = findViewById(R.id.tvSnacksCalories);

        llBreakfastItems = findViewById(R.id.llBreakfastItems);
        llLunchItems = findViewById(R.id.llLunchItems);
        llDinnerItems = findViewById(R.id.llDinnerItems);
        llSnacksItems = findViewById(R.id.llSnacksItems);

        tvBreakfastEmpty = findViewById(R.id.tvBreakfastEmpty);
        tvLunchEmpty = findViewById(R.id.tvLunchEmpty);
        tvDinnerEmpty = findViewById(R.id.tvDinnerEmpty);
        tvSnacksEmpty = findViewById(R.id.tvSnacksEmpty);

        tvBurnedCalories = findViewById(R.id.tvBurnedCalories);

        // Trends Views
        tvConsistencyStat = findViewById(R.id.tvConsistencyStat);
        tvWeightTrendText = findViewById(R.id.tvWeightTrendText);
        tvBmiDisplay = findViewById(R.id.tvBmiDisplay);
        pbWeightProgress = findViewById(R.id.pbWeightProgress);

        // Profile Views
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        tvProfileBMR = findViewById(R.id.tvProfileBMR);
        tvProfileTDEE = findViewById(R.id.tvProfileTDEE);
        tvProfileDietName = findViewById(R.id.tvProfileDietName);
        tvProfileDietMacros = findViewById(R.id.tvProfileDietMacros);
    }

    private void setupNavigation() {
        navTabDiary.setOnClickListener(v -> switchTab(0));
        navTabTrends.setOnClickListener(v -> switchTab(1));
        navTabRecipes.setOnClickListener(v -> switchTab(2));
        navTabProfile.setOnClickListener(v -> switchTab(3));
    }

    private void switchTab(int tabIndex) {
        tabViewDiary.setVisibility(tabIndex == 0 ? View.VISIBLE : View.GONE);
        tabViewTrends.setVisibility(tabIndex == 1 ? View.VISIBLE : View.GONE);
        tabViewRecipes.setVisibility(tabIndex == 2 ? View.VISIBLE : View.GONE);
        tabViewProfile.setVisibility(tabIndex == 3 ? View.VISIBLE : View.GONE);

        int colorPrimary = ContextCompat.getColor(this, R.color.primary);
        int colorMuted = ContextCompat.getColor(this, R.color.text_secondary);

        // Reset all icons & text
        ivNavDiary.setColorFilter(tabIndex == 0 ? colorPrimary : colorMuted);
        tvNavDiary.setTextColor(tabIndex == 0 ? colorPrimary : colorMuted);

        ivNavTrends.setColorFilter(tabIndex == 1 ? colorPrimary : colorMuted);
        tvNavTrends.setTextColor(tabIndex == 1 ? colorPrimary : colorMuted);

        ivNavRecipes.setColorFilter(tabIndex == 2 ? colorPrimary : colorMuted);
        tvNavRecipes.setTextColor(tabIndex == 2 ? colorPrimary : colorMuted);

        ivNavProfile.setColorFilter(tabIndex == 3 ? colorPrimary : colorMuted);
        tvNavProfile.setTextColor(tabIndex == 3 ? colorPrimary : colorMuted);

        if (tabIndex == 1) {
            refreshTrendsData();
        } else if (tabIndex == 3) {
            refreshProfileData();
        }

        FontHelper.applyVazirmatn(getWindow().getDecorView(), this);
    }

    private void setupListeners() {
        // Diary header click -> edit profile
        findViewById(R.id.layoutBrandHeader).setOnClickListener(v -> {
            Intent intent = new Intent(this, OnboardingActivity.class);
            startActivity(intent);
        });

        // Date navigation
        findViewById(R.id.btnPrevDay).setOnClickListener(v -> {
            currentCalendar.add(Calendar.DAY_OF_YEAR, -1);
            updateDateDisplay();
        });

        findViewById(R.id.btnNextDay).setOnClickListener(v -> {
            currentCalendar.add(Calendar.DAY_OF_YEAR, 1);
            updateDateDisplay();
        });

        // Cheat meal pill popover
        findViewById(R.id.cardCheatMealPill).setOnClickListener(v -> showCheatMealDialog());

        // Meal add food buttons
        findViewById(R.id.btnAddBreakfast).setOnClickListener(v -> showAddFoodBottomSheet("breakfast", "صبحانه"));
        findViewById(R.id.btnAddLunch).setOnClickListener(v -> showAddFoodBottomSheet("lunch", "ناهار"));
        findViewById(R.id.btnAddDinner).setOnClickListener(v -> showAddFoodBottomSheet("dinner", "شام"));
        findViewById(R.id.btnAddSnack).setOnClickListener(v -> showAddFoodBottomSheet("snack", "میان‌وعده‌ها"));

        // Workout log button
        findViewById(R.id.btnAddActivity).setOnClickListener(v -> {
            dataManager.addBurnedCalories(120);
            refreshAllData();
            Toast.makeText(this, "۱۲۰ کیلوکالری فعالیت ورزشی ثبت شد!", Toast.LENGTH_SHORT).show();
        });

        // Trends: Log weight button
        findViewById(R.id.btnLogWeight).setOnClickListener(v -> showLogWeightDialog());

        // Profile buttons
        findViewById(R.id.btnEditBiometrics).setOnClickListener(v -> {
            Intent intent = new Intent(this, OnboardingActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btnChangeDiet).setOnClickListener(v -> {
            Intent intent = new Intent(this, OnboardingActivity.class);
            startActivity(intent);
        });
    }

    private void updateDateDisplay() {
        Calendar today = Calendar.getInstance();
        boolean isToday = today.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == currentCalendar.get(Calendar.DAY_OF_YEAR);

        if (isToday) {
            tvDateDisplay.setText("امروز، " + toPersianDigits(currentCalendar.get(Calendar.DAY_OF_MONTH)) + " " + getMonthName(currentCalendar.get(Calendar.MONTH)));
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
            tvDateDisplay.setText(toPersianDigits(sdf.format(currentCalendar.getTime())));
        }
    }

    private String getMonthName(int month) {
        String[] months = {"فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور", "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"};
        int persianMonthIdx = (month + 9) % 12;
        return months[persianMonthIdx];
    }

    private void refreshAllData() {
        UserProfile profile = dataManager.getUserProfile();
        int calorieGoal = profile.getTargetCalories();
        int proteinGoal = profile.getTargetProtein();
        int carbGoal = profile.getTargetCarbs();
        int fatGoal = profile.getTargetFat();

        tvHeaderGreeting.setText("سلام، " + profile.getName());
        tvDietSubtitle.setText("رژیم: " + profile.getDietNameFa());
        if (tvHeroDietBadge != null) {
            tvHeroDietBadge.setText(profile.getDietNameFa());
        }

        // 1. Cheat meal status
        int cheatDays = dataManager.getCheatCycleDays();
        tvCheatMealStatus.setText("چیت‌میل: " + toPersianDigits(cheatDays) + " روز دیگر");

        // 2. Bento Matrix Calculations
        int totalConsumed = dataManager.getTotalConsumedCalories();
        int totalBurned = dataManager.getBurnedCalories();
        int netEaten = Math.max(0, totalConsumed - totalBurned);
        int remainingCalories = Math.max(0, calorieGoal - netEaten);

        tvTotalCalories.setText(toPersianDigits(remainingCalories));
        tvCalEaten.setText("مصرفی: " + toPersianDigits(totalConsumed));

        int percent = calorieGoal > 0 ? (int) Math.round((totalConsumed * 100.0) / calorieGoal) : 0;
        tvCalPercent.setText(toPersianDigits(percent) + "٪ پر شده");
        tvCalTarget.setText("سقف: " + toPersianDigits(calorieGoal));

        pbMainCalories.setMax(calorieGoal);
        pbMainCalories.setProgress(Math.min(totalConsumed, calorieGoal));

        // Macros
        int totalProt = dataManager.getTotalProtein();
        int totalCarb = dataManager.getTotalCarbs();
        int totalFat = dataManager.getTotalFat();

        tvProteinCount.setText(toPersianDigits(totalProt) + "g");
        tvProteinTarget.setText("هدف: " + toPersianDigits(proteinGoal) + "g");
        pbProtein.setMax(proteinGoal);
        pbProtein.setProgress(Math.min(totalProt, proteinGoal));

        tvCarbCount.setText(toPersianDigits(totalCarb) + "g");
        tvCarbTarget.setText("هدف: " + toPersianDigits(carbGoal) + "g");
        pbCarb.setMax(carbGoal);
        pbCarb.setProgress(Math.min(totalCarb, carbGoal));

        tvFatCount.setText(toPersianDigits(totalFat) + "g");
        tvFatTarget.setText("هدف: " + toPersianDigits(fatGoal) + "g");
        pbFat.setMax(fatGoal);
        pbFat.setProgress(Math.min(totalFat, fatGoal));

        // Burned calories
        tvBurnedCalories.setText(toPersianDigits(totalBurned) + " kcal سوزانده‌شده");

        // 3. Render 4 Meals
        renderMeal("breakfast", llBreakfastItems, tvBreakfastCalories, tvBreakfastEmpty);
        renderMeal("lunch", llLunchItems, tvLunchCalories, tvLunchEmpty);
        renderMeal("dinner", llDinnerItems, tvDinnerCalories, tvDinnerEmpty);
        renderMeal("snack", llSnacksItems, tvSnacksCalories, tvSnacksEmpty);

        refreshTrendsData();
        refreshProfileData();

        FontHelper.applyVazirmatn(getWindow().getDecorView(), this);
    }

    private void refreshTrendsData() {
        UserProfile profile = dataManager.getUserProfile();
        double w = profile.getWeightKg();
        double tw = profile.getTargetWeightKg();
        double hM = profile.getHeightCm() / 100.0;
        double bmi = (hM > 0) ? (w / (hM * hM)) : 22.0;

        String bmiStatus = bmi < 18.5 ? "کم‌وزن" : bmi < 25.0 ? "طبیعی و متناسب" : bmi < 30.0 ? "اضافه‌وزن خفیف" : "چاقی";
        tvBmiDisplay.setText("شاخص توده بدنی (BMI): " + String.format(Locale.US, "%.1f", bmi) + " (" + bmiStatus + ")");

        double diff = w - tw;
        if (diff > 0) {
            tvWeightTrendText.setText("وزن فعلی: " + toPersianDigits((int) w) + " kg | هدف: " + toPersianDigits((int) tw) + " kg (" + toPersianDigits((int) diff) + " کیلوگرم تا هدف)");
        } else {
            tvWeightTrendText.setText("وزن فعلی: " + toPersianDigits((int) w) + " kg | هدف: " + toPersianDigits((int) tw) + " kg (در محدوده تثبیت)");
        }

        int progress = (int) Math.min(100, Math.max(15, 100 - (diff * 10)));
        pbWeightProgress.setProgress(progress);
    }

    private void refreshProfileData() {
        UserProfile profile = dataManager.getUserProfile();
        tvProfileName.setText(profile.getName());
        tvProfileEmail.setText(profile.getEmail());
        tvProfileBMR.setText(toPersianDigits(profile.getBmr()) + " kcal");
        tvProfileTDEE.setText(toPersianDigits(profile.getTdee()) + " kcal");
        tvProfileDietName.setText(profile.getDietNameFa());

        String macrosRatio = "۵۰٪ کربوهیدرات | ۲۰٪ پروتئین | ۳۰٪ چربی";
        if ("keto".equalsIgnoreCase(profile.getDietType())) {
            macrosRatio = "۷۰٪ چربی سالم | ۲۵٪ پروتئین | ۵٪ کربوهیدرات";
        } else if ("high_protein".equalsIgnoreCase(profile.getDietType())) {
            macrosRatio = "۳۵٪ پروتئین | ۴۰٪ کربوهیدرات | ۲۵٪ چربی";
        } else if ("mediterranean".equalsIgnoreCase(profile.getDietType())) {
            macrosRatio = "۴۵٪ کربوهیدرات | ۲۰٪ پروتئین | ۳۵٪ چربی";
        }
        tvProfileDietMacros.setText(macrosRatio);
    }

    private void renderMeal(String mealType, LinearLayout container, TextView tvCalories, TextView tvEmpty) {
        container.removeAllViews();
        List<LoggedFood> foods = dataManager.getLoggedFoodsForMeal(mealType);

        int mealCals = dataManager.getMealCalories(mealType);
        tvCalories.setText(toPersianDigits(mealCals) + " kcal");

        if (foods.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            LayoutInflater inflater = LayoutInflater.from(this);

            for (LoggedFood food : foods) {
                View itemView = inflater.inflate(R.layout.item_meal_food, container, false);
                FontHelper.applyVazirmatn(itemView, this);

                TextView tvName = itemView.findViewById(R.id.tvFoodName);
                TextView tvQty = itemView.findViewById(R.id.tvFoodQuantity);
                TextView tvFoodCals = itemView.findViewById(R.id.tvFoodCalories);
                ImageView ivChevron = itemView.findViewById(R.id.ivChevron);
                LinearLayout layoutExpanded = itemView.findViewById(R.id.layoutMacrosExpanded);
                TextView tvProtBadge = itemView.findViewById(R.id.tvProteinBadge);
                TextView tvCarbBadge = itemView.findViewById(R.id.tvCarbBadge);
                TextView tvFatBadge = itemView.findViewById(R.id.tvFatBadge);
                ImageButton btnDelete = itemView.findViewById(R.id.btnDeleteFood);

                tvName.setText(food.getFoodName());
                tvQty.setText(toPersianDigits((int) Math.round(food.getQuantity())) + " " + food.getUnit());
                tvFoodCals.setText(toPersianDigits(food.getCalories()) + " kcal");

                tvProtBadge.setText("پروتئین: " + toPersianDigits(food.getProtein()) + "g");
                tvCarbBadge.setText("کربو: " + toPersianDigits(food.getCarbs()) + "g");
                tvFatBadge.setText("چربی: " + toPersianDigits(food.getFat()) + "g");

                // Accordion Progressive Disclosure
                itemView.setOnClickListener(v -> {
                    if (layoutExpanded.getVisibility() == View.VISIBLE) {
                        layoutExpanded.setVisibility(View.GONE);
                        ivChevron.setRotation(0f);
                    } else {
                        layoutExpanded.setVisibility(View.VISIBLE);
                        ivChevron.setRotation(180f);
                    }
                });

                // Delete Food Item
                btnDelete.setOnClickListener(v -> {
                    dataManager.removeLoggedFood(food.getId());
                    refreshAllData();
                    Toast.makeText(this, "غذا حذف شد", Toast.LENGTH_SHORT).show();
                });

                container.addView(itemView);
            }
        }
    }

    private void showAddFoodBottomSheet(String mealType, String mealTitleFa) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View sheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_add_food, null);
        FontHelper.applyVazirmatn(sheetView, this);
        dialog.setContentView(sheetView);

        TextView tvSheetTitle = sheetView.findViewById(R.id.tvSheetTitle);
        tvSheetTitle.setText("افزودن غذا به " + mealTitleFa);

        EditText etSearch = sheetView.findViewById(R.id.etSearchFood);
        RecyclerView rvResults = sheetView.findViewById(R.id.rvFoodSearchResults);
        LinearLayout layoutConfig = sheetView.findViewById(R.id.layoutFoodConfig);
        TextView tvSelectedFoodTitle = sheetView.findViewById(R.id.tvSelectedFoodTitle);
        EditText etQuantity = sheetView.findViewById(R.id.etFoodQuantity);
        Spinner spUnit = sheetView.findViewById(R.id.spFoodUnit);
        TextView tvLiveCalories = sheetView.findViewById(R.id.tvLiveCalculatedCalories);
        MaterialButton btnAdd = sheetView.findViewById(R.id.btnAddFoodToMeal);

        final FoodItem[] selectedFood = new FoodItem[1];

        rvResults.setLayoutManager(new LinearLayoutManager(this));
        FoodSearchAdapter adapter = new FoodSearchAdapter(dataManager.getFoodCatalog(), food -> {
            selectedFood[0] = food;
            layoutConfig.setVisibility(View.VISIBLE);
            tvSelectedFoodTitle.setText(food.getName());

            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, food.getAvailableUnits());
            spUnit.setAdapter(spinnerAdapter);

            updateCalculatedPreview(selectedFood[0], etQuantity, tvLiveCalories);
            FontHelper.applyVazirmatn(layoutConfig, this);
        });
        rvResults.setAdapter(adapter);

        com.google.android.material.chip.ChipGroup chipGroup = sheetView.findViewById(R.id.chipGroupCategories);
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipCatStews) adapter.setCategory("خورش‌ها");
            else if (checkedId == R.id.chipCatRice) adapter.setCategory("برنج و پلو");
            else if (checkedId == R.id.chipCatDrinks) adapter.setCategory("نوشیدنی و کافه");
            else if (checkedId == R.id.chipCatKebabs) adapter.setCategory("کباب‌ها");
            else if (checkedId == R.id.chipCatBreakfast) adapter.setCategory("صبحانه و لبنیات");
            else if (checkedId == R.id.chipCatTrad) adapter.setCategory("سنتی و آش");
            else if (checkedId == R.id.chipCatFast) adapter.setCategory("فست‌فود");
            else if (checkedId == R.id.chipCatSnack) adapter.setCategory("میوه و میان‌وعده");
            else adapter.setCategory("همه");
        });

        spUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (selectedFood[0] != null) {
                    updateCalculatedPreview(selectedFood[0], etQuantity, tvLiveCalories);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        etQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (selectedFood[0] != null) {
                    updateCalculatedPreview(selectedFood[0], etQuantity, tvLiveCalories);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnAdd.setOnClickListener(v -> {
            if (selectedFood[0] == null) return;

            double qty = 1.0;
            try {
                qty = Double.parseDouble(etQuantity.getText().toString().trim());
            } catch (Exception ignored) {}

            String unit = spUnit.getSelectedItem() != null ? spUnit.getSelectedItem().toString() : selectedFood[0].getDefaultUnit();
            int cals = (int) Math.round(selectedFood[0].getCaloriesPerUnit() * qty);
            int prot = (int) Math.round(selectedFood[0].getProtein() * qty);
            int carb = (int) Math.round(selectedFood[0].getCarbs() * qty);
            int fat = (int) Math.round(selectedFood[0].getFat() * qty);

            LoggedFood logged = new LoggedFood(selectedFood[0].getId(), selectedFood[0].getName(), qty, unit, cals, prot, carb, fat, mealType);
            dataManager.addLoggedFood(logged);
            refreshAllData();

            dialog.dismiss();
            Toast.makeText(this, selectedFood[0].getName() + " به " + mealTitleFa + " اضافه شد", Toast.LENGTH_SHORT).show();
        });

        if (dialog.getWindow() != null) {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        dialog.show();
    }

    private void updateCalculatedPreview(FoodItem food, EditText etQty, TextView tvPreview) {
        double qty = 1.0;
        try {
            qty = Double.parseDouble(etQty.getText().toString().trim());
        } catch (Exception ignored) {}

        int cals = (int) Math.round(food.getCaloriesPerUnit() * qty);
        int prot = (int) Math.round(food.getProtein() * qty);
        int carb = (int) Math.round(food.getCarbs() * qty);
        int fat = (int) Math.round(food.getFat() * qty);

        tvPreview.setText("کالری: " + toPersianDigits(cals) + " kcal | پروتئین: " + toPersianDigits(prot) + "g | کربوهیدرات: " + toPersianDigits(carb) + "g");
    }

    private void showCheatMealDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_cheat_meal, null);
        FontHelper.applyVazirmatn(dialogView, this);
        dialog.setContentView(dialogView);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        RadioGroup rg = dialog.findViewById(R.id.rgCheatInterval);
        RadioButton rb3 = dialog.findViewById(R.id.rbCheat3Days);
        RadioButton rb5 = dialog.findViewById(R.id.rbCheat5Days);
        RadioButton rb7 = dialog.findViewById(R.id.rbCheat7Days);
        RadioButton rb14 = dialog.findViewById(R.id.rbCheat14Days);

        int currentDays = dataManager.getCheatCycleDays();
        if (currentDays == 5) rb5.setChecked(true);
        else if (currentDays == 7) rb7.setChecked(true);
        else if (currentDays == 14) rb14.setChecked(true);
        else rb3.setChecked(true);

        dialog.findViewById(R.id.btnCancelCheat).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.btnSaveCheat).setOnClickListener(v -> {
            int selectedDays = 3;
            int checkedId = rg.getCheckedRadioButtonId();
            if (checkedId == R.id.rbCheat5Days) selectedDays = 5;
            else if (checkedId == R.id.rbCheat7Days) selectedDays = 7;
            else if (checkedId == R.id.rbCheat14Days) selectedDays = 14;

            dataManager.setCheatCycleDays(selectedDays);
            refreshAllData();
            dialog.dismiss();
            Toast.makeText(this, "چرخه چیت‌میل ذخیره شد!", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    private void showLogWeightDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_cheat_meal, null);

        // Simple prompt using AlertDialog
        android.widget.EditText input = new android.widget.EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint("مثلاً ۷۵.۵");
        input.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        input.setHintTextColor(ContextCompat.getColor(this, R.color.text_hint));
        input.setText(String.valueOf(dataManager.getUserProfile().getWeightKg()));

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("⚖️ ثبت وزن امروز")
                .setMessage("وزن دقیق امروز خود را بر حسب کیلوگرم وارد کنید:")
                .setView(input)
                .setPositiveButton("ذخیره", (d, which) -> {
                    try {
                        double newWeight = Double.parseDouble(input.getText().toString().trim());
                        UserProfile p = dataManager.getUserProfile();
                        p.setWeightKg(newWeight);
                        p.calculateMetabolism();
                        dataManager.saveUserProfile(p);
                        refreshAllData();
                        Toast.makeText(this, "وزن جدید ذخیره شد و متابولیسم به‌روزرسانی شد!", Toast.LENGTH_SHORT).show();
                    } catch (Exception ignored) {}
                })
                .setNegativeButton("انصراف", null)
                .show();
    }

    private String toPersianDigits(int number) {
        char[] persianDigits = {'۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹'};
        String str = String.valueOf(number);
        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (Character.isDigit(c)) {
                sb.append(persianDigits[c - '0']);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private String toPersianDigits(String str) {
        if (str == null) return "";
        char[] persianDigits = {'۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹'};
        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (Character.isDigit(c)) {
                sb.append(persianDigits[c - '0']);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
