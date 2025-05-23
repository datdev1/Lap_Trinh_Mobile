package com.b21dccn216.pocketcocktail.dao;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.b21dccn216.pocketcocktail.helper.DialogHelper;
import com.b21dccn216.pocketcocktail.helper.HelperDialog;
import com.b21dccn216.pocketcocktail.model.Ingredient;
import com.b21dccn216.pocketcocktail.model.Recipe;
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
import java.util.concurrent.CountDownLatch;

public class IngredientDAO {
    private final FirebaseFirestore db;
    private final CollectionReference ingredientRef;

//    private final ImageDAO imageDAO;
    private ImageDAO imageDAO; // Khai báo để tránh lỗi các hàm Imagur, comment lại và dùng private final ImageDAO imageDAO; để dùng lại Imgur

    private static final String IMGUR_REFRESH_TOKEN = "2396b095b3402713d6dd7895146265c06f22fc71";
    private boolean isAuthenticated = false;
    private final CountDownLatch authLatch = new CountDownLatch(1);

    public static final String ERROR_USER_NOT_AUTH = "User not authenticated";


    public IngredientDAO() {
        db = FirebaseFirestore.getInstance();
        ingredientRef = db.collection("ingredient");
//        imageDAO = new ImageDAO();
//        authenticateImgur();
    }



    private void authenticateImgur() {
        imageDAO.authenticate(IMGUR_REFRESH_TOKEN, new ImageDAO.AuthCallback() {
            @Override
            public void onSuccess() {
                Log.d("vietdung", "Imgur authentication successful");
                isAuthenticated = true;
                authLatch.countDown();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("vietdung", "Imgur authentication failed: " + e.getMessage());
                isAuthenticated = false;
                authLatch.countDown();
            }
        });
    }

