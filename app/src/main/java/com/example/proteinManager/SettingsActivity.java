package com.example.proteinManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.io.File;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Button buttonChangeLanguage;
    private ImageButton buttonBack;
    private androidx.appcompat.widget.SwitchCompat switchDarkMode;

    private Button loadDataButton;
    private Button saveDataButton;
    private EditText sampleNumberText;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        buttonBack = findViewById(R.id.buttonBack);
        buttonChangeLanguage = findViewById(R.id.buttonChangeLanguage);
        switchDarkMode = findViewById(R.id.switch_dark_mode);

        boolean isDarkMode = sharedPreferences.getBoolean("DarkMode", false);
        switchDarkMode.setChecked(isDarkMode);

        setAppTheme(isDarkMode);

        buttonBack.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        buttonChangeLanguage.setOnClickListener(v -> showLanguageDialog());

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setAppTheme(isChecked);
            editor.putBoolean("DarkMode", isChecked);
            editor.apply();
        });
        loadDataButton = findViewById(R.id.buttonLoadData);
        saveDataButton = findViewById(R.id.buttonSaveData);
        sampleNumberText = findViewById(R.id.sampleTextPanel);

        saveDataButton.setOnClickListener(v -> saveNumberToJson());
        loadDataButton.setOnClickListener(v -> loadNumberFromJson());

    }

    private void showLanguageDialog() {
        String[] languages = {"English", "Polski"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Language:")
                .setItems(languages, (dialog, which) -> {
                    String langCode = (which == 0) ? "en" : "pl";
                    changeLanguage(langCode);
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void changeLanguage(String langCode) {
        editor.putString("Language", langCode);
        editor.apply();

        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        recreate();
    }

    private void setAppTheme(boolean isDarkMode) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private File getJsonFile() {
        return new File(getExternalFilesDir(null), "number_data.json");
    }

    private void saveNumberToJson() {
        String input = sampleNumberText.getText().toString().trim();
        if (input.isEmpty()) {
            sampleNumberText.setError("Enter a number");
            return;
        }

        int number = Integer.parseInt(input);
        NumberData data = new NumberData(number);

        com.google.gson.Gson gson = new com.google.gson.Gson();
        String json = gson.toJson(data);

        File file = getJsonFile();
        try {
            java.io.FileWriter writer = new java.io.FileWriter(file);
            writer.write(json);
            writer.close();
            showMessage("Saved", "Number saved to file!");
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Error", "Failed to save file.");
        }
    }

    private void loadNumberFromJson() {
        File file = getJsonFile();
        if (!file.exists()) {
            showMessage("Not Found", "No saved data found.");
            return;
        }

        try {
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file));
            NumberData data = new com.google.gson.Gson().fromJson(reader, NumberData.class);
            reader.close();

            showMessage("Loaded", "Saved number: " + data.number);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Error", "Failed to load file.");
        }
    }

    private void showMessage(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

}
