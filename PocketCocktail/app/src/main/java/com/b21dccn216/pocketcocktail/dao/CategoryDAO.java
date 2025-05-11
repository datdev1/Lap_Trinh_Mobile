package com.b21dccn216.pocketcocktail.dao;

import android.content.Context;
import android.net.Uri;
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
import java.util.List;

public class CategoryDAO {
    private final FirebaseFirestore db;
    private final CollectionReference categoryRef;

    private final ImageDAO imageDAO;

    public static final String ERROR_USER_NOT_AUTH = "User not authenticated";

    public CategoryDAO() {
        db = FirebaseFirestore.getInstance();
        categoryRef = db.collection("category");
        imageDAO = new ImageDAO();
    }
    public interface CategoryCallback {
        void onCategoryLoaded(Category category);
        void onError(Exception e);
    }

    public interface CategoryListCallback {
        void onCategoryListLoaded(List<Category> categorys);
        void onError(Exception e);
    }

    public void addCategory(Category category, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        categoryRef.document(category.generateUUID())
                .set(category)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void addCategoryWithImage(Context context, Category category, Uri imageUri,
                                     OnSuccessListener<Void> onSuccess,
                                     OnFailureListener onFailure) {
        String title = ImageDAO.ImageDaoFolderForCategory + "_" + category.getName() + "_" + category.getUuid();
        new ImageDAO().uploadImageToImgur(context, imageUri, title,new ImageDAO.UploadCallback() {
            @Override
            public void onSuccess(String imageUrl) {
                category.generateUUID();
                category.setImage(imageUrl);

                categoryRef.document(category.getUuid())
                        .set(category)
                        .addOnSuccessListener(onSuccess)
                        .addOnFailureListener(onFailure);
            }

            @Override
            public void onFailure(Exception e) {
                onFailure.onFailure(e);
            }
        });
    }

    public void getCategory(String categoryUuid, OnSuccessListener<DocumentSnapshot> onSuccess, OnFailureListener onFailure) {
        categoryRef.document(categoryUuid).get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getCategory(String categoryUuid, CategoryCallback callback) {
        categoryRef.document(categoryUuid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Category category = documentSnapshot.toObject(Category.class);
                    callback.onCategoryLoaded(category);
                })
                .addOnFailureListener(callback::onError);
    }

    public void getCategory(Category category, OnSuccessListener<DocumentSnapshot> onSuccess, OnFailureListener onFailure) {
        categoryRef.document(category.getUuid()).get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }


    public void getAllCategorys(OnSuccessListener<QuerySnapshot> onSuccess, OnFailureListener onFailure) {
        categoryRef.get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getAllCategorys(CategoryListCallback callback) {
        categoryRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Category> categorys = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Category category = doc.toObject(Category.class);
                        if (category != null) {
                            categorys.add(category);
                        }
                    }
                    callback.onCategoryListLoaded(categorys);
                })
                .addOnFailureListener(callback::onError);
    }


    public void updateCategory(Category updatedCategory, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        categoryRef.document(updatedCategory.getUuid())
                .set(updatedCategory)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void updateCategoryWithImage(Context context, Category updatedCategory, @Nullable Uri newImageUri,
                                     OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        if (newImageUri != null) {
            // Nếu có ảnh mới → upload lên Imgur
            String title = ImageDAO.ImageDaoFolderForCategory + "_" + updatedCategory.getName() + "_" + updatedCategory.getUuid();
            new ImageDAO().uploadImageToImgur(context, newImageUri, title, new ImageDAO.UploadCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    updatedCategory.setImage(imageUrl); // Cập nhật ảnh mới
                    updateCategory(updatedCategory, onSuccess, onFailure); // Lưu vào Firestore
                }

                @Override
                public void onFailure(Exception e) {
                    onFailure.onFailure(e);
                }
            });
        } else {
            // Không có ảnh mới → chỉ cập nhật thông tin
            updateCategory(updatedCategory, onSuccess, onFailure);
        }
    }

    // 5. Delete a category by ID
    public void deleteCategory(String id, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        categoryRef.document(id)
                .delete()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
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
                        Category category = categorySnapshot.toObject(Category.class);
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
