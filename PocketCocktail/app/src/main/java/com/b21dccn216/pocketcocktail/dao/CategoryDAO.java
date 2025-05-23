package com.b21dccn216.pocketcocktail.dao;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.b21dccn216.pocketcocktail.helper.DialogHelper;
import com.b21dccn216.pocketcocktail.helper.HelperDialog;
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

//    private final ImageDAO imageDAO;
    private ImageDAO imageDAO; // Khai báo để tránh lỗi các hàm Imagur, comment lại và dùng private final ImageDAO imageDAO; để dùng lại Imgur

    private static final String IMGUR_REFRESH_TOKEN = "2396b095b3402713d6dd7895146265c06f22fc71";
    private boolean isAuthenticated = false;
    private final CountDownLatch authLatch = new CountDownLatch(1);

    public static final String ERROR_USER_NOT_AUTH = "User not authenticated";

    public CategoryDAO() {
        db = FirebaseFirestore.getInstance();
        categoryRef = db.collection("category");
//        imageDAO = new ImageDAO();
//        authenticateImgur();
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

    public void addCategoryWithImageWithImgur(Context context, Category category, Uri imageUri,
                                     OnSuccessListener<Void> onSuccess,
                                     OnFailureListener onFailure) {
        String title = ImageDAO.ImageDaoFolderForCategory + "_" + category.getName() + "_" + category.getUuid();
        
        if (isAuthenticated) {
            imageDAO.uploadImageAuthenticated(context, imageUri, title, new ImageDAO.UploadCallback() {
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
        } else {
            imageDAO.uploadImageAnonymous(context, imageUri, title, new ImageDAO.UploadCallback() {
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
    }

    public void updateCategory(Category updatedCategory, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        Map<String, Object> data = convertCategoryToMap(updatedCategory);
        categoryRef.document(updatedCategory.getUuid())
                .set(data)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void updateCategoryWithImageWithImgur(Context context, Category updatedCategory, @Nullable Uri newImageUri,
                                        OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        if (newImageUri != null) {
            String title = ImageDAO.ImageDaoFolderForCategory + "_" + updatedCategory.getName() + "_" + updatedCategory.getUuid();
            
            if (isAuthenticated) {
                // Delete old image first if it exists
                if (updatedCategory.getImage() != null && !updatedCategory.getImage().isEmpty()) {
                    imageDAO.deleteImageFromImgur(updatedCategory.getImage(), new ImageDAO.DeleteCallback() {
                        @Override
                        public void onSuccess() {
                            uploadNewImageAuthenticatedWithImgur(context, updatedCategory, newImageUri, title, onSuccess, onFailure);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e("vietdung", "Failed to delete old image: " + e.getMessage());
                            uploadNewImageAuthenticatedWithImgur(context, updatedCategory, newImageUri, title, onSuccess, onFailure);
                        }
                    });
                } else {
                    uploadNewImageAuthenticatedWithImgur(context, updatedCategory, newImageUri, title, onSuccess, onFailure);
                }
            } else {
                // Anonymous upload without deleting old image
                uploadNewImageAnonymousWithImgur(context, updatedCategory, newImageUri, title, onSuccess, onFailure);
            }
        } else {
            updateCategory(updatedCategory, onSuccess, onFailure);
        }
    }

    private void uploadNewImageAuthenticatedWithImgur(Context context, Category updatedCategory, Uri newImageUri,
                                           String title, OnSuccessListener<Void> onSuccess, 
                                           OnFailureListener onFailure) {
        imageDAO.uploadImageAuthenticated(context, newImageUri, title, new ImageDAO.UploadCallback() {
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

    private void uploadNewImageAnonymousWithImgur(Context context, Category updatedCategory, Uri newImageUri,
                                       String title, OnSuccessListener<Void> onSuccess, 
                                       OnFailureListener onFailure) {
        imageDAO.uploadImageAnonymous(context, newImageUri, title, new ImageDAO.UploadCallback() {
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

    private void checkCategoryDependencies(String categoryId, OnSuccessListener<Boolean> onSuccess, OnFailureListener onFailure) {
        // Check Drink dependencies
        DrinkDAO drinkDAO = new DrinkDAO();
        drinkDAO.getDrinksByCategoryId(categoryId, new DrinkDAO.DrinkListCallback() {
            @Override
            public void onDrinkListLoaded(List<Drink> drinks) {
                if (!drinks.isEmpty()) {
                    onFailure.onFailure(new Exception("Cannot delete category: It has associated drinks"));
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

    public void deleteCategoryWithImgur(String uuid, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        // First check dependencies
        checkCategoryDependencies(uuid, 
            canDelete -> {
                if (canDelete) {
                    categoryRef.document(uuid).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                Category category = convertDocumentToCategory(documentSnapshot);
                                if (category != null && category.getImage() != null && !category.getImage().isEmpty()) {
                                    if (isAuthenticated) {
                                        // Delete image if authenticated
                                        imageDAO.deleteImageFromImgur(category.getImage(), new ImageDAO.DeleteCallback() {
                                            @Override
                                            public void onSuccess() {
                                                deleteCategoryDocument(uuid, onSuccess, onFailure);
                                            }

                                            @Override
                                            public void onFailure(Exception e) {
                                                Log.e("vietdung", "Failed to delete image: " + e.getMessage());
                                                deleteCategoryDocument(uuid, onSuccess, onFailure);
                                            }
                                        });
                                    } else {
                                        // If not authenticated, just delete category document
                                        deleteCategoryDocument(uuid, onSuccess, onFailure);
                                    }
                                } else {
                                    // If no image, just delete the category
                                    deleteCategoryDocument(uuid, onSuccess, onFailure);
                                }
                            })
                            .addOnFailureListener(onFailure);
                }
            },
            onFailure
        );
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
                    categories.sort((c1, c2) -> c1.getName().compareTo(c2.getName()));
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

    public void addCategoryWithImage(Context context, Category category, Uri imageUri,
                                   OnSuccessListener<Void> onSuccess,
                                   OnFailureListener onFailure) {
        String title = category.getName().replaceAll("\\s+", "_") + "_" + category.getUuid();
        ImageDAOCloudinary imageDAO = new ImageDAOCloudinary(context);
        
        imageDAO.uploadImage(context, imageUri, ImageDAOCloudinary.ImageDaoFolderForCategory, title, new ImageDAOCloudinary.ImageUploadCallback() {
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

    public void updateCategoryWithImage(Context context, Category updatedCategory, Uri newImageUri,
                                      OnSuccessListener<Void> onSuccess,
                                      OnFailureListener onFailure) {
        ImageDAOCloudinary imageDAO = new ImageDAOCloudinary(context);
        
        // Delete old image if exists
        if (updatedCategory.getImage() != null && !updatedCategory.getImage().isEmpty()) {
            imageDAO.deleteImageByUrl(updatedCategory.getImage(), new ImageDAOCloudinary.DeleteCallback() {
                @Override
                public void onSuccess() {
                    uploadNewImage(context, updatedCategory, newImageUri, onSuccess, onFailure);
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("vietdung", "Failed to delete old image: " + e.getMessage());
                    // Continue with upload even if delete fails
                    uploadNewImage(context, updatedCategory, newImageUri, onSuccess, onFailure);
                }
            });
        } else {
            uploadNewImage(context, updatedCategory, newImageUri, onSuccess, onFailure);
        }
    }

    private void uploadNewImage(Context context, Category category, Uri imageUri,
                              OnSuccessListener<Void> onSuccess,
                              OnFailureListener onFailure) {
        String title = category.getName().replaceAll("\\s+", "_") + "_" + category.getUuid();
        ImageDAOCloudinary imageDAO = new ImageDAOCloudinary(context);
        
        imageDAO.uploadImage(context, imageUri, ImageDAOCloudinary.ImageDaoFolderForCategory, title, new ImageDAOCloudinary.ImageUploadCallback() {
            @Override
            public void onSuccess(String imageUrl) {
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

    public void deleteCategory(Context context, String uuid, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        // First check dependencies
        checkCategoryDependencies(uuid, 
            canDelete -> {
                if (canDelete) {
                    categoryRef.document(uuid).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                Category category = convertDocumentToCategory(documentSnapshot);
                                if (category != null && category.getImage() != null && !category.getImage().isEmpty()) {
                                    // Try to delete image
                                    ImageDAOCloudinary imageDAO = new ImageDAOCloudinary(context);
                                    imageDAO.deleteImageByUrl(category.getImage(), new ImageDAOCloudinary.DeleteCallback() {
                                        @Override
                                        public void onSuccess() {
                                            deleteCategoryDocument(uuid, onSuccess, onFailure);
                                        }

                                        @Override
                                        public void onFailure(Exception e) {
                                            Log.e("vietdung", "Failed to delete image: " + e.getMessage());
                                            // Continue with category deletion even if image deletion fails
                                            deleteCategoryDocument(uuid, onSuccess, onFailure);
                                        }
                                    });
                                } else {
                                    // If no image, just delete the category
                                    deleteCategoryDocument(uuid, onSuccess, onFailure);
                                }
                            })
                            .addOnFailureListener(onFailure);
                }
            },
            onFailure
        );
    }

}
