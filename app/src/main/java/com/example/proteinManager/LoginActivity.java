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

        nameEditText = findViewById(R.id.editText_name);
        emailEditText = findViewById(R.id.editText_email);
        passwordEditText = findViewById(R.id.editText_password);
        loginButton = findViewById(R.id.button_login);
        registerButton = findViewById(R.id.button_register);

        loginButton.setOnClickListener(v -> loginUser());
        registerButton.setOnClickListener(v -> registerUser());
    }

    private void loginUser() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.fields_required), Toast.LENGTH_SHORT).show();
            return;
        }

        JsonManager manager = new JsonManager();
        JsonObject userData = manager.readJSONObject(this, "userData");

        if (!userData.has(name)){
            Toast.makeText(this, "No user of this name registered.", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObject account = userData.get(name).getAsJsonObject();
        String correctEmail = account.get("userEmail").getAsString();
        String correctPassword = account.get("userPassword").getAsString();

        if (!email.equals(correctEmail) || !password.equals(correctPassword)){
            Toast.makeText(this, "Incorrect email or password.", Toast.LENGTH_SHORT).show();
            return;
        }

        manager.updateStringProperty(this, "settings", "currentAccount", name);
        Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void registerUser() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.fields_required), Toast.LENGTH_SHORT).show();
            return;
        }

        JsonManager manager = new JsonManager();
        JsonObject userData = manager.readJSONObject(this, "userData");

        if(userData.has(name)){
            Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
            return;
        }
        AddNewUserToDatabase(name, email, password);
        Toast.makeText(this, getString(R.string.register_success), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
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


}
