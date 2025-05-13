package com.b21dccn216.pocketcocktail.dao;

import android.util.Log;

import com.b21dccn216.pocketcocktail.model.Recipe;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeDAO {
    private final FirebaseFirestore db;
    private final CollectionReference recipeRef;

    public static final String ERROR_USER_NOT_AUTH = "User not authenticated";

    public RecipeDAO() {
        db = FirebaseFirestore.getInstance();
        recipeRef = db.collection("recipe");
    }

    private Map<String, Object> convertRecipeToMap(Recipe recipe) {
        Map<String, Object> data = new HashMap<>();
        data.put("uuid", recipe.getUuid());
        data.put("drinkId", recipe.getDrinkId());
        data.put("ingredientId", recipe.getIngredientId());
        data.put("amount", recipe.getAmount());
        data.put("createdAt", recipe.getCreatedAtTimestamp());
        data.put("updatedAt", recipe.getUpdatedAtTimestamp());
        return data;
    }

    private Recipe convertDocumentToRecipe(DocumentSnapshot doc) {
        Recipe recipe = new Recipe();
        recipe.setUuid(doc.getString("uuid"));
        recipe.setDrinkId(doc.getString("drinkId"));
        recipe.setIngredientId(doc.getString("ingredientId"));

        Double amount = doc.getDouble("amount");
        recipe.setAmount(amount != null ? amount.floatValue() : 0);
        
        Timestamp createdAt = doc.getTimestamp("createdAt");
        if (createdAt != null) {
            recipe.setCreatedAtTimestamp(createdAt);
        }
        
        Timestamp updatedAt = doc.getTimestamp("updatedAt");
        if (updatedAt != null) {
            recipe.setUpdatedAtTimestamp(updatedAt);
        }
        
        return recipe;
    }

    public interface RecipeCallback {
        void onRecipeLoaded(Recipe recipe);
        void onError(Exception e);
    }

    public interface RecipeListCallback {
        void onRecipeListLoaded(List<Recipe> recipes);
        void onError(Exception e);
    }

    public interface DrinkIDListCallback {
        void onDrinkIDListLoaded(List<String> drinkIds);
        void onError(Exception e);
    }

    public void addRecipe(Recipe recipe, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        recipe.generateUUID();
        Map<String, Object> data = convertRecipeToMap(recipe);
        recipeRef.document(recipe.getUuid())
                .set(data)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

//    public void getRecipe(String recipeUuid, OnSuccessListener<DocumentSnapshot> onSuccess, OnFailureListener onFailure) {
//        recipeRef.document(recipeUuid).get()
//                .addOnSuccessListener(onSuccess)
//                .addOnFailureListener(onFailure);
//    }


    public void getRecipe(String recipeUuid, RecipeCallback callback) {
        recipeRef.document(recipeUuid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Recipe recipe = convertDocumentToRecipe(documentSnapshot);
                    callback.onRecipeLoaded(recipe);
                })
                .addOnFailureListener(callback::onError);
    }

//    public void getRecipesByDrinkId(String drinkId, OnSuccessListener<QuerySnapshot> onSuccess, OnFailureListener onFailure) {
//        recipeRef.whereEqualTo("drinkId", drinkId)
//                .get()
//                .addOnSuccessListener(onSuccess)
//                .addOnFailureListener(onFailure);
//    }

    public void getRecipesByDrinkId(String drinkId, RecipeListCallback callback) {
        recipeRef.whereEqualTo("drinkId", drinkId)
                .get()
                .addOnSuccessListener(
                        querySnapshot -> {
                            List<Recipe> recipes = new ArrayList<>();
                            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                Recipe recipe = convertDocumentToRecipe(doc);
                                if (recipe != null) {
                                    recipes.add(recipe);
                                }
                            }
                            callback.onRecipeListLoaded(recipes);
                        }
                )
                .addOnFailureListener(callback::onError);
    }

//    public void getRecipesByIngredientId(String ingredientId, OnSuccessListener<QuerySnapshot> onSuccess, OnFailureListener onFailure) {
//        recipeRef.whereEqualTo("ingredientId", ingredientId)
//                .get()
//                .addOnSuccessListener(onSuccess)
//                .addOnFailureListener(onFailure);
//    }

    public void getRecipesByIngredientId(String ingredientId, RecipeListCallback callback) {
        recipeRef.whereEqualTo("drinkId", ingredientId)
                .get()
                .addOnSuccessListener(
                        querySnapshot -> {
                            List<Recipe> recipes = new ArrayList<>();
                            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                Recipe recipe = convertDocumentToRecipe(doc);
                                if (recipe != null) {
                                    recipes.add(recipe);
                                }
                            }
                            callback.onRecipeListLoaded(recipes);
                        }
                )
                .addOnFailureListener(callback::onError);
    }

