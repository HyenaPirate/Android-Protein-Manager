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

import com.google.gson.JsonObject;

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




    private void saveNumberToJson() {
        String input = sampleNumberText.getText().toString().trim();
        if (input.isEmpty()) {
            sampleNumberText.setError("Enter a number");
            return;
        }
        JsonObject numberJson = new JsonObject();
        numberJson.addProperty("number", Integer.parseInt(input));

        JsonManager manager = new JsonManager();
        manager.saveJSONObject(this, "numberJson", numberJson);

    }

    private void loadNumberFromJson() {
        JsonManager manager = new JsonManager();
        JsonObject obj = manager.readJSONObject(this, "numberJson");
        if (obj != null) {
            int number = obj.get("number").getAsInt();
            showMessage("Loaded", "Saved number: " + number);
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
