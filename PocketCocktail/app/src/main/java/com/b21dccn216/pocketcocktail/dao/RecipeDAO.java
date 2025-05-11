package com.b21dccn216.pocketcocktail.dao;

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
        recipe.setAmount(doc.getDouble("amount"));
        
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

    public void addRecipe(Recipe recipe, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        Map<String, Object> data = convertRecipeToMap(recipe);
        recipeRef.document(recipe.generateUUID())
                .set(data)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getRecipe(String recipeUuid, OnSuccessListener<DocumentSnapshot> onSuccess, OnFailureListener onFailure) {
        recipeRef.document(recipeUuid).get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getRecipe(String recipeUuid, RecipeCallback callback) {
        recipeRef.document(recipeUuid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Recipe recipe = convertDocumentToRecipe(documentSnapshot);
                    callback.onRecipeLoaded(recipe);
                })
                .addOnFailureListener(callback::onError);
    }

    public void getRecipesByDrinkId(String drinkId, OnSuccessListener<QuerySnapshot> onSuccess, OnFailureListener onFailure) {
        recipeRef.whereEqualTo("drinkId", drinkId)
                .get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getRecipesByIngredientId(String ingredientId, OnSuccessListener<QuerySnapshot> onSuccess, OnFailureListener onFailure) {
        recipeRef.whereEqualTo("ingredientId", ingredientId)
                .get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getAllRecipes(OnSuccessListener<QuerySnapshot> onSuccess, OnFailureListener onFailure) {
        recipeRef.get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

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
} 