//    public void getAllRecipes(OnSuccessListener<QuerySnapshot> onSuccess, OnFailureListener onFailure) {
//        recipeRef.get()
//                .addOnSuccessListener(onSuccess)
//                .addOnFailureListener(onFailure);
//    }

    public void getAllRecipes(RecipeListCallback callback) {
        recipeRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Recipe> recipes = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Recipe recipe = convertDocumentToRecipe(doc);
                        if (recipe != null) {
                            recipes.add(recipe);
                        }
                    }
                    callback.onRecipeListLoaded(recipes);
                })
                .addOnFailureListener(callback::onError);
    }

    public void updateRecipe(Recipe updatedRecipe, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        Map<String, Object> data = convertRecipeToMap(updatedRecipe);
        recipeRef.document(updatedRecipe.getUuid())
                .set(data)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void deleteRecipe(String uuid, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        recipeRef.document(uuid)
                .delete()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void searchDrinkIDByIngredient(List<String> drinkIds, List<String> ingredientIds, DrinkIDListCallback callback) {
//        recipeRef.whereArrayContainsAny("drinkId", drinkIds)
//                .whereArrayContainsAny("ingredientId", ingredientIds)
        if (ingredientIds.isEmpty()) {
            callback.onDrinkIDListLoaded(new ArrayList<>());
            return;
        }
        recipeRef//.whereIn("drinkId", drinkIds)
                .whereIn("ingredientId", ingredientIds)
                .get()
                .addOnSuccessListener(querySnapshot -> {
//                    List<String> matchingDrinkIds = new ArrayList<>();
//                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
//                        Recipe recipe = convertDocumentToRecipe(doc);
//                        if (recipe != null && !matchingDrinkIds.contains(recipe.getDrinkId())) {
//                            matchingDrinkIds.add(recipe.getDrinkId());
//                        }
//                    }

                    // Map to store drinkId -> count of matching ingredients
                    Map<String, Integer> drinkIngredientCount = new HashMap<>();
                    List<String> matchingDrinkIds = new ArrayList<>();

                    // Count how many ingredients each drink has from our list
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Recipe recipe = convertDocumentToRecipe(doc);
                        if (recipe != null) {
                            String drinkId = recipe.getDrinkId();
                            drinkIngredientCount.put(drinkId,
                                    drinkIngredientCount.getOrDefault(drinkId, 0) + 1);
                        }
                    }

                    // Only include drinks that have ALL required ingredients
                    for (Map.Entry<String, Integer> entry : drinkIngredientCount.entrySet()) {
                        if (entry.getValue() == ingredientIds.size()) {
                            matchingDrinkIds.add(entry.getKey());
                        }
                    }
                    callback.onDrinkIDListLoaded(matchingDrinkIds);
                })
                .addOnFailureListener(e ->
                {
                    Log.e("DrinkDAO","RecipeDAO Trường hợp 1: Nếu có Category / Name và có list IngredientID" + e.toString());
                    callback.onError(e);

                });
    }

    public void searchDrinkIDByIngredient(List<String> ingredientIds, DrinkIDListCallback callback) {
        if (ingredientIds.isEmpty()) {
            callback.onDrinkIDListLoaded(new ArrayList<>());
            return;
        }
        recipeRef.whereIn("ingredientId", ingredientIds)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    // Map to store drinkId -> count of matching ingredients
                    Map<String, Integer> drinkIngredientCount = new HashMap<>();
                    List<String> matchingDrinkIds = new ArrayList<>();

                    // Count how many ingredients each drink has from our list
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Recipe recipe = convertDocumentToRecipe(doc);
                        if (recipe != null) {
                            String drinkId = recipe.getDrinkId();
                            drinkIngredientCount.put(drinkId, 
                                drinkIngredientCount.getOrDefault(drinkId, 0) + 1);
                        }
                    }

                    // Only include drinks that have ALL required ingredients
                    for (Map.Entry<String, Integer> entry : drinkIngredientCount.entrySet()) {
                        if (entry.getValue() == ingredientIds.size()) {
                            matchingDrinkIds.add(entry.getKey());
                        }
                    }

                    callback.onDrinkIDListLoaded(matchingDrinkIds);
                })
                .addOnFailureListener(e -> {
                    Log.e("DrinkDAO","RecipeDAO Trường hợp 3: Nếu không có Category / Name và có list IngredientID" + e.toString());
                    callback.onError(e);
                });
    }

} 