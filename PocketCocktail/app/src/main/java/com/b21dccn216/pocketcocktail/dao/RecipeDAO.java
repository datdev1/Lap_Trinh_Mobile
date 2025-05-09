package com.b21dccn216.pocketcocktail.dao;

import com.b21dccn216.pocketcocktail.model.Recipe;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RecipeDAO {
    private final FirebaseFirestore db;
    private final CollectionReference recipeRef;

    public static final String ERROR_USER_NOT_AUTH = "User not authenticated";

    public RecipeDAO() {
        db = FirebaseFirestore.getInstance();
        recipeRef = db.collection("recipe");
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
        recipeRef.document(recipe.generateUUID())
                .set(recipe)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getRecipe(String recipeUuid, OnSuccessListener<DocumentSnapshot> onSuccess, OnFailureListener onFailure) {
        recipeRef.document(recipeUuid).get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getRecipe(Recipe recipe, OnSuccessListener<DocumentSnapshot> onSuccess, OnFailureListener onFailure) {
        recipeRef.document(recipe.getUuid()).get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
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
                        Recipe recipe = doc.toObject(Recipe.class);
                        if (recipe != null) {
                            recipes.add(recipe);
                        }
                    }
                    callback.onRecipeListLoaded(recipes);
                })
                .addOnFailureListener(callback::onError);
    }

    public void updateRecipe(Recipe updatedRecipe, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        recipeRef.document(updatedRecipe.getUuid())
                .set(updatedRecipe)
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