package com.example.proteinManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;

public class AddProductActivity extends AppCompatActivity {

    private EditText productNameEditText;
    private ArrayList<String> productList;
    private ArrayAdapter<String> adapter;

    private Button scanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addproduct);

        productNameEditText = findViewById(R.id.editText_productName);
        ListView listView = findViewById(R.id.listView_productList);

        Intent intent = getIntent();
        productList = intent.getStringArrayListExtra("productList");

        if (productList == null) {
            productList = new ArrayList<>();
        }

        adapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.textView_productName, productList);
        listView.setAdapter(adapter);

        Button addButton = findViewById(R.id.button_addProduct);
        addButton.setOnClickListener(v -> {
            String productName = productNameEditText.getText().toString();

            if (!productName.isEmpty()) {
                productList.add(productName);
                adapter.notifyDataSetChanged();
            }
        });

        ImageButton backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> {
            returnToMainActivity();
        });

        scanButton = findViewById(R.id.buttonScanCode);
        scanButton.setOnClickListener(v->{
            scanCode();
        });
    }

    private void returnToMainActivity() {
        Intent resultIntent = new Intent();
        resultIntent.putStringArrayListExtra("updatedProductList", productList);
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


}
