package com.example.proteinManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class LoginActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, passwordEditText;
    private Button loginButton, registerButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        CheckSettingsHealth();

        nameEditText = findViewById(R.id.editText_name);
        emailEditText = findViewById(R.id.editText_email);
        passwordEditText = findViewById(R.id.editText_password);
        loginButton = findViewById(R.id.button_login);
        registerButton = findViewById(R.id.button_register);

        loginButton.setOnClickListener(v -> loginUser());
        registerButton.setOnClickListener(v -> registerUser());
    }

    private void loginUser() {
        if (CheckIfLoggedIn()) {
            Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
        }
    }

    private void registerUser() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.fields_required), Toast.LENGTH_SHORT).show();
            return;
        }

        AddNewUserToDatabase(name, email, password);

        Toast.makeText(this, getString(R.string.register_success), Toast.LENGTH_SHORT).show();

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private boolean CheckIfLoggedIn(){
        JsonManager manager = new JsonManager();
        JsonObject settings =  manager.readJSONObject(this, "settings");
        if (!settings.has("currentAccount") || settings.get("currentAccount").isJsonNull()){
            return false;
        }

        String loggedInUser = settings.get("currentAccount").getAsString();
        JsonObject userData = manager.readJSONObject(this, "userData");
        return userData.has(loggedInUser);
    }

    private void AddNewUserToDatabase(String userName, String email, String password){
        JsonManager manager = new JsonManager();

        JsonObject newUserData = new JsonObject();
        newUserData.addProperty("userEmail", email);
        newUserData.addProperty("userPassword", password);

        JsonObject newUser = new JsonObject();
        newUser.add(userName, newUserData);

        manager.appendToJSONObject(this, "userData", newUser);
        manager.updateStringProperty(this, "settings", "currentAccount", userName);
    }

    private void CheckSettingsHealth(){
        JsonManager manager = new JsonManager();
        if(!manager.isJsonFileValid(this, "settings")){
            JsonObject settings = new JsonObject();

            settings.addProperty("isDarkTheme", false);
            settings.addProperty("doDailyNotification", false);
            settings.addProperty("appLanguage", "en");
            settings.addProperty("dailyNotificationHour", 17);
            settings.addProperty("dailyNotificationMinute", 30);
            settings.addProperty("targetProtein", 0);
            settings.addProperty("currentAccount", "");

            manager.saveJSONObject(this, "settings", settings);
        }
    }
}
