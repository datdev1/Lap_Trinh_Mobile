package com.b21dccn216.pocketcocktail.dao;

import com.b21dccn216.pocketcocktail.model.Drink;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DrinkDAO {
    private final FirebaseFirestore db;
    private final CollectionReference drinkRef;

    public static final String ERROR_USER_NOT_AUTH = "User not authenticated";

    public DrinkDAO() {
        db = FirebaseFirestore.getInstance();
        drinkRef = db.collection("drink");
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

    // 5. Delete a drink by ID
    public void deleteDrink(String id, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        drinkRef.document(id)
                .delete()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

}
