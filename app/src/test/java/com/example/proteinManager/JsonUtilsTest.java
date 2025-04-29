package com.example.proteinManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.junit.Test;

import static org.junit.Assert.*;

public class JsonUtilsTest {

    @Test
    public void testAddStepsToEmptyCalendar() {
        JsonArray calendar = new JsonArray();
        String date = "2025-04-29";
        int steps = 1234;

        JsonArray result = JsonUtils.addStepsToDate(calendar, date, steps);
        assertEquals(1, result.size());

        JsonObject entry = result.get(0).getAsJsonObject();
        assertEquals(date, entry.get("date").getAsString());
        assertEquals(steps, entry.get("stepsCounted").getAsInt());
    }

    @Test
    public void testAddStepsToExistingDate() {
        JsonArray calendar = new JsonArray();
        JsonObject day = new JsonObject();
        day.addProperty("date", "2025-04-29");
        day.add("productsConsumed", new JsonArray());
        day.addProperty("stepsCounted", 1000);
        calendar.add(day);

        JsonArray result = JsonUtils.addStepsToDate(calendar, "2025-04-29", 2222);
        JsonObject entry = result.get(0).getAsJsonObject();

        assertEquals(1, result.size());
        assertEquals(2222, entry.get("stepsCounted").getAsInt());
    }

    @Test
    public void testRemoveObjectFromArrayByKeyValue_found() {
        JsonArray input = new JsonArray();

        JsonObject obj1 = new JsonObject();
        obj1.addProperty("name", "apple");
        JsonObject obj2 = new JsonObject();
        obj2.addProperty("name", "banana");

        input.add(obj1);
        input.add(obj2);

        JsonArray result = JsonUtils.removeObjectFromArrayByKeyValue(input, "name", "apple");

        assertEquals(1, result.size());
        assertEquals("banana", result.get(0).getAsJsonObject().get("name").getAsString());
    }

    @Test
    public void testRemoveObjectFromArrayByKeyValue_notFound() {
        JsonArray input = new JsonArray();

        JsonObject obj1 = new JsonObject();
        obj1.addProperty("name", "apple");
        input.add(obj1);

        JsonArray result = JsonUtils.removeObjectFromArrayByKeyValue(input, "name", "orange");

        assertEquals(1, result.size());
        assertEquals("apple", result.get(0).getAsJsonObject().get("name").getAsString());
    }
}
