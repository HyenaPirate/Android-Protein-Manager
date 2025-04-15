package com.example.proteinManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    private TextView proteinTextView, carbsTextView, caloriesTextView, stepsTextView;
    private EditText editTargetProtein, editConsumedProtein;
    private Button saveButton;
    private CalendarView calendarView;
    private ImageButton backButton;

    private String selectedDate;

    private HashMap<String, ProteinData> proteinDataMap;
    private SharedPreferences sharedPreferences;
    private Gson gson = new Gson();
    private static final String PREF_NAME = "ProteinDataPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarView = findViewById(R.id.calendarView);
        proteinTextView = findViewById(R.id.tv_proteinValue);
        carbsTextView = findViewById(R.id.tv_carbsValue);
        caloriesTextView = findViewById(R.id.tv_caloriesValue);
        stepsTextView = findViewById(R.id.tv_stepsValue);
        editTargetProtein = findViewById(R.id.edit_targetProtein);
        editConsumedProtein = findViewById(R.id.edit_consumedProtein);
        saveButton = findViewById(R.id.button_saveProtein);
        backButton = findViewById(R.id.button_back);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        loadProteinData();

        selectedDate = getCurrentDateString(calendarView.getDate());
        updateUIForDate(selectedDate);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = formatDate(year, month, dayOfMonth);
            updateUIForDate(selectedDate);
        });

        saveButton.setOnClickListener(v -> {
            int target = parseInput(editTargetProtein.getText().toString());
            int consumed = parseInput(editConsumedProtein.getText().toString());

            proteinDataMap.put(selectedDate, new ProteinData(target, consumed));
            saveProteinData();

            updateUIForDate(selectedDate);
        });

        backButton.setOnClickListener(v -> finish());
    }

    private void updateUIForDate(String dateKey) {
        ProteinData data = proteinDataMap.get(dateKey);
        if (data != null) {
            proteinTextView.setText(getString(R.string.protein_display, data.consumedProtein, data.targetProtein));
            editTargetProtein.setText(String.valueOf(data.targetProtein));
            editConsumedProtein.setText(String.valueOf(data.consumedProtein));
        } else {
            proteinTextView.setText(getString(R.string.protein_display, 0, 0));
            editTargetProtein.setText("");
            editConsumedProtein.setText("");
        }

        // Placeholder values for other macros – you can later replace with real logic
        carbsTextView.setText("140");
        caloriesTextView.setText("2000");
        stepsTextView.setText("3500");
    }

    private String getCurrentDateString(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date(millis));
    }

    private String formatDate(int year, int month, int day) {
        return String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day);
    }

    private void loadProteinData() {
        String json = sharedPreferences.getString("proteinDataMap", null);
        if (json != null) {
            Type type = new TypeToken<HashMap<String, ProteinData>>() {}.getType();
            proteinDataMap = gson.fromJson(json, type);
        } else {
            proteinDataMap = new HashMap<>();
        }
    }

    private void saveProteinData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(proteinDataMap);
        editor.putString("proteinDataMap", json);
        editor.apply();
    }

    private int parseInput(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
