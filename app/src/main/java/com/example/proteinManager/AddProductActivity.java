package com.example.proteinManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AddProductActivity extends AppCompatActivity {

    private EditText productNameEditText;
    private EditText productProteinEditText;
    private EditText productCarbsEditText;
    private EditText productCodeEditText;
    private ArrayList<String> productList;
    private ArrayAdapter<String> adapter;


    private ImageButton scanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addproduct);

        productNameEditText = findViewById(R.id.editText_productName);
        productProteinEditText = findViewById(R.id.editText_protein);
        productCarbsEditText = findViewById(R.id.editText_carbs);
        productCodeEditText = findViewById(R.id.editText_productCode);
        ListView listView = findViewById(R.id.listView_productList);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedProduct = (String) parent.getItemAtPosition(position);
                JsonManager jsonManager = new JsonManager();

                // Load products to get the ID
                JsonArray productArray = jsonManager.readJSONArray(getApplicationContext(), "products");
                int foundId = -1;

                if (productArray != null) {
                    for (JsonElement el : productArray) {
                        JsonObject obj = el.getAsJsonObject();
                        if (obj.has("productName") && selectedProduct.equals(obj.get("productName").getAsString())) {
                            foundId = obj.get("productId").getAsInt();
                            break;
                        }
                    }
                }

                if (foundId != -1) {
                    String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    jsonManager.addIdToCalendar(getApplicationContext(), "calendar", today, foundId);

                    Toast.makeText(getApplicationContext(), "Added \"" + selectedProduct + "\" to calendar", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Product ID not found!", Toast.LENGTH_SHORT).show();
                }
            }
        });


// 🔄 Load products from JSON
        productList = loadProductsList(this);

        adapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.textView_productName, productList);
        listView.setAdapter(adapter);

        Button addButton = findViewById(R.id.button_addProduct);
        addButton.setOnClickListener(v -> {
            String productName = productNameEditText.getText().toString();

            if (!productName.isEmpty()) {
                addProductByName(productName, this);
                productList.add(productName);
                adapter.notifyDataSetChanged();
            }
        });

        ImageButton backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> {
            returnToMainActivity();
        });

        scanButton = findViewById(R.id.button_ScanCode);
        scanButton.setOnClickListener(v->{
            scanCode();
        });
    }

    public void addProductByName(String name, Context context) {
        JsonManager jsonManager = new JsonManager();
        JsonArray productArray = jsonManager.readJSONArray(context, "products");

        if (productArray == null) {
            productArray = new JsonArray(); // create new array if file doesn't exist
        }

        int newId = productArray.size(); // use current size as new ID

        JsonObject newProduct = new JsonObject();
        newProduct.addProperty("productId", newId);
        newProduct.addProperty("productName", name);
        newProduct.add("productCode", null); // null value
        newProduct.addProperty("productUnit", "sztuka");
        newProduct.addProperty("productBaseCount", 1);

        // Nutrients object
        JsonObject nutrients = new JsonObject();
        nutrients.addProperty("productProtein", Integer.parseInt(productProteinEditText.getText().toString()));
        nutrients.addProperty("productCarbohydrates", Integer.parseInt(productCarbsEditText.getText().toString()));
        nutrients.addProperty("productCode", Integer.parseInt(productCodeEditText.getText().toString()));

        newProduct.add("productNutrients", nutrients);

        // Add to array and save
        productArray.add(newProduct);
        jsonManager.saveJSONArray(context, "products", productArray);

        Toast.makeText(context, "Product \"" + name + "\" added.", Toast.LENGTH_SHORT).show();
    }


    private void returnToMainActivity() {
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    public void scanCode(){
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash On");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result ->{
        if (result.getContents() != null){
            AlertDialog.Builder builder = new AlertDialog.Builder(AddProductActivity.this);
            builder.setTitle("Result");
            builder.setMessage(result.getContents());
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }
    });

    private ArrayList<String> loadProductsList(Context context) {
        ArrayList<String> productList = new ArrayList<>();

        JsonManager jsonManager = new JsonManager();
        JsonArray jsonArray = jsonManager.readJSONArray(context, "products");

        if (jsonArray != null) {
            for (JsonElement element : jsonArray) {
                JsonObject product = element.getAsJsonObject();
                if (product.has("productName")) {
                    String name = product.get("productName").getAsString();
                    productList.add(name);
                }
            }
        } else {
            productList.add("default product");
        }

        return productList;
    }

}
