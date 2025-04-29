package com.example.proteinManager;

import android.content.Context;

import com.google.gson.JsonObject;

public class UserAuthHelper {

    private JsonManager jsonManager;

    public UserAuthHelper(JsonManager jsonManager) {
        this.jsonManager = jsonManager;
    }

    public boolean checkIfLoggedIn(Context context) {
        JsonObject settings = jsonManager.readJSONObject(context, "settings");
        if (!settings.has("currentAccount") || settings.get("currentAccount").isJsonNull() ||
                settings.get("currentAccount").getAsString().isEmpty()) {
            return false;
        }

        String loggedInUser = settings.get("currentAccount").getAsString();
        JsonObject userData = jsonManager.readJSONObject(context, "userData");
        return userData.has(loggedInUser);
    }
}
