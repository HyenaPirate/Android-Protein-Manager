package com.example.proteinManager;
import android.content.Context;
import android.content.res.Configuration;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Button;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "AppSettings";
    private static final String PREF_LANGUAGE = "Language";
    private static final String PREF_DARK_MODE = "DarkMode";
    private static final int ADD_PRODUCT_REQUEST = 1;
    private ArrayList<String> productList;
    private ArrayAdapter<String> adapter;

    private TextView stepsValueTextView;
    private TextView proteinValueTextView;
    private TextView carbsValueTextView;
    private TextView caloriesValueTextView;
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private SensorEventListener stepSensorListener;
    private int stepsCount = 0;

    private int totalProteins = 0;
    private int totalCarbs = 0;
    private int totalCalories = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String email = prefs.getString("email", null);
        String password = prefs.getString("password", null);

        if (email == null || password == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        String language = sharedPreferences.getString(PREF_LANGUAGE, "en");
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        setContentView(R.layout.activity_main);
        boolean isDarkMode = sharedPreferences.getBoolean(PREF_DARK_MODE, false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        setContentView(R.layout.activity_main);

        stepsValueTextView = findViewById(R.id.stepsValue);
        proteinValueTextView = findViewById(R.id.proteinValue);
        carbsValueTextView = findViewById(R.id.carbValue);
        caloriesValueTextView = findViewById(R.id.caloriesValue);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (sensorManager != null) {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            stepsValueTextView.setTextSize(55);
        }

        if (stepSensor == null) {
            stepsValueTextView.setText(getString(R.string.no_steps_sensor));
            stepsValueTextView.setTextSize(29);
        }
        else {

            stepSensorListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {

                        stepsCount = (int) event.values[0];

                        stepsValueTextView.setText(String.format(Locale.getDefault(), "%d", stepsCount));
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }
            };

            sensorManager.registerListener(stepSensorListener, stepSensor, SensorManager.SENSOR_DELAY_UI);
        }

        ImageButton buttonCalendar = findViewById(R.id.calendar);
        buttonCalendar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
            startActivity(intent);
        });

        ImageButton buttonProfileSettings = findViewById(R.id.profileSettings);
        buttonProfileSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileSettingsActivity.class);
            startActivity(intent);
        });

        ImageButton buttonSettings = findViewById(R.id.settings);
        buttonSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        ListView listView = findViewById(R.id.listView_productList);
        productList = loadProductsList(this);

        adapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.textView_productName, productList);
        listView.setAdapter(adapter);

        Button buttonAdd = findViewById(R.id.button_addProduct);
        buttonAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddProductActivity.class);
            intent.putStringArrayListExtra("productList", productList);
            startActivityForResult(intent, ADD_PRODUCT_REQUEST);
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 1);
        }
    }

    private ArrayList<String> loadProductsList(Context context) {

        totalProteins = 0;
        totalCarbs = 0;
        totalCalories = 0;

        ArrayList<String> productList = new ArrayList<>();
        JsonManager jsonManager = new JsonManager();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        JsonArray calendarArray = jsonManager.readJSONArray(context, "calendar");
        if (calendarArray == null) {
            productList.add("No calendar file.");
            return productList;
        }

        JsonArray todayIds = null;
        for (JsonElement entry : calendarArray) {
            JsonObject day = entry.getAsJsonObject();
            if (day.has("date") && today.equals(day.get("date").getAsString())) {
                todayIds = day.getAsJsonArray("productsConsumed");
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


    private void UpdateCounters(){
        proteinValueTextView.setText(String.valueOf(totalProteins));
        carbsValueTextView.setText(String.valueOf(totalCarbs));
        caloriesValueTextView.setText(String.valueOf(totalCalories));
        Log.d("Counter", "protein: " + String.valueOf(totalProteins));
        Log.d("Counter", "carbs: " + String.valueOf(totalCarbs));
        Log.d("Counter", "calories: " + String.valueOf(totalCalories));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_PRODUCT_REQUEST && resultCode == RESULT_OK) {
            productList.clear();
            productList.addAll(loadProductsList(this));  // reload from JSON
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(stepSensorListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null) {
            sensorManager.registerListener(stepSensorListener, stepSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity", "Permission to recognize activity has been granted");
            } else {
                Log.d("MainActivity", "Permission to recognize activity has not been granted");
            }
        }
    }
}
