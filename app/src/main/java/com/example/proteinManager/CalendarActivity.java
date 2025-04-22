package com.example.proteinManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    private TextView proteinTextView, carbsTextView, caloriesTextView, stepsTextView;
    private ListView productList;
    private CalendarView calendarView;
    private ImageButton backButton;

    private int totalProteins =0, totalCarbs =0, totalCalories =0, totalSteps = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarView = findViewById(R.id.calendarView);
        proteinTextView = findViewById(R.id.tv_proteinValue);
        carbsTextView = findViewById(R.id.tv_carbsValue);
        caloriesTextView = findViewById(R.id.tv_caloriesValue);
        stepsTextView = findViewById(R.id.tv_stepsValue);
        backButton = findViewById(R.id.button_back);
        productList = findViewById(R.id.listView_productList);

        backButton.setOnClickListener(v -> finish());

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> updateDate(year+"-"+(month+1)+"-"+dayOfMonth));

    }

    private void updateDate(String dateSelected){
        Log.d("Calendar", dateSelected);
        ArrayList<String> consumedProducts = loadProductsList(this, dateSelected);
        //productList = consumedProducts;
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
        UpdateCounters();

        return productList;
    }
}
