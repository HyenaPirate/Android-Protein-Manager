package com.example.proteinManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int ADD_PRODUCT_REQUEST = 1;
    private ArrayList<String> productList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        productList = new ArrayList<>();
        productList.add("Twoja stara");
        productList.add("Ziemniak");
        productList.add("Cebula");
        productList.add("Twoj stary");

        adapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.textView_productName, productList);
        listView.setAdapter(adapter);

        Button buttonAdd = findViewById(R.id.button_addProduct);
        buttonAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddProductActivity.class);
            intent.putStringArrayListExtra("productList", productList);
            startActivityForResult(intent, ADD_PRODUCT_REQUEST);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_PRODUCT_REQUEST && resultCode == RESULT_OK && data != null) {
            ArrayList<String> updatedList = data.getStringArrayListExtra("updatedProductList");
            if (updatedList != null) {
                productList.clear();
                productList.addAll(updatedList);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
