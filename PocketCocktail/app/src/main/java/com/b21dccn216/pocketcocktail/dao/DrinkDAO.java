package com.b21dccn216.pocketcocktail.dao;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

import com.b21dccn216.pocketcocktail.model.Drink;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DrinkDAO {
    private final FirebaseFirestore db;
    private final CollectionReference drinkRef;

    private final ImageDAO imageDAO;
    public static final String ERROR_USER_NOT_AUTH = "User not authenticated";

    public DrinkDAO() {
        db = FirebaseFirestore.getInstance();
        drinkRef = db.collection("drink");
        imageDAO = new ImageDAO();
    }
    public interface DrinkCallback {
        void onDrinkLoaded(Drink drink);
        void onError(Exception e);
    }

    public interface DrinkListCallback {
        void onDrinkListLoaded(List<Drink> drinks);
        void onError(Exception e);
    }

    public void addDrink(Drink drink, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        drinkRef.document(drink.generateUUID())
                .set(drink)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void addDrinkWithImage(Context context, Drink drink, Uri imageUri,
                                  OnSuccessListener<Void> onSuccess,
                                  OnFailureListener onFailure) {
        String title = ImageDAO.ImageDaoFolderForDrink + "_" + drink.getName() + "_" + drink.getUuid();
        new ImageDAO().uploadImageToImgur(context, imageUri, title, new ImageDAO.UploadCallback() {
            @Override
            public void onSuccess(String imageUrl) {
                drink.generateUUID();
                drink.setImage(imageUrl);

                drinkRef.document(drink.getUuid())
                        .set(drink)
                        .addOnSuccessListener(onSuccess)
                        .addOnFailureListener(onFailure);
            }

            @Override
            public void onFailure(Exception e) {
                onFailure.onFailure(e);
            }
        });
    }

    public void getDrink(String drinkUuid, OnSuccessListener<DocumentSnapshot> onSuccess, OnFailureListener onFailure) {
        drinkRef.document(drinkUuid).get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getDrink(Drink drink, OnSuccessListener<DocumentSnapshot> onSuccess, OnFailureListener onFailure) {
        drinkRef.document(drink.getUuid()).get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getDrinksByCategoryId(String categoryId, OnSuccessListener<QuerySnapshot> onSuccess, OnFailureListener onFailure) {
        drinkRef.whereEqualTo("categoryId", categoryId)
                .get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getAllDrinks(OnSuccessListener<QuerySnapshot> onSuccess, OnFailureListener onFailure) {
        drinkRef.get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getAllDrinks(DrinkListCallback callback) {
        drinkRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Drink> drinks = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Drink drink = doc.toObject(Drink.class);
                        if (drink != null) {
                            drinks.add(drink);
                        }
                    }
                    Log.e("load Drink", "getAllDrinks: " + drinks);
                    callback.onDrinkListLoaded(drinks);
                })
                .addOnFailureListener(callback::onError);
    }
    public void updateDrink(Drink updatedDrink, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        drinkRef.document(updatedDrink.getUuid())
                .set(updatedDrink)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void updateDrinkWithImage(Context context, Drink updatedDrink, @Nullable Uri newImageUri,
                                     OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {

        if (newImageUri != null) {
            // Nếu có ảnh mới → upload lên Imgur
            String title = ImageDAO.ImageDaoFolderForDrink + "_" + updatedDrink.getName() + "_" + updatedDrink.getUuid();
            new ImageDAO().uploadImageToImgur(context, newImageUri, title, new ImageDAO.UploadCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    updatedDrink.setImage(imageUrl); // Cập nhật ảnh mới
                    updateDrink(updatedDrink, onSuccess, onFailure); // Lưu vào Firestore
                }

                @Override
                public void onFailure(Exception e) {
                    onFailure.onFailure(e);
                }
            });

        } else {
            // Không có ảnh mới → chỉ cập nhật thông tin
            updateDrink(updatedDrink, onSuccess, onFailure);
        }
    }


    // 5. Delete a drink by ID
    public void deleteDrink(String uuid, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        drinkRef.document(uuid)
                .delete()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }


    public void getDrinksSortAndLimit(DRINK_FIELD sortTag, Query.Direction sortOrder, int limit,
                                      DrinkListCallback callback) {
        drinkRef
                .orderBy(sortTag.getValue(), sortOrder)
                .limit(limit)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Drink> drinkList = new ArrayList<>();
                    for (DocumentSnapshot drinkSnapshot : queryDocumentSnapshots.getDocuments()) {
                        Drink drink = drinkSnapshot.toObject(Drink.class);
                        drinkList.add(drink);
                    }
                    callback.onDrinkListLoaded(drinkList);
                })
                .addOnFailureListener(callback::onError);
    }


    public enum DRINK_FIELD{
        RATE("rate"),
        NAME("name");

        private final String value;

        DRINK_FIELD(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
