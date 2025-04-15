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
            return null;
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            JsonElement element = JsonParser.parseReader(reader);
            reader.close();

            if (element.isJsonObject()) {
                return element.getAsJsonObject();
            } else {
                Toast.makeText(context, "JSON is not an object.", Toast.LENGTH_SHORT).show();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to read JSON.", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public JsonArray readJSONArray(Context context, String fileName) {
        File file = getJsonFile(context, fileName);

        if (!file.exists()) {
            Toast.makeText(context, "File \"" + fileName + "\" does not exist.", Toast.LENGTH_SHORT).show();
            return null;
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            JsonElement element = JsonParser.parseReader(reader);
            reader.close();

            if (element.isJsonArray()) {
                return element.getAsJsonArray();
            } else {
                Toast.makeText(context, "JSON is not an array.", Toast.LENGTH_SHORT).show();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to read JSON.", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public void saveJSONObject(Context context, String fileName, JsonObject jsonObject) {
        File file = getJsonFile(context, fileName);

        try {
            FileWriter writer = new FileWriter(file);
            new Gson().toJson(jsonObject, writer);
            writer.close();
            Toast.makeText(context, "JSON object saved.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to save JSON object.", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveJSONArray(Context context, String fileName, JsonArray jsonArray) {
        File file = getJsonFile(context, fileName);

        try {
            FileWriter writer = new FileWriter(file);
            new Gson().toJson(jsonArray, writer);
            writer.close();
            Toast.makeText(context, "JSON array saved.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to save JSON array.", Toast.LENGTH_SHORT).show();
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

}
