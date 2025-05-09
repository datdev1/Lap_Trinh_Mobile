package com.b21dccn216.pocketcocktail.dao;

import android.content.Context;
import android.net.Uri;

import com.b21dccn216.pocketcocktail.model.Ingredient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class IngredientDAO {
    private final FirebaseFirestore db;
    private final CollectionReference ingredientRef;
    private final ImageDAO imageDAO;

    public static final String ERROR_USER_NOT_AUTH = "User not authenticated";

    public IngredientDAO() {
        db = FirebaseFirestore.getInstance();
        ingredientRef = db.collection("ingredient");
        imageDAO = new ImageDAO();
    }

    public interface IngredientCallback {
        void onIngredientLoaded(Ingredient ingredient);
        void onError(Exception e);
    }

    public interface IngredientListCallback {
        void onIngredientListLoaded(List<Ingredient> ingredients);
        void onError(Exception e);
    }

    public void addIngredient(Ingredient ingredient, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        ingredientRef.document(ingredient.generateUUID())
                .set(ingredient)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void addIngredientWithImage(Context context, Ingredient ingredient, Uri imageUri,
                                     OnSuccessListener<Void> onSuccess,
                                     OnFailureListener onFailure) {
        String title = ImageDAO.ImageDaoFolderForIngredient + "_" + ingredient.getName() + "_" + ingredient.getUuid();
        new ImageDAO().uploadImageToImgur(context, imageUri, title, new ImageDAO.UploadCallback() {
            @Override
            public void onSuccess(String imageUrl) {
                ingredient.generateUUID();
                ingredient.setImage(imageUrl);

                ingredientRef.document(ingredient.getUuid())
                        .set(ingredient)
                        .addOnSuccessListener(onSuccess)
                        .addOnFailureListener(onFailure);
            }

            @Override
            public void onFailure(Exception e) {
                onFailure.onFailure(e);
            }
        });
    }

    public void getIngredient(String ingredientUuid, OnSuccessListener<DocumentSnapshot> onSuccess, OnFailureListener onFailure) {
        ingredientRef.document(ingredientUuid).get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getIngredient(Ingredient ingredient, OnSuccessListener<DocumentSnapshot> onSuccess, OnFailureListener onFailure) {
        ingredientRef.document(ingredient.getUuid()).get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getIngredientByName(String name, OnSuccessListener<QuerySnapshot> onSuccess, OnFailureListener onFailure) {
        ingredientRef.whereEqualTo("name", name)
                .get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getAllIngredients(OnSuccessListener<QuerySnapshot> onSuccess, OnFailureListener onFailure) {
        ingredientRef.get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getAllIngredients(IngredientListCallback callback) {
        ingredientRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Ingredient> ingredients = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Ingredient ingredient = doc.toObject(Ingredient.class);
                        if (ingredient != null) {
                            ingredients.add(ingredient);
                        }
                    }
                    callback.onIngredientListLoaded(ingredients);
                })
                .addOnFailureListener(callback::onError);
    }

    public void updateIngredient(Ingredient updatedIngredient, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        ingredientRef.document(updatedIngredient.getUuid())
                .set(updatedIngredient)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void updateIngredientWithImage(Context context, Ingredient updatedIngredient, Uri newImageUri,
                                        OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        if (newImageUri != null) {
            String title = ImageDAO.ImageDaoFolderForIngredient + "_" + updatedIngredient.getName() + "_" + updatedIngredient.getUuid();
            new ImageDAO().uploadImageToImgur(context, newImageUri, title, new ImageDAO.UploadCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    updatedIngredient.setImage(imageUrl);
                    updateIngredient(updatedIngredient, onSuccess, onFailure);
                }

                @Override
                public void onFailure(Exception e) {
                    onFailure.onFailure(e);
                }
            });
        } else {
            updateIngredient(updatedIngredient, onSuccess, onFailure);
        }
    }

    public void deleteIngredient(String uuid, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        ingredientRef.document(uuid)
                .delete()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }
} 