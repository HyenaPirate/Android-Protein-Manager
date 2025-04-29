package com.example.proteinManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonUtils {

    public static JsonArray addStepsToDate(JsonArray calendar, String date, int steps) {
        boolean found = false;

        for (JsonElement el : calendar) {
            JsonObject day = el.getAsJsonObject();
            if (day.has("date") && day.get("date").getAsString().equals(date)) {
                day.addProperty("stepsCounted", steps);
                found = true;
                break;
            }
        }

        if (!found) {
            JsonObject newEntry = new JsonObject();
            newEntry.addProperty("date", date);
            newEntry.add("productsConsumed", new JsonArray());
            newEntry.addProperty("stepsCounted", steps);
            calendar.add(newEntry);
        }

        return calendar;
    }

    public static JsonArray removeObjectFromArrayByKeyValue(JsonArray array, String key, String value) {
        JsonArray result = new JsonArray();

        for (JsonElement element : array) {
            JsonObject obj = element.getAsJsonObject();
            if (!obj.has(key) || !obj.get(key).getAsString().equals(value)) {
                result.add(obj);
            }
        }

        return result;
    }
}
