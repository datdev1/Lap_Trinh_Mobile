package com.b21dccn216.pocketcocktail.utils;

import android.content.Context;
import android.util.Log;

import com.b21dccn216.pocketcocktail.dao.CategoryDAO;
import com.b21dccn216.pocketcocktail.dao.DrinkDAO;
import com.b21dccn216.pocketcocktail.model.Category;
import com.b21dccn216.pocketcocktail.model.Drink;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class DataImporter {
    private static final String TAG = "DataImporter";
    private final Context context;
    private final CategoryDAO categoryDAO;
    private final DrinkDAO drinkDAO;
    private final Map<Integer, String> spiritIdToUuidMap;

    public DataImporter(Context context) {
        this.context = context;
        this.categoryDAO = new CategoryDAO();
        this.drinkDAO = new DrinkDAO();
        this.spiritIdToUuidMap = new HashMap<>();
    }

    public void importData(String jsonFileName) {
        try {
            String jsonString = loadJSONFromAsset(jsonFileName);
            JSONObject jsonObject = new JSONObject(jsonString);
            
            // Import Categories (Spirits) first
            JSONArray spiritsArray = jsonObject.getJSONArray("Spirits");
            importCategories(spiritsArray, 0, () -> {
                try {
                    // After categories are imported, import drinks
                    JSONArray drinksArray = jsonObject.getJSONArray("Drinks");
                    importDrinks(drinksArray, 0);
                } catch (JSONException e) {
                    Log.e(TAG, "Error getting drinks array: " + e.getMessage());
                }
            });
        } catch (JSONException | IOException e) {
            Log.e(TAG, "Error importing data: " + e.getMessage());
        }
    }

    private void importCategories(JSONArray categories, int index, Runnable onComplete) {
        if (index >= categories.length()) {
            onComplete.run();
            return;
        }

        try {
            JSONObject categoryJson = categories.getJSONObject(index);
            Category category = new Category();
            category.setName(categoryJson.getString("name"));
            category.setDescription(categoryJson.getString("description"));
            category.setImage(categoryJson.getString("img"));
            
            // Store the mapping of original ID to new UUID
            int originalId = categoryJson.getInt("id");
            String newUuid = category.generateUUID();
            spiritIdToUuidMap.put(originalId, newUuid);

            categoryDAO.addCategory(category, 
                aVoid -> {
                    Log.d(TAG, "Category imported: " + category.getName());
                    importCategories(categories, index + 1, onComplete);
                },
                e -> {
                    Log.e(TAG, "Error importing category: " + e.getMessage());
                    importCategories(categories, index + 1, onComplete);
                }
            );
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing category: " + e.getMessage());
            importCategories(categories, index + 1, onComplete);
        }
    }

    private void importDrinks(JSONArray drinks, int index) {
        if (index >= drinks.length()) {
            Log.d(TAG, "All drinks imported successfully");
            return;
        }

        try {
            JSONObject drinkJson = drinks.getJSONObject(index);
            Drink drink = new Drink();
            drink.setName(drinkJson.getString("name"));
            drink.setUserId("fd441a08-ad16-4b32-933e-55afeb6eef30");
            drink.setImage(drinkJson.getString("img"));
            
            // Map spiritId to categoryId using our mapping
            int spiritId = drinkJson.getInt("spiritId");
            String categoryId = spiritIdToUuidMap.get(spiritId);
            drink.setCategoryId(categoryId);
            
            drink.setInstruction("");
            
            // Combine description and tip
            String description = drinkJson.getString("description");
            if (drinkJson.has("tip")) {
                description += "\n" + drinkJson.getString("tip");
            }
            drink.setDescription(description);
            
            drink.setRate(drinkJson.getDouble("rate"));

            drinkDAO.addDrink(drink,
                aVoid -> {
                    Log.d(TAG, "Drink imported: " + drink.getName());
                    importDrinks(drinks, index + 1);
                },
                e -> {
                    Log.e(TAG, "Error importing drink: " + e.getMessage());
                    importDrinks(drinks, index + 1);
                }
            );
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing drink: " + e.getMessage());
            importDrinks(drinks, index + 1);
        }
    }

    private String loadJSONFromAsset(String fileName) throws IOException {
        String json;
        try (InputStream is = context.getAssets().open(fileName)) {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            json = new String(buffer, StandardCharsets.UTF_8);
        }
        return json;
    }
} 