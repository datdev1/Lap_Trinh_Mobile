package com.b21dccn216.pocketcocktail.dao;

import android.content.Context;
import android.net.Uri;
import com.b21dccn216.pocketcocktail.model.Ingredient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private Map<String, Object> convertIngredientToMap(Ingredient ingredient) {
        Map<String, Object> data = new HashMap<>();
        data.put("uuid", ingredient.getUuid());
        data.put("name", ingredient.getName());
        data.put("description", ingredient.getDescription());
        data.put("unit", ingredient.getUnit());
        data.put("image", ingredient.getImage());
        data.put("createdAt", ingredient.getCreatedAtTimestamp());
        data.put("updatedAt", ingredient.getUpdatedAtTimestamp());
        return data;
    }

    private Ingredient convertDocumentToIngredient(DocumentSnapshot doc) {
        Ingredient ingredient = new Ingredient();
        ingredient.setUuid(doc.getString("uuid"));
        ingredient.setName(doc.getString("name"));
        ingredient.setDescription(doc.getString("description"));
        ingredient.setUnit(doc.getString("unit"));
        ingredient.setImage(doc.getString("image"));
        
        Timestamp createdAt = doc.getTimestamp("createdAt");
        if (createdAt != null) {
            ingredient.setCreatedAtTimestamp(createdAt);
        }
        
        Timestamp updatedAt = doc.getTimestamp("updatedAt");
        if (updatedAt != null) {
            ingredient.setUpdatedAtTimestamp(updatedAt);
        }
        
        return ingredient;
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
        Map<String, Object> data = convertIngredientToMap(ingredient);
        ingredientRef.document(ingredient.generateUUID())
                .set(data)
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
                Map<String, Object> data = convertIngredientToMap(ingredient);

                ingredientRef.document(ingredient.getUuid())
                        .set(data)
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

    public void getIngredient(String ingredientUuid, IngredientCallback callback) {
        ingredientRef.document(ingredientUuid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Ingredient ingredient = convertDocumentToIngredient(documentSnapshot);
                    callback.onIngredientLoaded(ingredient);
                })
                .addOnFailureListener(callback::onError);
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
                        Ingredient ingredient = convertDocumentToIngredient(doc);
                        if (ingredient != null) {
                            ingredients.add(ingredient);
                        }
                    }
                    callback.onIngredientListLoaded(ingredients);
                })
                .addOnFailureListener(callback::onError);
    }

    public void updateIngredient(Ingredient updatedIngredient, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        Map<String, Object> data = convertIngredientToMap(updatedIngredient);
        ingredientRef.document(updatedIngredient.getUuid())
                .set(data)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void updateIngredientWithImage(Context context, Ingredient updatedIngredient, @Nullable Uri newImageUri,
                                     OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        if (newImageUri != null) {
            String title = ImageDAO.ImageDaoFolderForIngredient + "_" + updatedIngredient.getName() + "_" + updatedIngredient.getUuid();
            new ImageDAO().uploadImageToImgur(context, newImageUri, title, new ImageDAO.UploadCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    updatedIngredient.setImage(imageUrl);
                    Map<String, Object> data = convertIngredientToMap(updatedIngredient);
                    
                    ingredientRef.document(updatedIngredient.getUuid())
                            .set(data)
                            .addOnSuccessListener(onSuccess)
                            .addOnFailureListener(onFailure);
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

    public void getIngredientDiscover(INGREDIENT_FIELD sortTag, Query.Direction sortOrder, int limit,
                                    IngredientListCallback callback) {
        ingredientRef
                .orderBy(sortTag.getValue(), sortOrder)
                .limit(limit)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Ingredient> ingredientList = new ArrayList<>();
                    for (DocumentSnapshot ingredientSnapshot : queryDocumentSnapshots.getDocuments()) {
                        Ingredient ingredient = ingredientSnapshot.toObject(Ingredient.class);
                        ingredientList.add(ingredient);
                    }
                    callback.onIngredientListLoaded(ingredientList);
                })
                .addOnFailureListener(callback::onError);
    }

    public enum INGREDIENT_FIELD{
        NAME("name");

        private final String value;

        INGREDIENT_FIELD(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public void searchIngredients(String query, IngredientListCallback callback) {
        ingredientRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Ingredient> ingredients = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Ingredient ingredient = convertDocumentToIngredient(doc);
                        if (ingredient != null && matchesSearchQuery(ingredient, query)) {
                            ingredients.add(ingredient);
                        }
                    }
                    callback.onIngredientListLoaded(ingredients);
                })
                .addOnFailureListener(callback::onError);
    }

    private boolean matchesSearchQuery(Ingredient ingredient, String searchQuery) {
        if (searchQuery.isEmpty()) {
            return true;
        }

        String query = searchQuery.toLowerCase();
        return (ingredient.getName() != null && ingredient.getName().toLowerCase().contains(query)) ||
                (ingredient.getDescription() != null && ingredient.getDescription().toLowerCase().contains(query)) ||
                (ingredient.getUnit() != null && ingredient.getUnit().toLowerCase().contains(query));
    }
} 