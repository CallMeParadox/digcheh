package com.digcheh.app;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
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
    private static final int CALORIE_GOAL = 2100;
    private static final int PROTEIN_GOAL = 110;
    private static final int CARB_GOAL = 220;
    private static final int FAT_GOAL = 65;

    // UI Views
    private TextView tvCheatMealStatus, tvDateDisplay;
    private TextView tvTotalCalories, tvCalorieTarget;
    private ProgressBar pbMainCalories;
    private TextView tvProteinCount, tvCarbCount, tvFatCount;
    private ProgressBar pbProtein, pbCarb, pbFat;
    private TextView tvBurnedCalories;

    // Meal containers
    private TextView tvBreakfastCalories, tvLunchCalories, tvDinnerCalories, tvSnacksCalories;
    private LinearLayout llBreakfastItems, llLunchItems, llDinnerItems, llSnacksItems;
    private TextView tvBreakfastEmpty, tvLunchEmpty, tvDinnerEmpty, tvSnacksEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataManager = DataManager.getInstance(this);

        initViews();
        setupListeners();
        updateDateDisplay();
        refreshAllData();
    }

    private void initViews() {
        tvCheatMealStatus = findViewById(R.id.tvCheatMealStatus);
        tvDateDisplay = findViewById(R.id.tvDateDisplay);

        tvTotalCalories = findViewById(R.id.tvTotalCalories);
        tvCalorieTarget = findViewById(R.id.tvCalorieTarget);
        pbMainCalories = findViewById(R.id.pbMainCalories);

        tvProteinCount = findViewById(R.id.tvProteinCount);
        tvCarbCount = findViewById(R.id.tvCarbCount);
        tvFatCount = findViewById(R.id.tvFatCount);

        pbProtein = findViewById(R.id.pbProtein);
        pbCarb = findViewById(R.id.pbCarb);
        pbFat = findViewById(R.id.pbFat);

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
    }

    private void setupListeners() {
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

        // Activity log button
        findViewById(R.id.btnAddActivity).setOnClickListener(v -> {
            dataManager.addBurnedCalories(100);
            refreshAllData();
            Toast.makeText(this, "۱۰۰ کیلوکالری فعالیت ثبت شد!", Toast.LENGTH_SHORT).show();
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
        // Gregorian to Persian mapping approximation for display
        return "مهر";
    }

    private void refreshAllData() {
        // 1. Cheat meal status
        int cheatDays = dataManager.getCheatCycleDays();
        tvCheatMealStatus.setText("چیت‌میل: " + toPersianDigits(cheatDays) + " روز دیگر");

        // 2. Bento Matrix Calculations
        int totalConsumed = dataManager.getTotalConsumedCalories();
        int totalBurned = dataManager.getBurnedCalories();
        int netCalories = Math.max(0, totalConsumed - totalBurned);

        tvTotalCalories.setText(toPersianDigits(netCalories));
        tvCalorieTarget.setText("از " + toPersianDigits(CALORIE_GOAL) + " kcal");
        pbMainCalories.setMax(CALORIE_GOAL);
        pbMainCalories.setProgress(Math.min(netCalories, CALORIE_GOAL));

        // Macros
        int totalProt = dataManager.getTotalProtein();
        int totalCarb = dataManager.getTotalCarbs();
        int totalFat = dataManager.getTotalFat();

        tvProteinCount.setText(toPersianDigits(totalProt) + "g / " + toPersianDigits(PROTEIN_GOAL) + "g");
        pbProtein.setMax(PROTEIN_GOAL);
        pbProtein.setProgress(Math.min(totalProt, PROTEIN_GOAL));

        tvCarbCount.setText(toPersianDigits(totalCarb) + "g / " + toPersianDigits(CARB_GOAL) + "g");
        pbCarb.setMax(CARB_GOAL);
        pbCarb.setProgress(Math.min(totalCarb, CARB_GOAL));

        tvFatCount.setText(toPersianDigits(totalFat) + "g / " + toPersianDigits(FAT_GOAL) + "g");
        pbFat.setMax(FAT_GOAL);
        pbFat.setProgress(Math.min(totalFat, FAT_GOAL));

        // Burned calories
        tvBurnedCalories.setText(toPersianDigits(totalBurned) + " kcal سوزانده‌شده");

        // 3. Render 4 Meals
        renderMeal("breakfast", llBreakfastItems, tvBreakfastCalories, tvBreakfastEmpty);
        renderMeal("lunch", llLunchItems, tvLunchCalories, tvLunchEmpty);
        renderMeal("dinner", llDinnerItems, tvDinnerCalories, tvDinnerEmpty);
        renderMeal("snack", llSnacksItems, tvSnacksCalories, tvSnacksEmpty);
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
                tvCarbBadge.setText("کربوهیدرات: " + toPersianDigits(food.getCarbs()) + "g");
                tvFatBadge.setText("چربی: " + toPersianDigits(food.getFat()) + "g");

                // Progressive Disclosure (Accordion)
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

        // Search adapter setup
        rvResults.setLayoutManager(new LinearLayoutManager(this));
        FoodSearchAdapter adapter = new FoodSearchAdapter(dataManager.getFoodCatalog(), food -> {
            selectedFood[0] = food;
            layoutConfig.setVisibility(View.VISIBLE);
            tvSelectedFoodTitle.setText(food.getName());

            // Unit spinner
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, food.getAvailableUnits());
            spUnit.setAdapter(spinnerAdapter);

            updateCalculatedPreview(selectedFood[0], etQuantity, tvLiveCalories);
        });
        rvResults.setAdapter(adapter);

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
        dialog.setContentView(R.layout.dialog_cheat_meal);
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
