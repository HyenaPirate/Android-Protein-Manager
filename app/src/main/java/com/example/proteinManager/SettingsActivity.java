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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.app.NotificationChannel;
import androidx.core.app.NotificationCompat;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    private Button buttonChangeLanguage;
    private ImageButton buttonBack;
    private androidx.appcompat.widget.SwitchCompat switchDarkMode;

    private androidx.appcompat.widget.SwitchCompat switchNotification;

    private Button notificationButton1;
    private Button notificationButton2;

    private TimePicker timePickerNotification;

    private EditText proteinTargetEditText;


    private int notificationHour = 17, notificationMinute = 30;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CheckFile();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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
        switchNotification = findViewById(R.id.switch_daily_notification);
        proteinTargetEditText = findViewById(R.id.editTarget_protein);
        timePickerNotification = findViewById(R.id.timePicker);

        LoadSettings();

        proteinTargetEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String newText = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String updatedText = editable.toString();
                if (updatedText.isEmpty()){
                    return;
                }
                JsonManager manager = new JsonManager();
                manager.updateIntProperty(proteinTargetEditText.getContext(), "settings", "targetProtein", Integer.parseInt(updatedText));
            }
        });

        buttonBack.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        buttonChangeLanguage.setOnClickListener(v -> showLanguageDialog());

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Apply the theme
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );

            // Update settings file
            JsonManager manager = new JsonManager();
            manager.updateStringProperty(this, "settings", "isDarkTheme", String.valueOf(isChecked));
        });

        switchNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            JsonManager manager = new JsonManager();
            manager.updateStringProperty(this, "settings", "doDailyNotification", String.valueOf(isChecked));
            ManageDailyNotifications();
        });

        timePickerNotification.setOnTimeChangedListener((view, hourOfDay, minute) -> {
            NotificationScheduler.cancelDailyNotification(this);
            ManageDailyNotifications();
            JsonManager manager = new JsonManager();
            manager.updateIntProperty(this, "settings", "dailyNotificationHour", hourOfDay);
            manager.updateIntProperty(this, "settings", "dailyNotificationMinute", minute);
            Log.i("Time", notificationHour+":"+notificationMinute);
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
        // Apply the language
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Update settings file
        JsonManager manager = new JsonManager();
        manager.updateStringProperty(this, "settings", "appLanguage", langCode);

        // Recreate activity to reflect language change
        recreate();
    }

    @SuppressLint("ObsoleteSdkInt")
    private void LoadSettings(){
        JsonManager manager = new JsonManager();
        JsonObject settings = manager.readJSONObject(this, "settings");

        switchDarkMode.setChecked(settings.get("isDarkTheme").getAsBoolean());
        switchNotification.setChecked(settings.get("doDailyNotification").getAsBoolean());

        if (Build.VERSION.SDK_INT >= 23) {
            timePickerNotification.setHour(settings.get("dailyNotificationHour").getAsInt());
            timePickerNotification.setMinute(settings.get("dailyNotificationMinute").getAsInt());
        } else {
            timePickerNotification.setCurrentHour(settings.get("dailyNotificationHour").getAsInt());
            timePickerNotification.setCurrentMinute(settings.get("dailyNotificationMinute").getAsInt());
        }


        proteinTargetEditText.setText(settings.get("targetProtein").getAsString());
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
        // Time for the alarm (e.g., 15 seconds from now for test)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 15);

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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    Toast.makeText(this, "This app needs permission to schedule exact alarms. Please enable it in settings.", Toast.LENGTH_LONG).show();

                    Intent settingsIntent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    startActivity(settingsIntent);
                    return;
                }
            }

            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            String formattedTime = sdf.format(calendar.getTime());
            Toast.makeText(this, "Notification set for " + formattedTime, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to set alarm", Toast.LENGTH_SHORT).show();
        }
    }



    private void ManageDailyNotifications(){

        if (!switchNotification.isChecked()){
            NotificationScheduler.cancelDailyNotification(this);
            return;
        }
        getNotificationTime();
        try {
            NotificationScheduler.scheduleDailyNotification(this, notificationHour, notificationMinute);
        } catch (SecurityException e) {
            Toast.makeText(this, "Enable exact alarms permission in system settings", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            startActivity(intent);
        }
    }

    private void getNotificationTime(){
        if (Build.VERSION.SDK_INT >= 23) {
            notificationHour = timePickerNotification.getHour();
            notificationMinute = timePickerNotification.getMinute();
        } else {
            notificationHour = timePickerNotification.getCurrentHour();
            notificationMinute = timePickerNotification.getCurrentMinute();
        }
    }

    private void CheckFile(){
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
            Log.i("Files", "Utworzono settings.json");
        }
    }



}
