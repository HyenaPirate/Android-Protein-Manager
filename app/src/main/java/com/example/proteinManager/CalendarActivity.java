package com.example.proteinManager;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.CalendarView;

import androidx.appcompat.app.AppCompatActivity;

public class CalendarActivity extends AppCompatActivity {

    private TextView proteinTextView, carbsTextView, caloriesTextView, stepsTextView;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        CalendarView calendarView = findViewById(R.id.calendarView);
        proteinTextView = findViewById(R.id.tv_proteinValue);
        carbsTextView = findViewById(R.id.tv_carbsValue);
        caloriesTextView = findViewById(R.id.tv_caloriesValue);
        stepsTextView = findViewById(R.id.tv_stepsValue);
        backButton = findViewById(R.id.button_back);

        proteinTextView.setText("90");
        carbsTextView.setText("140");
        caloriesTextView.setText("2000");
        stepsTextView.setText("3500");

        backButton.setOnClickListener(v -> {
            finish();
        });
    }
}
