// CalendarUtilsTest.java
package com.example.proteinManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.Test;
import static org.junit.Assert.*;

public class CalendarUtilsTest {

    @Test
    public void testAddProductToEmptyCalendar() {
        JsonArray calendar = new JsonArray();
        String date = "2025-04-29";
        int productId = 42;

        JsonArray updated = CalendarUtils.addProductToDate(calendar, date, productId);

        assertEquals(1, updated.size());
        JsonObject entry = updated.get(0).getAsJsonObject();
        assertEquals(date, entry.get("date").getAsString());
        assertEquals(1, entry.get("productsConsumed").getAsJsonArray().size());
        assertEquals(productId, entry.get("productsConsumed").getAsJsonArray().get(0).getAsInt());
    }

    @Test
    public void testAddProductToExistingDate() {
        JsonArray calendar = new JsonArray();
        JsonObject day = new JsonObject();
        day.addProperty("date", "2025-04-29");
        JsonArray consumed = new JsonArray();
        consumed.add(10);
        day.add("productsConsumed", consumed);
        calendar.add(day);

        JsonArray updated = CalendarUtils.addProductToDate(calendar, "2025-04-29", 99);

        JsonObject updatedDay = updated.get(0).getAsJsonObject();
        JsonArray consumedProducts = updatedDay.getAsJsonArray("productsConsumed");

        assertEquals(2, consumedProducts.size());
        assertEquals(10, consumedProducts.get(0).getAsInt());
        assertEquals(99, consumedProducts.get(1).getAsInt());
    }
}
