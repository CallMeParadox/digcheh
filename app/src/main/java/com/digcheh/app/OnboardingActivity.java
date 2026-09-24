package com.digcheh.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class OnboardingActivity extends AppCompatActivity {

    private EditText etUserName, etUserEmail;
    private RadioGroup rgGender;
    private EditText etAge, etHeight, etWeight, etTargetWeight;
    private Spinner spActivityLevel;
    private RadioGroup rgDietType;
    private TextView tvLiveTargetCalories, tvLiveMacrosPreview;
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
        setupActivitySpinner();
        setupListeners();
        recalculateAndPreview();
    }

    private void initViews() {
        etUserName = findViewById(R.id.etUserName);
        etUserEmail = findViewById(R.id.etUserEmail);
        rgGender = findViewById(R.id.rgGender);
        etAge = findViewById(R.id.etAge);
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        etTargetWeight = findViewById(R.id.etTargetWeight);
        spActivityLevel = findViewById(R.id.spActivityLevel);
        rgDietType = findViewById(R.id.rgDietType);
        tvLiveTargetCalories = findViewById(R.id.tvLiveTargetCalories);
        tvLiveMacrosPreview = findViewById(R.id.tvLiveMacrosPreview);
        btnSubmit = findViewById(R.id.btnSubmitOnboarding);

        // Prepopulate with existing or defaults
        etUserName.setText(tempProfile.getName());
        etUserEmail.setText(tempProfile.getEmail());
        if ("female".equalsIgnoreCase(tempProfile.getGender())) {
            rgGender.check(R.id.rbFemale);
        } else {
            rgGender.check(R.id.rbMale);
        }
        etAge.setText(String.valueOf(tempProfile.getAge()));
        etHeight.setText(String.valueOf((int) tempProfile.getHeightCm()));
        etWeight.setText(String.valueOf((int) tempProfile.getWeightKg()));
        etTargetWeight.setText(String.valueOf((int) tempProfile.getTargetWeightKg()));
    }

    private void setupActivitySpinner() {
        String[] activityLevels = {
                "کم‌تحرک (پشت‌میزنشین و بدون ورزش)",
                "فعالیت کم (۱ تا ۲ روز تمرین در هفته)",
                "فعالیت متوسط (۳ تا ۵ روز ورزش در هفته)",
                "فعالیت شدید (ورزشکار روزانه و تمرین سنگین)"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, activityLevels);
        spActivityLevel.setAdapter(adapter);
        spActivityLevel.setSelection(1); // default light/moderate
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

        rgGender.setOnCheckedChangeListener((group, checkedId) -> recalculateAndPreview());

        spActivityLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                recalculateAndPreview();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        rgDietType.setOnCheckedChangeListener((group, checkedId) -> recalculateAndPreview());

        btnSubmit.setOnClickListener(v -> saveAndProceed());
    }

    private void recalculateAndPreview() {
        try {
            tempProfile.setName(etUserName.getText().toString().trim());
            tempProfile.setEmail(etUserEmail.getText().toString().trim());
            tempProfile.setGender(rgGender.getCheckedRadioButtonId() == R.id.rbFemale ? "female" : "male");

            double age = Double.parseDouble(etAge.getText().toString().trim());
            double height = Double.parseDouble(etHeight.getText().toString().trim());
            double weight = Double.parseDouble(etWeight.getText().toString().trim());
            double targetWeight = Double.parseDouble(etTargetWeight.getText().toString().trim());

            tempProfile.setAge((int) age);
            tempProfile.setHeightCm(height);
            tempProfile.setWeightKg(weight);
            tempProfile.setTargetWeightKg(targetWeight);

            int pos = spActivityLevel.getSelectedItemPosition();
            if (pos == 0) tempProfile.setActivityLevel("sedentary");
            else if (pos == 1) tempProfile.setActivityLevel("light");
            else if (pos == 2) tempProfile.setActivityLevel("moderate");
            else tempProfile.setActivityLevel("active");

            int dietId = rgDietType.getCheckedRadioButtonId();
            if (dietId == R.id.rbDietKeto) tempProfile.setDietType("keto");
            else if (dietId == R.id.rbDietHighProtein) tempProfile.setDietType("high_protein");
            else if (dietId == R.id.rbDietMed) tempProfile.setDietType("mediterranean");
            else if (dietId == R.id.rbDietCustom) tempProfile.setDietType("custom");
            else tempProfile.setDietType("balanced");

            tempProfile.calculateMetabolism();

            tvLiveTargetCalories.setText("کالری روزانه شما: " + toPersianDigits(tempProfile.getTargetCalories()) + " kcal");
            tvLiveMacrosPreview.setText("پروتئین: " + toPersianDigits(tempProfile.getTargetProtein()) + "g | " +
                    "کربوهیدرات: " + toPersianDigits(tempProfile.getTargetCarbs()) + "g | " +
                    "چربی: " + toPersianDigits(tempProfile.getTargetFat()) + "g");
        } catch (Exception ignored) {
            // Keep default values if parsing empty inputs
        }
    }

    private void saveAndProceed() {
        recalculateAndPreview();
        dataManager.saveUserProfile(tempProfile);
        Toast.makeText(this, "پروفایل رژیم با موفقیت ساخته شد!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
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
}
