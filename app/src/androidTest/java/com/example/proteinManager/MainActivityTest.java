package com.example.proteinManager;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Before
    public void launchActivity() {
        // Launch MainActivity before each test
        ActivityScenario.launch(MainActivity.class);
    }

    @Test
    public void testUIElementsAreVisible() {
        // Check if the steps, proteins, carbs, and calories TextViews are visible
        onView(withId(R.id.stepsValue)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.proteinValue)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.carbValue)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.caloriesValue)).check(ViewAssertions.matches(isDisplayed()));
    }

    @Test
    public void testInitialProteinIsZero() {
        // Test that the initial steps count is zero (or whatever default value you want)
        onView(withId(R.id.proteinValue)).check(ViewAssertions.matches(withText("0")));
    }

    @Test
    public void testCalendarButtonOpensCalendarActivity() {
        // Simulate clicking the calendar button and ensure CalendarActivity opens
        onView(withId(R.id.calendar)).perform(ViewActions.click());
        onView(withId(R.id.calendarLayout)).check(ViewAssertions.matches(isDisplayed()));  // Check if the calendar layout is displayed
    }

    @Test
    public void testProfileSettingsButtonOpensProfileSettingsActivity() {
        // Simulate clicking the profile settings button and ensure ProfileSettingsActivity opens
        onView(withId(R.id.profileSettings)).perform(ViewActions.click());
        onView(withId(R.id.profileSettingsLayout)).check(ViewAssertions.matches(isDisplayed()));  // Check if the ProfileSettings layout is displayed
    }

    @Test
    public void testSettingsButtonOpensSettingsActivity() {
        // Simulate clicking the settings button and ensure SettingsActivity opens
        onView(withId(R.id.settings)).perform(ViewActions.click());
        onView(withId(R.id.settingsLayout)).check(ViewAssertions.matches(isDisplayed()));  // Check if the Settings layout is displayed
    }

    @Test
    public void testAddProductButtonOpensAddProductActivity() {
        // Simulate clicking the Add Product button and ensure AddProductActivity opens
        onView(withId(R.id.button_addProduct)).perform(ViewActions.click());
        onView(withId(R.id.addProductLayout)).check(ViewAssertions.matches(isDisplayed()));  // Check if the AddProduct layout is displayed
    }

    @Test
    public void testDarkModeApplied() {
        // Test that dark mode is applied when enabled in settings
        onView(withId(R.id.settings)).perform(ViewActions.click());
        onView(withId(R.id.switch_dark_mode)).perform(ViewActions.click());  // Simulate toggling dark mode
        onView(withId(R.id.switch_dark_mode)).check(ViewAssertions.matches(ViewMatchers.isChecked()));  // Check if the switch is checked
    }
}
