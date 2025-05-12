package com.b21dccn216.pocketcocktail.dao;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import com.google.firebase.Timestamp;
import com.b21dccn216.pocketcocktail.model.Category;
import com.b21dccn216.pocketcocktail.model.Drink;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class CategoryDAO {
    private final FirebaseFirestore db;
    private final CollectionReference categoryRef;
    private final ImageDAO imageDAO;
    private static final String IMGUR_REFRESH_TOKEN = "2396b095b3402713d6dd7895146265c06f22fc71";
    private boolean isAuthenticated = false;
    private final CountDownLatch authLatch = new CountDownLatch(1);

    public static final String ERROR_USER_NOT_AUTH = "User not authenticated";

    public CategoryDAO() {
        db = FirebaseFirestore.getInstance();
        categoryRef = db.collection("category");
        imageDAO = new ImageDAO();
        authenticateImgur();
    }

    private void authenticateImgur() {
        imageDAO.authenticate(IMGUR_REFRESH_TOKEN, new ImageDAO.AuthCallback() {
            @Override
            public void onSuccess() {
                Log.d("CategoryDAO", "Imgur authentication successful");
                isAuthenticated = true;
                authLatch.countDown();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("CategoryDAO", "Imgur authentication failed: " + e.getMessage());
                isAuthenticated = false;
                authLatch.countDown();
            }
        });
    }

    private void waitForAuthentication() {
        try {
            authLatch.await();
        } catch (InterruptedException e) {
            Log.e("CategoryDAO", "Authentication wait interrupted", e);
        }
    }

    public interface CategoryCallback {
        void onCategoryLoaded(Category category);
        void onError(Exception e);
    }

    public interface CategoryListCallback {
        void onCategoryListLoaded(List<Category> categorys);
        void onError(Exception e);
    }

    private Map<String, Object> convertCategoryToMap(Category category) {
        Map<String, Object> data = new HashMap<>();
        data.put("uuid", category.getUuid());
        data.put("name", category.getName());
        data.put("description", category.getDescription());
        data.put("image", category.getImage());
        data.put("createdAt", category.getCreatedAtTimestamp());
        data.put("updatedAt", category.getUpdatedAtTimestamp());
        return data;
    }

    private Category convertDocumentToCategory(DocumentSnapshot doc) {
        Category category = new Category();
        category.setUuid(doc.getString("uuid"));
        category.setName(doc.getString("name"));
        category.setDescription(doc.getString("description"));
        category.setImage(doc.getString("image"));
        
        Timestamp createdAt = doc.getTimestamp("createdAt");
        if (createdAt != null) {
            category.setCreatedAtTimestamp(createdAt);
        }
        
        Timestamp updatedAt = doc.getTimestamp("updatedAt");
        if (updatedAt != null) {
            category.setUpdatedAtTimestamp(updatedAt);
        }
        
        return category;
    }

    public void addCategory(Category category, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        category.generateUUID();
        Map<String, Object> data = convertCategoryToMap(category);
        categoryRef.document(category.getUuid())
                .set(data)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void addCategoryWithImage(Context context, Category category, Uri imageUri,
                                     OnSuccessListener<Void> onSuccess,
                                     OnFailureListener onFailure) {
        //waitForAuthentication();
        if (!isAuthenticated) {
            onFailure.onFailure(new Exception("Imgur authentication failed"));
            return;
        }

        String title = ImageDAO.ImageDaoFolderForCategory + "_" + category.getName() + "_" + category.getUuid();
        imageDAO.uploadImageToImgur(context, imageUri, title, new ImageDAO.UploadCallback() {
            @Override
            public void onSuccess(String imageUrl) {
                category.generateUUID();
                category.setImage(imageUrl);
                Map<String, Object> data = convertCategoryToMap(category);
                
                categoryRef.document(category.getUuid())
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

    public void updateCategory(Category updatedCategory, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        Map<String, Object> data = convertCategoryToMap(updatedCategory);
        categoryRef.document(updatedCategory.getUuid())
                .set(data)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void updateCategoryWithImage(Context context, Category updatedCategory, @Nullable Uri newImageUri,
                                        OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        //waitForAuthentication();
        if (!isAuthenticated) {
            onFailure.onFailure(new Exception("Imgur authentication failed"));
            return;
        }

        if (newImageUri != null) {
            // First delete the old image if it exists
            if (updatedCategory.getImage() != null && !updatedCategory.getImage().isEmpty()) {
                imageDAO.deleteImageFromImgur(updatedCategory.getImage(), new ImageDAO.DeleteCallback() {
                    @Override
                    public void onSuccess() {
                        // After deleting old image, upload new one
                        uploadNewImage(context, updatedCategory, newImageUri, onSuccess, onFailure);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // Even if delete fails, continue with new image upload
                        Log.e("CategoryDAO", "Failed to delete old image for category " + updatedCategory.getUuid() + ": " + e.getMessage());
                        uploadNewImage(context, updatedCategory, newImageUri, onSuccess, onFailure);
                    }
                });
            } else {
                // If no old image, just upload new one
                uploadNewImage(context, updatedCategory, newImageUri, onSuccess, onFailure);
            }
        } else {
            updateCategory(updatedCategory, onSuccess, onFailure);
        }
    }

    private void uploadNewImage(Context context, Category updatedCategory, Uri newImageUri,
                              OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        String title = ImageDAO.ImageDaoFolderForCategory + "_" + updatedCategory.getName() + "_" + updatedCategory.getUuid();
        imageDAO.uploadImageToImgur(context, newImageUri, title, new ImageDAO.UploadCallback() {
            @Override
            public void onSuccess(String imageUrl) {
                updatedCategory.setImage(imageUrl);
                Map<String, Object> data = convertCategoryToMap(updatedCategory);

                categoryRef.document(updatedCategory.getUuid())
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

    public void deleteCategory(String id, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        //waitForAuthentication();
        if (!isAuthenticated) {
            onFailure.onFailure(new Exception("Imgur authentication failed"));
            return;
        }

        // First get the category to get its image URL
        categoryRef.document(id).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Category category = convertDocumentToCategory(documentSnapshot);
                    if (category != null && category.getImage() != null && !category.getImage().isEmpty()) {
                        // Delete the image first
                        imageDAO.deleteImageFromImgur(category.getImage(), new ImageDAO.DeleteCallback() {
                            @Override
                            public void onSuccess() {
                                // After image is deleted, delete the category document
                                deleteCategoryDocument(id, onSuccess, onFailure);
                            }

                            @Override
                            public void onFailure(Exception e) {
                                // Even if image deletion fails, continue with category deletion
                                Log.e("CategoryDAO", "Failed to delete image for category " + id + ": " + e.getMessage());
                                deleteCategoryDocument(id, onSuccess, onFailure);
                            }
                        });
                    } else {
                        // If no image, just delete the category
                        deleteCategoryDocument(id, onSuccess, onFailure);
                    }
                })
                .addOnFailureListener(onFailure);
    }

    private void deleteCategoryDocument(String id, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        categoryRef.document(id)
                .delete()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getCategory(String categoryUuid, CategoryCallback callback) {
        categoryRef.document(categoryUuid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Category category = convertDocumentToCategory(documentSnapshot);
                    callback.onCategoryLoaded(category);
                })
                .addOnFailureListener(callback::onError);
    }

    public void getAllCategorys(CategoryListCallback callback) {
        categoryRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Category> categories = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Category category = convertDocumentToCategory(doc);
                        if (category != null) {
                            categories.add(category);
                        }
                    }
                    callback.onCategoryListLoaded(categories);
                })
                .addOnFailureListener(callback::onError);
    }

    public void getCategoryDiscover(CATEGORY_FIELD sortTag, Query.Direction sortOrder, int limit,
                                      CategoryListCallback callback) {
        categoryRef
                .orderBy(sortTag.getValue(), sortOrder)
                .limit(limit)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Category> categoryList = new ArrayList<>();
                    for (DocumentSnapshot categorySnapshot : queryDocumentSnapshots.getDocuments()) {
                        Category category = convertDocumentToCategory(categorySnapshot);
                        categoryList.add(category);
                    }
                    callback.onCategoryListLoaded(categoryList);
                })
                .addOnFailureListener(callback::onError);
    }

    public enum CATEGORY_FIELD{
        NAME("name");

        private final String value;

        CATEGORY_FIELD(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

}
