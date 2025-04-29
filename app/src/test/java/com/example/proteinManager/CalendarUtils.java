// CalendarUtils.java (w tym samym pakiecie)
package com.example.proteinManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CalendarUtils {

    public static JsonArray addProductToDate(JsonArray calendarArray, String date, int productId) {
        boolean dateFound = false;

        for (JsonElement el : calendarArray) {
            JsonObject dayEntry = el.getAsJsonObject();
            if (dayEntry.has("date") && date.equals(dayEntry.get("date").getAsString())) {
                JsonArray consumed = dayEntry.getAsJsonArray("productsConsumed");
                consumed.add(productId);
                dateFound = true;
                break;
            }
        }

        if (!dateFound) {
            JsonObject newEntry = new JsonObject();
            newEntry.addProperty("date", date);
            JsonArray consumed = new JsonArray();
            consumed.add(productId);
            newEntry.add("productsConsumed", consumed);
            calendarArray.add(newEntry);
        }

        return calendarArray;
    }
}
