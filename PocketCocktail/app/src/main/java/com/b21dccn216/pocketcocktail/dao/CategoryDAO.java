package com.b21dccn216.pocketcocktail.dao;

import android.content.Context;
import android.net.Uri;

import com.b21dccn216.pocketcocktail.model.Category;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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

    // 5. Delete a category by ID
    public void deleteCategory(String id, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        categoryRef.document(id)
                .delete()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }


}
