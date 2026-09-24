package com.digcheh.app;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class OnboardingActivity extends AppCompatActivity {

    private EditText etUserName, etUserEmail;
    private EditText etAge, etHeight, etWeight, etTargetWeight;

    // Gender Selection Cards
    private MaterialCardView cardGenderMale, cardGenderFemale;
    private TextView tvGenderMaleText, tvGenderFemaleText;
    private String selectedGender = "male";

    // Activity Selection Cards
    private MaterialCardView cardActSedentary, cardActLight, cardActModerate, cardActActive;
    private TextView tvActSedentaryTitle, tvActLightTitle, tvActModerateTitle, tvActActiveTitle;
    private String selectedActivity = "light";

    // Diet Selection Cards
    private MaterialCardView cardDietBalanced, cardDietHighProtein, cardDietKeto, cardDietMed, cardDietCustom;
    private TextView tvDietBalancedTitle, tvDietHighProteinTitle, tvDietKetoTitle, tvDietMedTitle, tvDietCustomTitle;
    private String selectedDiet = "balanced";

    // Dynamic Live Preview
    private TextView tvLiveBMR, tvLiveTDEE, tvLiveTargetCalories, tvLiveMacrosPreview;
    private MaterialButton btnSubmit;

    private DataManager dataManager;
    private UserProfile tempProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        dataManager = DataManager.getInstance(this);
        tempProfile = dataManager.getUserProfile();

        initViews();
        loadExistingValues();
        setupListeners();
        updateGenderSelection();
        updateActivitySelection();
        updateDietSelection();
        recalculateAndPreview();

        FontHelper.applyVazirmatn(getWindow().getDecorView(), this);
    }

    private void initViews() {
        etUserName = findViewById(R.id.etUserName);
        etUserEmail = findViewById(R.id.etUserEmail);
        etAge = findViewById(R.id.etAge);
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        etTargetWeight = findViewById(R.id.etTargetWeight);

        // Gender
        cardGenderMale = findViewById(R.id.cardGenderMale);
        cardGenderFemale = findViewById(R.id.cardGenderFemale);
        tvGenderMaleText = findViewById(R.id.tvGenderMaleText);
        tvGenderFemaleText = findViewById(R.id.tvGenderFemaleText);

        // Activity
        cardActSedentary = findViewById(R.id.cardActSedentary);
        cardActLight = findViewById(R.id.cardActLight);
        cardActModerate = findViewById(R.id.cardActModerate);
        cardActActive = findViewById(R.id.cardActActive);

        tvActSedentaryTitle = findViewById(R.id.tvActSedentaryTitle);
        tvActLightTitle = findViewById(R.id.tvActLightTitle);
        tvActModerateTitle = findViewById(R.id.tvActModerateTitle);
        tvActActiveTitle = findViewById(R.id.tvActActiveTitle);

        // Diet
        cardDietBalanced = findViewById(R.id.cardDietBalanced);
        cardDietHighProtein = findViewById(R.id.cardDietHighProtein);
        cardDietKeto = findViewById(R.id.cardDietKeto);
        cardDietMed = findViewById(R.id.cardDietMed);
        cardDietCustom = findViewById(R.id.cardDietCustom);

        tvDietBalancedTitle = findViewById(R.id.tvDietBalancedTitle);
        tvDietHighProteinTitle = findViewById(R.id.tvDietHighProteinTitle);
        tvDietKetoTitle = findViewById(R.id.tvDietKetoTitle);
        tvDietMedTitle = findViewById(R.id.tvDietMedTitle);
        tvDietCustomTitle = findViewById(R.id.tvDietCustomTitle);

        // Live preview
        tvLiveBMR = findViewById(R.id.tvLiveBMR);
        tvLiveTDEE = findViewById(R.id.tvLiveTDEE);
        tvLiveTargetCalories = findViewById(R.id.tvLiveTargetCalories);
        tvLiveMacrosPreview = findViewById(R.id.tvLiveMacrosPreview);
        btnSubmit = findViewById(R.id.btnSubmitOnboarding);
    }

    private void loadExistingValues() {
        if (tempProfile != null) {
            etUserName.setText(tempProfile.getName());
            etUserEmail.setText(tempProfile.getEmail());
            selectedGender = tempProfile.getGender() != null ? tempProfile.getGender() : "male";
            selectedActivity = tempProfile.getActivityLevel() != null ? tempProfile.getActivityLevel() : "light";
            selectedDiet = tempProfile.getDietType() != null ? tempProfile.getDietType() : "balanced";

            etAge.setText(String.valueOf(tempProfile.getAge() > 0 ? tempProfile.getAge() : 26));
            etHeight.setText(String.valueOf(tempProfile.getHeightCm() > 0 ? (int) tempProfile.getHeightCm() : 178));
            etWeight.setText(String.valueOf(tempProfile.getWeightKg() > 0 ? (int) tempProfile.getWeightKg() : 78));
            etTargetWeight.setText(String.valueOf(tempProfile.getTargetWeightKg() > 0 ? (int) tempProfile.getTargetWeightKg() : 72));
        }
    }

    private void setupListeners() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                recalculateAndPreview();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };

        etAge.addTextChangedListener(watcher);
        etHeight.addTextChangedListener(watcher);
        etWeight.addTextChangedListener(watcher);
        etTargetWeight.addTextChangedListener(watcher);

        // Gender clicks
        cardGenderMale.setOnClickListener(v -> {
            selectedGender = "male";
            updateGenderSelection();
            recalculateAndPreview();
        });

        cardGenderFemale.setOnClickListener(v -> {
            selectedGender = "female";
            updateGenderSelection();
            recalculateAndPreview();
        });

        // Activity clicks
        cardActSedentary.setOnClickListener(v -> {
            selectedActivity = "sedentary";
            updateActivitySelection();
            recalculateAndPreview();
        });
        cardActLight.setOnClickListener(v -> {
            selectedActivity = "light";
            updateActivitySelection();
            recalculateAndPreview();
        });
        cardActModerate.setOnClickListener(v -> {
            selectedActivity = "moderate";
            updateActivitySelection();
            recalculateAndPreview();
        });
        cardActActive.setOnClickListener(v -> {
            selectedActivity = "active";
            updateActivitySelection();
            recalculateAndPreview();
        });

        // Diet clicks
        cardDietBalanced.setOnClickListener(v -> {
            selectedDiet = "balanced";
            updateDietSelection();
            recalculateAndPreview();
        });
        cardDietHighProtein.setOnClickListener(v -> {
            selectedDiet = "high_protein";
            updateDietSelection();
            recalculateAndPreview();
        });
        cardDietKeto.setOnClickListener(v -> {
            selectedDiet = "keto";
            updateDietSelection();
            recalculateAndPreview();
        });
        cardDietMed.setOnClickListener(v -> {
            selectedDiet = "mediterranean";
            updateDietSelection();
            recalculateAndPreview();
        });
        cardDietCustom.setOnClickListener(v -> {
            selectedDiet = "custom";
            updateDietSelection();
            recalculateAndPreview();
        });

        btnSubmit.setOnClickListener(v -> saveAndProceed());
    }

    private void updateGenderSelection() {
        boolean isMale = "male".equalsIgnoreCase(selectedGender);
        setCardStyle(cardGenderMale, tvGenderMaleText, isMale);
        setCardStyle(cardGenderFemale, tvGenderFemaleText, !isMale);
    }

    private void updateActivitySelection() {
        setCardStyle(cardActSedentary, tvActSedentaryTitle, "sedentary".equalsIgnoreCase(selectedActivity));
        setCardStyle(cardActLight, tvActLightTitle, "light".equalsIgnoreCase(selectedActivity));
        setCardStyle(cardActModerate, tvActModerateTitle, "moderate".equalsIgnoreCase(selectedActivity));
        setCardStyle(cardActActive, tvActActiveTitle, "active".equalsIgnoreCase(selectedActivity));
    }

    private void updateDietSelection() {
        setCardStyle(cardDietBalanced, tvDietBalancedTitle, "balanced".equalsIgnoreCase(selectedDiet));
        setCardStyle(cardDietHighProtein, tvDietHighProteinTitle, "high_protein".equalsIgnoreCase(selectedDiet));
        setCardStyle(cardDietKeto, tvDietKetoTitle, "keto".equalsIgnoreCase(selectedDiet));
        setCardStyle(cardDietMed, tvDietMedTitle, "mediterranean".equalsIgnoreCase(selectedDiet));
        setCardStyle(cardDietCustom, tvDietCustomTitle, "custom".equalsIgnoreCase(selectedDiet));
    }

    private void setCardStyle(MaterialCardView card, TextView title, boolean isSelected) {
        int colorPrimary = ContextCompat.getColor(this, R.color.primary);
        int colorPrimaryLight = ContextCompat.getColor(this, R.color.primary_light);
        int colorCardBorder = ContextCompat.getColor(this, R.color.card_border);
        int colorWhite = ContextCompat.getColor(this, R.color.surface_white);
        int colorTextPrimary = ContextCompat.getColor(this, R.color.text_primary);

        if (isSelected) {
            card.setStrokeWidth(dpToPx(2));
            card.setStrokeColor(colorPrimary);
            card.setCardBackgroundColor(colorPrimaryLight);
            if (title != null) {
                title.setTextColor(colorPrimary);
            }
        } else {
            card.setStrokeWidth(dpToPx(1));
            card.setStrokeColor(colorCardBorder);
            card.setCardBackgroundColor(colorWhite);
            if (title != null) {
                title.setTextColor(colorTextPrimary);
            }
        }
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private void recalculateAndPreview() {
        try {
            tempProfile.setName(etUserName.getText().toString().trim());
            tempProfile.setEmail(etUserEmail.getText().toString().trim());
            tempProfile.setGender(selectedGender);

            String ageStr = etAge.getText().toString().trim();
            String hStr = etHeight.getText().toString().trim();
            String wStr = etWeight.getText().toString().trim();
            String twStr = etTargetWeight.getText().toString().trim();

            double age = ageStr.isEmpty() ? 26 : Double.parseDouble(ageStr);
            double height = hStr.isEmpty() ? 178 : Double.parseDouble(hStr);
            double weight = wStr.isEmpty() ? 78 : Double.parseDouble(wStr);
            double targetWeight = twStr.isEmpty() ? 72 : Double.parseDouble(twStr);

            tempProfile.setAge((int) age);
            tempProfile.setHeightCm(height);
            tempProfile.setWeightKg(weight);
            tempProfile.setTargetWeightKg(targetWeight);
            tempProfile.setActivityLevel(selectedActivity);
            tempProfile.setDietType(selectedDiet);

            tempProfile.calculateMetabolism();

            int bmr = (int) Math.round(tempProfile.getBmr());
            int tdee = (int) Math.round(tempProfile.getTdee());
            int targetCals = (int) Math.round(tempProfile.getTargetCalories());

            tvLiveBMR.setText("متابولیسم پایه (BMR): " + toPersianDigits(bmr) + " kcal");
            tvLiveTDEE.setText("مصرف روزانه (TDEE): " + toPersianDigits(tdee) + " kcal");
            tvLiveTargetCalories.setText("🎯 کالری هدف روزانه: " + toPersianDigits(targetCals) + " kcal");
            tvLiveMacrosPreview.setText("پروتئین: " + toPersianDigits(tempProfile.getTargetProtein()) + "g | کربوهیدرات: " +
                    toPersianDigits(tempProfile.getTargetCarbs()) + "g | چربی: " + toPersianDigits(tempProfile.getTargetFat()) + "g");

        } catch (Exception ignored) {}
    }

    private void saveAndProceed() {
        String name = etUserName.getText().toString().trim();
        String email = etUserEmail.getText().toString().trim();

        if (name.isEmpty()) name = "کاربر دیگچه";
        if (email.isEmpty()) email = "user@digcheh.ir";

        tempProfile.setName(name);
        tempProfile.setEmail(email);
        tempProfile.setGender(selectedGender);
        tempProfile.setActivityLevel(selectedActivity);
        tempProfile.setDietType(selectedDiet);
        tempProfile.calculateMetabolism();

        dataManager.saveUserProfile(tempProfile);
        dataManager.setUserOnboarded(true);

        Toast.makeText(this, "پروفایل متابولیک شما با موفقیت ذخیره شد!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private String toPersianDigits(int value) {
        String[] faDigits = {"۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹"};
        String str = String.format("%,d", value);
        for (int i = 0; i < 10; i++) {
            str = str.replace(String.valueOf(i), faDigits[i]);
        }
        return str;
    }
}
