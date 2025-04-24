package com.example.proteinManager;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class JsonManager {

    private File getJsonFile(Context context, String fileName) {
        return new File(context.getExternalFilesDir(null), fileName + ".json");
    }

    public JsonObject readJSONObject(Context context, String fileName) {
        File file = getJsonFile(context, fileName);

        if (!file.exists()) {
            Toast.makeText(context, "File \"" + fileName + "\" does not exist.", Toast.LENGTH_SHORT).show();
            return new JsonObject();
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            JsonElement element = JsonParser.parseReader(reader);
            reader.close();

            if (element.isJsonObject()) {
                return element.getAsJsonObject();
            } else {
                //Toast.makeText(context, "JSON is not an object.", Toast.LENGTH_SHORT).show();
                return new JsonObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to read JSON.", Toast.LENGTH_SHORT).show();
            return new JsonObject();
        }
    }

    public JsonArray readJSONArray(Context context, String fileName) {
        File file = getJsonFile(context, fileName);

        if (!file.exists()) {
            Toast.makeText(context, "File \"" + fileName + "\" does not exist.", Toast.LENGTH_SHORT).show();
            return new JsonArray();
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            JsonElement element = JsonParser.parseReader(reader);
            reader.close();

            if (element.isJsonArray()) {
                return element.getAsJsonArray();
            } else {
                //Toast.makeText(context, "JSON is not an array.", Toast.LENGTH_SHORT).show();
                return new JsonArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(context, "Failed to read JSON.", Toast.LENGTH_SHORT).show();
            return new JsonArray();
        }
    }

    public void saveJSONObject(Context context, String fileName, JsonObject jsonObject) {
        File file = getJsonFile(context, fileName);

        try {
            FileWriter writer = new FileWriter(file);
            new Gson().toJson(jsonObject, writer);
            writer.close();
            //Toast.makeText(context, "JSON object saved.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(context, "Failed to save JSON object.", Toast.LENGTH_SHORT).show();
        }
    }

    public void createEmptyJsonFile(Context context, String fileName) {
        File file = getJsonFile(context, fileName);

        try {
            // Just create and close the file — empty content
            FileWriter writer = new FileWriter(file);
            writer.close();
            //Toast.makeText(context, "Empty JSON file created.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(context, "Failed to create empty JSON file.", Toast.LENGTH_SHORT).show();
        }
    }


    public void appendToJSONObject(Context context, String fileName, JsonObject newEntry) {
        File file = getJsonFile(context, fileName);
        Gson gson = new Gson();
        JsonObject existingData = new JsonObject();

        try {
            // If file exists and is not empty, load existing data
            if (file.exists() && file.length() > 0) {
                FileReader reader = new FileReader(file);
                existingData = gson.fromJson(reader, JsonObject.class);
                reader.close();
            }

            // Merge: Add each property from newEntry to existingData
            for (String key : newEntry.keySet()) {
                existingData.add(key, newEntry.get(key));
            }

            // Save the updated JSON object back to the file
            FileWriter writer = new FileWriter(file);
            gson.toJson(existingData, writer);
            writer.close();

            //Toast.makeText(context, "JSON updated with new entry.", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(context, "Failed to append to JSON.", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateStringProperty(Context context, String fileName, String propertyName, String newValue) {
        File file = getJsonFile(context, fileName);
        Gson gson = new Gson();

        try {
            if (!file.exists() || file.length() == 0) {
                Toast.makeText(context, "JSON file not found or empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Load current JSON data
            FileReader reader = new FileReader(file);
            JsonObject rootObject = gson.fromJson(reader, JsonObject.class);
            reader.close();

            // Check if the property exists (optional)
            if (rootObject.has(propertyName)) {
                rootObject.addProperty(propertyName, newValue);

                // Save updated JSON back to file
                FileWriter writer = new FileWriter(file);
                gson.toJson(rootObject, writer);
                writer.close();

                //Toast.makeText(context, "Updated '" + propertyName + "' to '" + newValue + "'", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(context, "Property '" + propertyName + "' not found in JSON.", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(context, "Failed to update JSON property.", Toast.LENGTH_SHORT).show();
        }
    }


    public void updateIntProperty(Context context, String fileName, String propertyName, int newValue) {
        File file = getJsonFile(context, fileName);
        Gson gson = new Gson();

        try {
            if (!file.exists() || file.length() == 0) {
                Toast.makeText(context, "JSON file not found or empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Load current JSON data
            FileReader reader = new FileReader(file);
            JsonObject rootObject = gson.fromJson(reader, JsonObject.class);
            reader.close();

            // Check if the property exists (optional)
            if (rootObject.has(propertyName)) {
                rootObject.addProperty(propertyName, newValue);

                // Save updated JSON back to file
                FileWriter writer = new FileWriter(file);
                gson.toJson(rootObject, writer);
                writer.close();

                // Optionally, you can display a toast message for success
                // Toast.makeText(context, "Updated '" + propertyName + "' to '" + newValue + "'", Toast.LENGTH_SHORT).show();
            } else {
                // Optionally, you can display a toast message for not found property
                // Toast.makeText(context, "Property '" + propertyName + "' not found in JSON.", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Optionally, display a toast for error
            // Toast.makeText(context, "Failed to update JSON property.", Toast.LENGTH_SHORT).show();
        }
    }


    public void saveJSONArray(Context context, String fileName, JsonArray jsonArray) {
        File file = getJsonFile(context, fileName);

        try {
            FileWriter writer = new FileWriter(file);
            new Gson().toJson(jsonArray, writer);
            writer.close();
            //Toast.makeText(context, "JSON array saved.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(context, "Failed to save JSON array.", Toast.LENGTH_SHORT).show();
        }
    }

    public void removeFromJSONArray(Context context, String fileName, String key, String value) {
        JsonArray jsonArray = readJSONArray(context, fileName);

        if (jsonArray == null) {
            Toast.makeText(context, "Cannot remove item: file not found or empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonArray updatedArray = new JsonArray();
        boolean removed = false;

        for (JsonElement element : jsonArray) {
            JsonObject obj = element.getAsJsonObject();
            if (obj.has(key) && obj.get(key).getAsString().equals(value)) {
                removed = true; // skip adding this item to the new array
            } else {
                updatedArray.add(obj);
            }
        }

        if (removed) {
            saveJSONArray(context, fileName, updatedArray);
            Toast.makeText(context, "Item removed.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "No matching item found.", Toast.LENGTH_SHORT).show();
        }
    }

    public void addIdToCalendar(Context context, String fileName, String date, int productId) {
        JsonArray calendarArray = readJSONArray(context, fileName);
        if (calendarArray == null) {
            calendarArray = new JsonArray();
        }

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

        saveJSONArray(context, fileName, calendarArray);
    }

    public void saveSteps(Context context, String date, int steps) {
        JsonArray calendarArray = readJSONArray(context, "calendar");
        if (calendarArray == null) {
            calendarArray = new JsonArray();
        }

        boolean dateFound = false;
        for (JsonElement el : calendarArray) {
            JsonObject dayEntry = el.getAsJsonObject();
            if (dayEntry.has("date") && date.equals(dayEntry.get("date").getAsString())) {
                dayEntry.addProperty("stepsCounted", steps);
                dateFound = true;
                break;
            }
        }

        if (!dateFound) {
            JsonObject newEntry = new JsonObject();
            newEntry.addProperty("date", date);
            newEntry.add("productsConsumed", new JsonArray());
            newEntry.addProperty("stepsCounted", steps);
            calendarArray.add(newEntry);
        }

        saveJSONArray(context, "calendar", calendarArray);
    }

    public boolean isJsonFileValid(Context context, String fileName) {
        File file = getJsonFile(context, fileName);
        return file.exists() && file.length() > 0;
    }



}
