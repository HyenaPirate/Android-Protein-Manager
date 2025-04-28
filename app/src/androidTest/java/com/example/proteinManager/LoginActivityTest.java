package com.example.proteinManager;

import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
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

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Before
    public void launchActivity() {
        // Launch LoginActivity before each test
        ActivityScenario.launch(LoginActivity.class);
    }

    @Test
    public void testUIElementsAreVisible() {
        // Check if the name, email, password EditTexts and login, register buttons are visible
        onView(withId(R.id.editText_name)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.editText_email)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.editText_password)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.button_login)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.button_register)).check(ViewAssertions.matches(isDisplayed()));
    }

    @Test
    public void testLoginButtonFunctionality() {
        ActivityScenario.launch(MainActivity.class);

        // Simulate entering valid user credentials and clicking login
        onView(withId(R.id.editText_name)).perform(ViewActions.replaceText("testUser"));
        onView(withId(R.id.editText_email)).perform(ViewActions.replaceText("test@test.com"));
        onView(withId(R.id.editText_password)).perform(ViewActions.replaceText("test"));
        onView(withId(R.id.button_login)).perform(ViewActions.click());

        // Check if MainActivity is launched (check if an element in MainActivity is displayed)
        onView(withId(R.id.main)).check(ViewAssertions.matches(isDisplayed()));  // Assuming this is an element from MainActivity
    }

    @Test
    public void testRegisterButtonOpensRegisterFlow() {
        // Simulate clicking the register button and ensure registration process starts

        onView(withId(R.id.editText_name)).perform(ViewActions.typeText("newValidUser123"));
        onView(withId(R.id.editText_email)).perform(ViewActions.typeText("newuser@example.com"));
        onView(withId(R.id.editText_password)).perform(ViewActions.typeText("newPassword123"));

        onView(withId(R.id.button_register)).perform(ViewActions.click());

        // Here, we can assume that if registration is successful, the user will be directed to MainActivity.
        // You can modify this test to verify other UI changes once you have them set up.
        onView(withId(R.id.main)).check(ViewAssertions.matches(isDisplayed()));
    }

    @Test
    public void testInvalidCredentials() {
        // Invalid Username (no such user exists)
        onView(withId(R.id.editText_name)).perform(ViewActions.clearText(), ViewActions.typeText("invalidUser"));
        onView(withId(R.id.editText_email)).perform(ViewActions.clearText(), ViewActions.typeText("test@test.com"));
        onView(withId(R.id.editText_password)).perform(ViewActions.clearText(), ViewActions.typeText("test"));
        onView(withId(R.id.button_login)).perform(ViewActions.click());

        // Check that user is still on the Login screen (invalid name)
        onView(withId(R.id.editText_name)).check(ViewAssertions.matches(isDisplayed()));  // Assuming this indicates the user is still on Login screen

        // Invalid Email
        onView(withId(R.id.editText_name)).perform(ViewActions.clearText(), ViewActions.typeText("testUser"));
        onView(withId(R.id.editText_email)).perform(ViewActions.clearText(), ViewActions.typeText("invalidEmail@wrong.com"));
        onView(withId(R.id.editText_password)).perform(ViewActions.clearText(), ViewActions.typeText("test"));
        onView(withId(R.id.button_login)).perform(ViewActions.click());

        // Check that user is still on the Login screen (invalid email)
        onView(withId(R.id.editText_email)).check(ViewAssertions.matches(isDisplayed()));  // Assuming this indicates the user is still on Login screen

        // Invalid Password
        onView(withId(R.id.editText_name)).perform(ViewActions.clearText(), ViewActions.typeText("testUser"));
        onView(withId(R.id.editText_email)).perform(ViewActions.clearText(), ViewActions.typeText("test@test.com"));
        onView(withId(R.id.editText_password)).perform(ViewActions.clearText(), ViewActions.typeText("wrongPassword"));
        onView(withId(R.id.button_login)).perform(ViewActions.click());

        // Check that user is still on the Login screen (invalid password)
        onView(withId(R.id.editText_password)).check(ViewAssertions.matches(isDisplayed()));  // Assuming this indicates the user is still on Login screen
    }


    @Test
    public void testUsernameAlreadyExistsOnRegister() {
        // Simulate trying to register with an existing username
        onView(withId(R.id.editText_name)).perform(ViewActions.typeText("testUser"));
        onView(withId(R.id.editText_email)).perform(ViewActions.typeText("test2@test.com"));
        onView(withId(R.id.editText_password)).perform(ViewActions.typeText("test"));
        onView(withId(R.id.button_register)).perform(ViewActions.click());

        // Here, we can just verify that the UI remains on the Login screen after trying to register an existing user.
        onView(withId(R.id.editText_name)).check(ViewAssertions.matches(isDisplayed()));
    }

    @Test
    public void testRegisterButtonFieldsNotEmpty() {
        // Check that the register button is enabled only if all fields are filled
        onView(withId(R.id.editText_name)).perform(ViewActions.typeText("newUser"));
        onView(withId(R.id.editText_email)).perform(ViewActions.typeText("newuser@example.com"));
        onView(withId(R.id.editText_password)).perform(ViewActions.typeText("password"));
        onView(withId(R.id.button_register)).check(ViewAssertions.matches(ViewMatchers.isEnabled()));
    }

}