    private void waitForAuthentication() {
        try {
            authLatch.await();
        } catch (InterruptedException e) {
            Log.e("vietdung", "Authentication wait interrupted", e);
        }
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
        ingredient.generateUUID();
        Map<String, Object> data = convertIngredientToMap(ingredient);
        ingredientRef.document(ingredient.getUuid())
                .set(data)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void addIngredientWithImageWithIgmur(Context context, Ingredient ingredient, Uri imageUri,
                                     OnSuccessListener<Void> onSuccess,
                                     OnFailureListener onFailure) {
        String title = ImageDAO.ImageDaoFolderForIngredient + "_" + ingredient.getName() + "_" + ingredient.getUuid();
        
        if (isAuthenticated) {
            imageDAO.uploadImageAuthenticated(context, imageUri, title, new ImageDAO.UploadCallback() {
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
        } else {
            imageDAO.uploadImageAnonymous(context, imageUri, title, new ImageDAO.UploadCallback() {
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
    }

//    public void getIngredient(String ingredientUuid, OnSuccessListener<DocumentSnapshot> onSuccess, OnFailureListener onFailure) {
//        ingredientRef.document(ingredientUuid).get()
//                .addOnSuccessListener(onSuccess)
//                .addOnFailureListener(onFailure);
//    }

    public void getIngredient(String ingredientUuid, IngredientCallback callback) {
        ingredientRef.document(ingredientUuid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Ingredient ingredient = convertDocumentToIngredient(documentSnapshot);
                    callback.onIngredientLoaded(ingredient);
                })
                .addOnFailureListener(callback::onError);
    }

//    public void getIngredientByName(String name, OnSuccessListener<QuerySnapshot> onSuccess, OnFailureListener onFailure) {
//        ingredientRef.whereEqualTo("name", name)
//                .get()
//                .addOnSuccessListener(onSuccess)
//                .addOnFailureListener(onFailure);
//    }

    public void getIngredientByName(String name, IngredientListCallback callback) {
        ingredientRef.whereEqualTo("name", name)
                .get()
                .addOnSuccessListener(
                        querySnapshot -> {
                            List<Ingredient> ingredients = new ArrayList<>();
                            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                Ingredient ingredient = convertDocumentToIngredient(doc);
                                if (ingredient != null) {
                                    ingredients.add(ingredient);
                                }
                                callback.onIngredientListLoaded(ingredients);
                            }
                        }
                )
                .addOnFailureListener(callback::onError);
    }

//    public void getAllIngredients(OnSuccessListener<QuerySnapshot> onSuccess, OnFailureListener onFailure) {
//        ingredientRef.get()
//                .addOnSuccessListener(onSuccess)
//                .addOnFailureListener(onFailure);
//    }

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
                    ingredients.sort((a, b) -> a.getName().compareTo(b.getName()));
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



    private void checkIngredientDependencies(String ingredientId, OnSuccessListener<Boolean> onSuccess, OnFailureListener onFailure) {
        // Check Recipe dependencies
        RecipeDAO recipeDAO = new RecipeDAO();
        recipeDAO.getRecipesByIngredientId(ingredientId, new RecipeDAO.RecipeListCallback() {
            @Override
            public void onRecipeListLoaded(List<Recipe> recipes) {
                Log.d("vietdung", "onRecipeListLoaded: " + recipes);
                if (!recipes.isEmpty()) {
                    onFailure.onFailure(new Exception("Cannot delete ingredient: It is used in recipes"));
                    return;
                }
                // If no dependencies found, allow deletion
                onSuccess.onSuccess(true);
            }

            @Override
            public void onError(Exception e) {
                onFailure.onFailure(e);
            }
        });
    }



    private void deleteIngredientDocument(String uuid, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        ingredientRef.document(uuid)
                .delete()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getAllIngredientSortAndLimit(INGREDIENT_FIELD sortField, Query.Direction sortOrder, int limit,
                                    IngredientListCallback callback) {
        ingredientRef
                .orderBy(sortField.getValue(), sortOrder)
                .limit(limit)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Ingredient> ingredientList = new ArrayList<>();
                    for (DocumentSnapshot ingredientSnapshot : queryDocumentSnapshots.getDocuments()) {
                        Ingredient ingredient = convertDocumentToIngredient(ingredientSnapshot);
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
        ingredientRef//.whereArrayContains("name", query)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Ingredient> ingredients = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Ingredient ingredient = convertDocumentToIngredient(doc);
                        if (ingredient != null && matchesSearchQuery(ingredient, query)) {
                            ingredients.add(ingredient);
                        }
//                        ingredients.add(ingredient);
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

    //    public void updateIngredientWithImageWithIgmur(Context context, Ingredient updatedIngredient, @Nullable Uri newImageUri,
//                                     OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
//        if (newImageUri != null) {
//            String title = ImageDAO.ImageDaoFolderForIngredient + "_" + updatedIngredient.getName() + "_" + updatedIngredient.getUuid();
//
//            if (isAuthenticated) {
//                // Delete old image first if it exists
//                if (updatedIngredient.getImage() != null && !updatedIngredient.getImage().isEmpty()) {
//                    imageDAO.deleteImageFromImgur(updatedIngredient.getImage(), new ImageDAO.DeleteCallback() {
//                        @Override
//                        public void onSuccess() {
//                            uploadNewImageAuthenticatedWithIgmur(context, updatedIngredient, newImageUri, title, onSuccess, onFailure);
//                        }
//
//                        @Override
//                        public void onFailure(Exception e) {
//                            Log.e("vietdung", "Failed to delete old image: " + e.getMessage());
//                            uploadNewImageAuthenticatedWithIgmur(context, updatedIngredient, newImageUri, title, onSuccess, onFailure);
//                        }
//                    });
//                } else {
//                    uploadNewImageAuthenticatedWithIgmur(context, updatedIngredient, newImageUri, title, onSuccess, onFailure);
//                }
//            } else {
//                // Anonymous upload without deleting old image
//                uploadNewImageAnonymousWithIgmur(context, updatedIngredient, newImageUri, title, onSuccess, onFailure);
//            }
//        } else {
//            updateIngredient(updatedIngredient, onSuccess, onFailure);
//        }
//    }
//
//    private void uploadNewImageAuthenticatedWithIgmur(Context context, Ingredient updatedIngredient, Uri newImageUri,
//                                           String title, OnSuccessListener<Void> onSuccess,
//                                           OnFailureListener onFailure) {
//        imageDAO.uploadImageAuthenticated(context, newImageUri, title, new ImageDAO.UploadCallback() {
//            @Override
//            public void onSuccess(String imageUrl) {
//                updatedIngredient.setImage(imageUrl);
//                Map<String, Object> data = convertIngredientToMap(updatedIngredient);
//
//                ingredientRef.document(updatedIngredient.getUuid())
//                        .set(data)
//                        .addOnSuccessListener(onSuccess)
//                        .addOnFailureListener(onFailure);
//            }
//
//            @Override
//            public void onFailure(Exception e) {
//                onFailure.onFailure(e);
//            }
//        });
//    }
//
//    private void uploadNewImageAnonymousWithIgmur(Context context, Ingredient updatedIngredient, Uri newImageUri,
//                                       String title, OnSuccessListener<Void> onSuccess,
//                                       OnFailureListener onFailure) {
//        imageDAO.uploadImageAnonymous(context, newImageUri, title, new ImageDAO.UploadCallback() {
//            @Override
//            public void onSuccess(String imageUrl) {
//                updatedIngredient.setImage(imageUrl);
//                Map<String, Object> data = convertIngredientToMap(updatedIngredient);
//
//                ingredientRef.document(updatedIngredient.getUuid())
//                        .set(data)
//                        .addOnSuccessListener(onSuccess)
//                        .addOnFailureListener(onFailure);
//            }
//
//            @Override
//            public void onFailure(Exception e) {
//                onFailure.onFailure(e);
//            }
//        });
//    }
//    public void deleteIngredientWithIgmur(String uuid, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
//        // First check dependencies
//        checkIngredientDependencies(uuid,
//            canDelete -> {
//                if (canDelete) {
//                    ingredientRef.document(uuid).get()
//                            .addOnSuccessListener(documentSnapshot -> {
//                                Ingredient ingredient = convertDocumentToIngredient(documentSnapshot);
//                                if (ingredient != null && ingredient.getImage() != null && !ingredient.getImage().isEmpty()) {
//                                    if (isAuthenticated) {
//                                        // Delete image if authenticated
//                                        imageDAO.deleteImageFromImgur(ingredient.getImage(), new ImageDAO.DeleteCallback() {
//                                            @Override
//                                            public void onSuccess() {
//                                                deleteIngredientDocument(uuid, onSuccess, onFailure);
//                                            }
//
//                                            @Override
//                                            public void onFailure(Exception e) {
//                                                Log.e("vietdung", "Failed to delete image: " + e.getMessage());
//                                                deleteIngredientDocument(uuid, onSuccess, onFailure);
//                                            }
//                                        });
//                                    } else {
//                                        // If not authenticated, just delete ingredient document
//                                        deleteIngredientDocument(uuid, onSuccess, onFailure);
//                                    }
//                                } else {
//                                    // If no image, just delete the ingredient
//                                    deleteIngredientDocument(uuid, onSuccess, onFailure);
//                                }
//                            })
//                            .addOnFailureListener(onFailure);
//                }
//            },
//            onFailure
//        );
//    }

    public void addIngredientWithImage(Context context, Ingredient ingredient, Uri imageUri,
                                     OnSuccessListener<Void> onSuccess,
                                     OnFailureListener onFailure) {
        String title = ingredient.getName().replaceAll("\\s+", "_") + "_" + ingredient.getUuid();
        ImageDAOCloudinary imageDAO = new ImageDAOCloudinary(context);
        
        imageDAO.uploadImage(context, imageUri, ImageDAOCloudinary.ImageDaoFolderForIngredient, title, new ImageDAOCloudinary.ImageUploadCallback() {
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

    public void updateIngredientWithImage(Context context, Ingredient updatedIngredient, Uri newImageUri,
                                        OnSuccessListener<Void> onSuccess,
                                        OnFailureListener onFailure) {
        ImageDAOCloudinary imageDAO = new ImageDAOCloudinary(context);
        
        // Delete old image if exists
        if (updatedIngredient.getImage() != null && !updatedIngredient.getImage().isEmpty()) {
            imageDAO.deleteImageByUrl(updatedIngredient.getImage(), new ImageDAOCloudinary.DeleteCallback() {
                @Override
                public void onSuccess() {
                    uploadNewImage(context, updatedIngredient, newImageUri, onSuccess, onFailure);
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("vietdung", "Failed to delete old image: " + e.getMessage());
                    // Continue with upload even if delete fails
                    uploadNewImage(context, updatedIngredient, newImageUri, onSuccess, onFailure);
                }
            });
        } else {
            uploadNewImage(context, updatedIngredient, newImageUri, onSuccess, onFailure);
        }
    }

    private void uploadNewImage(Context context, Ingredient ingredient, Uri imageUri,
                              OnSuccessListener<Void> onSuccess,
                              OnFailureListener onFailure) {
        String title = ingredient.getName().replaceAll("\\s+", "_") + "_" + ingredient.getUuid();
        ImageDAOCloudinary imageDAO = new ImageDAOCloudinary(context);
        
        imageDAO.uploadImage(context, imageUri, ImageDAOCloudinary.ImageDaoFolderForIngredient, title, new ImageDAOCloudinary.ImageUploadCallback() {
            @Override
            public void onSuccess(String imageUrl) {
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

    public void deleteIngredient(Context context, String uuid, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        // First check dependencies
        checkIngredientDependencies(uuid, 
            canDelete -> {
                if (canDelete) {
                    ingredientRef.document(uuid).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                Ingredient ingredient = convertDocumentToIngredient(documentSnapshot);
                                if (ingredient != null && ingredient.getImage() != null && !ingredient.getImage().isEmpty()) {
                                    // Try to delete image
                                    ImageDAOCloudinary imageDAO = new ImageDAOCloudinary(context);
                                    imageDAO.deleteImageByUrl(ingredient.getImage(), new ImageDAOCloudinary.DeleteCallback() {
                                        @Override
                                        public void onSuccess() {
                                            deleteIngredientDocument(uuid, onSuccess, onFailure);
                                        }

                                        @Override
                                        public void onFailure(Exception e) {
                                            Log.e("vietdung", "Failed to delete image: " + e.getMessage());
                                            // Continue with ingredient deletion even if image deletion fails
                                            deleteIngredientDocument(uuid, onSuccess, onFailure);
                                        }
                                    });
                                } else {
                                    // If no image, just delete the ingredient
                                    deleteIngredientDocument(uuid, onSuccess, onFailure);
                                }
                            })
                            .addOnFailureListener(onFailure);
                }
            },
            onFailure
        );
    }
} 