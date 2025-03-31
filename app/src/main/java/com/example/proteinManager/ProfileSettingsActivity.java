package com.example.proteinManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileSettingsActivity extends AppCompatActivity {

    private ImageButton backButton;
    private ImageView avatarImage;
    private EditText nameEditText, passwordEditText;
    private Button backupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        backButton = findViewById(R.id.buttonBack);
        avatarImage = findViewById(R.id.image_avatar);
        nameEditText = findViewById(R.id.editText_name);
        passwordEditText = findViewById(R.id.editText_password);
        backupButton = findViewById(R.id.button_backup);

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileSettingsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        backupButton.setOnClickListener(v -> {
            // uwu
        });
    }
}
