package com.example.proteinManager;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;


public class NotificationReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "protein_reminder_channel";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Reminder", "NotificationReceiver triggered ✅");

        // Create and show the notification
        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.person_24)
                .setContentTitle("Przypomnienie")
                .setContentText("Nie zapomnij uzupełnić swojego spożycia białka 💪")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(contentIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Protein Reminder";
            String description = "Channel for daily protein reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
        }

        // Retrieve last used hour/minute
        SharedPreferences prefs = context.getSharedPreferences("reminder_prefs", Context.MODE_PRIVATE);
        int hour = prefs.getInt("reminder_hour", 19);
        int minute = prefs.getInt("reminder_minute", 0);

        // Schedule for the next day at the same time
        NotificationScheduler.scheduleDailyNotification(context, hour, minute);
    }



}


class NotificationScheduler {

    private static final int NOTIFICATION_REQUEST_CODE = 100;

    @SuppressLint("ScheduleExactAlarm")
    public static void scheduleDailyNotification(Context context, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();

        // Set the desired time
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // If that time has already passed today, schedule for tomorrow
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                NOTIFICATION_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    Intent settingsIntent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(settingsIntent);
                    return;
                }
            }

        }
        SharedPreferences prefs = context.getSharedPreferences("reminder_prefs", Context.MODE_PRIVATE);
        prefs.edit().putInt("reminder_hour", hour).putInt("reminder_minute", minute).apply();
        Toast.makeText(context, "Notifications set for "+ hour+":"+minute, Toast.LENGTH_SHORT).show();
    }

    public static void cancelDailyNotification(Context context) {
        try {
            Intent intent = new Intent(context, NotificationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    NOTIFICATION_REQUEST_CODE,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            if (alarmManager != null && pendingIntent != null) {
                alarmManager.cancel(pendingIntent);
                //Toast.makeText(context, "Notifications canceled.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Unable to cancel notification: AlarmManager or PendingIntent is null", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to cancel notification: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}
