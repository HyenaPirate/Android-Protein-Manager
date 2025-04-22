package com.example.proteinManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.app.NotificationChannel;
import androidx.core.app.NotificationCompat;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Button buttonChangeLanguage;
    private ImageButton buttonBack;
    private androidx.appcompat.widget.SwitchCompat switchDarkMode;

    private Button notificationButton1;
    private Button notificationButton2;

    private int notificationHour = 17, notificationMinute = 30;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        notificationButton1 = findViewById(R.id.buttonNotification1);
        notificationButton1.setOnClickListener(v -> {
            sendImmediateNotification();
        });

        notificationButton2 = findViewById(R.id.buttonNotification2);
        notificationButton2.setOnClickListener(v -> {
            scheduleNotification();
        });

        buttonBack = findViewById(R.id.buttonBack);
        buttonChangeLanguage = findViewById(R.id.buttonChangeLanguage);
        switchDarkMode = findViewById(R.id.switch_dark_mode);

        boolean isDarkMode = sharedPreferences.getBoolean("DarkMode", false);
        switchDarkMode.setChecked(isDarkMode);

        setAppTheme(isDarkMode);

        buttonBack.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        buttonChangeLanguage.setOnClickListener(v -> showLanguageDialog());

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setAppTheme(isChecked);
            editor.putBoolean("DarkMode", isChecked);
            editor.apply();
        });

    }

    private void showLanguageDialog() {
        String[] languages = {"English", "Polski"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Language:")
                .setItems(languages, (dialog, which) -> {
                    String langCode = (which == 0) ? "en" : "pl";
                    changeLanguage(langCode);
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void changeLanguage(String langCode) {
        editor.putString("Language", langCode);
        editor.apply();

        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        recreate();
    }

    private void setAppTheme(boolean isDarkMode) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void sendImmediateNotification() {
        String channelId = "protein_channel";
        String channelName = "Protein Notifications";

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null) {
            Log.e("Notification", "NotificationManager is null");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for immediate notifications");
            notificationManager.createNotificationChannel(channel);
        }

        Log.d("Notification", "Notification channel created or already exists.");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.person_24)
                .setContentTitle("Protein Manager")
                .setContentText("Don't forget to update your protein intake!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
        Log.d("Notification", "Notification should be showing now.");

        Toast.makeText(this, "Push notification sent!", Toast.LENGTH_SHORT).show();
    }


    @SuppressLint("ScheduleExactAlarm")
    private void scheduleNotification() {
        // Time for the alarm (19:00)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 15);

        // If it's already past 19:00 today, schedule for tomorrow
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );

            // ✅ Toast confirmation
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            String formattedTime = sdf.format(calendar.getTime());
            Toast.makeText(this, "Codzienne przypomnienie ustawione na " + formattedTime + " 💪", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Nie udało się ustawić przypomnienia 😔", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private void scheduleDailyNotification() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, notificationHour);
        calendar.set(Calendar.MINUTE, notificationMinute);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            String formattedTime = sdf.format(calendar.getTime());
            Toast.makeText(this, "Codzienne przypomnienie ustawione na " + formattedTime + " 💪", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Nie udało się ustawić przypomnienia 😔", Toast.LENGTH_SHORT).show();
        }
    }

}
