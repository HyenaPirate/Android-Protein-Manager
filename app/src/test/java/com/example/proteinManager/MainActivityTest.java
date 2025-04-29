package com.example.proteinManager;

import android.content.Context;

import com.google.gson.JsonObject;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MainActivityTest {

    private MainActivity mainActivity;
    private JsonManager mockJsonManager;
    private Context mockContext;

    @Before
    public void setUp() {
        mainActivity = new MainActivity();
        mockJsonManager = mock(JsonManager.class);
        mockContext = mock(Context.class);
    }

    @Test
    public void testCheckIfLoggedIn_returnsTrueWhenValidUser() {
        // Given
        JsonObject settings = new JsonObject();
        settings.addProperty("currentAccount", "user1");

        JsonObject userData = new JsonObject();
        userData.add("user1", new JsonObject());

        when(mockJsonManager.readJSONObject(mockContext, "settings")).thenReturn(settings);
        when(mockJsonManager.readJSONObject(mockContext, "userData")).thenReturn(userData);

        // Replace JsonManager in mainActivity (would need to make this injectable/refactor)
        // For now we assume you extracted this logic to a testable class

        boolean result = new UserAuthHelper(mockJsonManager).checkIfLoggedIn(mockContext);

        assertTrue(result);
    }

    @Test
    public void testCheckIfLoggedIn_returnsFalseWhenNoUser() {
        JsonObject settings = new JsonObject();
        settings.addProperty("currentAccount", "");

        JsonObject userData = new JsonObject();

        when(mockJsonManager.readJSONObject(mockContext, "settings")).thenReturn(settings);
        when(mockJsonManager.readJSONObject(mockContext, "userData")).thenReturn(userData);

        boolean result = new UserAuthHelper(mockJsonManager).checkIfLoggedIn(mockContext);

        assertFalse(result);
    }
}

