package com.example.proteinManager;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.gson.JsonObject;

import java.io.File;


public class ProfileSettingsActivity extends AppCompatActivity {
    private Button findShopButton;
    private ImageButton backButton;
    private ImageView avatarImage;
    private EditText nameEditText, emailEditText, passwordEditText;
    private Button saveButton, backupButton, logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        backButton = findViewById(R.id.buttonBack);
        avatarImage = findViewById(R.id.image_avatar);
        nameEditText = findViewById(R.id.editText_name);
        emailEditText = findViewById(R.id.editText_email);
        passwordEditText = findViewById(R.id.editText_password);
        backupButton = findViewById(R.id.button_backup);
        findShopButton = findViewById(R.id.button_findShop);
        saveButton = findViewById(R.id.button_save);
        logoutButton = findViewById(R.id.button_logout);

        LoadUserData();

        logoutButton.setOnClickListener(v -> {
            JsonManager manager = new JsonManager();
            manager.updateStringProperty(this, "settings", "currentAccount", "");

            Intent intent = new Intent(ProfileSettingsActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
        avatarImage.setOnClickListener(v -> {
            //to do
        });

        saveButton.setOnClickListener(v -> {
            Toast.makeText(this, "Changes would be saved!", Toast.LENGTH_SHORT).show();
        });

        findShopButton.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=Protein+Shop");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                Uri webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=Protein+Shop");
                Intent webIntent = new Intent(Intent.ACTION_VIEW, webUri);
                startActivity(webIntent);
            }
        });

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileSettingsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        backupButton.setOnClickListener(v -> {
            sendJsonViaBluetooth(this, "products");
        });
    }

    public void sendJsonViaBluetooth(Context context, String fileName) {
        File file = new File(context.getExternalFilesDir(null), fileName + ".json");

        if (!file.exists()) {
            Toast.makeText(context, "File not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri fileUri = FileProvider.getUriForFile(
                context,
                context.getPackageName() + ".fileprovider",
                file
        );

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_STREAM, fileUri);
        intent.setPackage("com.android.bluetooth"); // Targets Bluetooth app

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            context.startActivity(Intent.createChooser(intent, "Send JSON via Bluetooth"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No Bluetooth app found.", Toast.LENGTH_SHORT).show();
        }
    }

    private void LoadUserData(){
        JsonManager manager = new JsonManager();
        JsonObject settings = manager.readJSONObject(this, "settings");
        String userName = settings.get("currentAccount").getAsString();

        JsonObject users = manager.readJSONObject(this, "userData");

        JsonObject userData = users.get(userName).getAsJsonObject();
        String userEmail = userData.get("userEmail").getAsString();
        String userPassword = userData.get("userPassword").getAsString();

        nameEditText.setText(userName);
        emailEditText.setText(userEmail);
        passwordEditText.setText(userPassword);
    }

}

