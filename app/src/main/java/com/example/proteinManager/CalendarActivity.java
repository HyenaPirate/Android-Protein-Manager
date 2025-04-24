package com.example.proteinManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    private TextView proteinTextView, carbsTextView, caloriesTextView, stepsTextView;
    private ListView productListView;
    private ArrayList<String> productList;
    private ArrayAdapter<String> adapter;
    private ImageButton backButton;

    private int totalProteins =0, totalCarbs =0, totalCalories =0, totalSteps = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        productListView = findViewById(R.id.listView_productList);
        CalendarView calendarView = findViewById(R.id.calendarView);
        backButton = findViewById(R.id.button_back);

        proteinTextView = findViewById(R.id.tv_proteinValue);
        carbsTextView = findViewById(R.id.tv_carbsValue);
        caloriesTextView = findViewById(R.id.tv_caloriesValue);
        stepsTextView = findViewById(R.id.tv_stepsValue);

        backButton.setOnClickListener(v -> finish());

        productList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productList);
        productListView.setAdapter(adapter);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            @SuppressLint("DefaultLocale") String formattedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
            Log.i("Calendar", formattedDate);
            updateDate(formattedDate);
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate today = null;
            today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String selectedDate = today.format(formatter);
            updateDate(selectedDate);
        }

    }

    private void updateDate(String dateSelected){
        Log.d("Calendar", dateSelected);
        productList.clear();
        ArrayList<String> consumedProducts = loadProductsList(this, dateSelected);
        productList.addAll(consumedProducts);
        adapter.notifyDataSetChanged();
    }

    private void UpdateCounters(){
        proteinTextView.setText(String.valueOf(totalProteins));
        carbsTextView.setText(String.valueOf(totalCarbs));
        caloriesTextView.setText(String.valueOf(totalCalories));
        stepsTextView.setText(String.valueOf(totalSteps));
    }

    private ArrayList<String> loadProductsList(Context context, String dateSelected) {

        totalProteins = 0;
        totalCarbs = 0;
        totalCalories = 0;
        totalSteps = 0;

        UpdateCounters();

        ArrayList<String> productList = new ArrayList<>();
        JsonManager jsonManager = new JsonManager();
        JsonArray calendarArray = jsonManager.readJSONArray(context, "calendar");
        if (calendarArray == null) {
            productList.add("No calendar file.");
            return productList;
        }


        JsonArray todayIds = null;
        for (JsonElement entry : calendarArray) {
            JsonObject day = entry.getAsJsonObject();
            if (day.has("date") && dateSelected.equals(day.get("date").getAsString())) {
                todayIds = day.getAsJsonArray("productsConsumed");
                if (day.has("stepsCounted")) totalSteps += day.get("stepsCounted").getAsInt();
                break;
            }
        }

        if (todayIds == null || todayIds.isEmpty()) {
            productList.add("No products consumed today");
            return productList;
        }

        // Step 3: Load products.json to match IDs
        JsonArray productsArray = jsonManager.readJSONArray(context, "products");
        if (productsArray == null) {
            productList.add("Failed to load products");
            return productList;
        }

        for (JsonElement idElement : todayIds) {
            int productId = idElement.getAsInt();

            for (JsonElement productEl : productsArray) {
                JsonObject product = productEl.getAsJsonObject();
                if (!product.has("productId") || product.get("productId").getAsInt() != productId) {
                    continue;
                }

                String name = product.get("productName").getAsString();
                productList.add(name);

                JsonObject nutrients = product.getAsJsonObject("productNutrients");
                if (nutrients == null) {
                    break;
                }
                if (nutrients.has("productProtein")) totalProteins += nutrients.get("productProtein").getAsInt();
                if (nutrients.has("productCalories")) totalCalories += nutrients.get("productCalories").getAsInt();
                if (nutrients.has("productCarbohydrates")) totalCarbs += nutrients.get("productCarbohydrates").getAsInt();


            }
        }
        Log.d("Calendar", "Protein before update: " + totalProteins);
        UpdateCounters();
        Log.d("Calendar", "Protein after update: " + proteinTextView.getText());

        return productList;
    }
}
