package com.example.proteinManager;

import android.content.Intent;
import android.os.Build;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.google.gson.JsonObject;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static java.util.function.Predicate.not;

@RunWith(AndroidJUnit4.class)
public class SettingsActivityTest {



    @Rule
    public ActivityScenarioRule<SettingsActivity> activityRule = new ActivityScenarioRule<>(SettingsActivity.class);

    @Test
    public void testDarkModeToggle() {
        // Check initial state of dark mode switch (assume it's off by default)
        Espresso.onView(withId(R.id.switch_dark_mode))
                .check(matches(isNotChecked()));

        // Toggle the switch to turn on dark mode
        Espresso.onView(withId(R.id.switch_dark_mode))
                .perform(ViewActions.click());

        // Verify that dark mode is now enabled
        Espresso.onView(withId(R.id.switch_dark_mode))
                .check(matches(isChecked()));
    }

    @Test
    public void testNotificationToggle() {
        // Initially, check if notifications are disabled
        Espresso.onView(withId(R.id.switch_daily_notification))
                .check(matches(isNotChecked()));

        // Toggle the switch to enable notifications
        Espresso.onView(withId(R.id.switch_daily_notification))
                .perform(ViewActions.click());

        // Verify that notifications are now enabled
        Espresso.onView(withId(R.id.switch_daily_notification))
                .check(matches(isChecked()));
    }

    @Test
    public void testProteinTargetEditText() {
        // Verify the EditText is initially empty or has a default value
        Espresso.onView(withId(R.id.editTarget_protein))
                .check(matches(withText("")));

        // Set a new protein target
        Espresso.onView(withId(R.id.editTarget_protein))
                .perform(ViewActions.replaceText("150"));

        // Verify that the EditText now contains the updated protein target
        Espresso.onView(withId(R.id.editTarget_protein))
                .check(matches(withText("150")));
    }

    @Test
    public void testLanguageChange() {
        // Click the change language button
        Espresso.onView(withId(R.id.buttonChangeLanguage))
                .perform(ViewActions.click());

        // Select "Polski" language (index 1 in the dialog)
        Espresso.onView(withText("Polski"))
                .perform(ViewActions.click());

        // Verify the language change has been applied
        // Check if the button's text changes to the translated version in Polish
        Espresso.onView(withId(R.id.buttonBack))
                .check(matches(withText("Wróć")));  // This assumes that "Back" is translated to "Wróć" in Polish
    }


}
