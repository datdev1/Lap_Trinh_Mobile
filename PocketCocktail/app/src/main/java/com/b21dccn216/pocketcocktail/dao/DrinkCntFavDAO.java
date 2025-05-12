package com.b21dccn216.pocketcocktail.dao;

import android.util.Log;

import com.b21dccn216.pocketcocktail.model.DrinkCntFav;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DrinkCntFavDAO {
    private final FirebaseFirestore db;
    private final CollectionReference drinkCntFavRef;

    public static final String ERROR_USER_NOT_AUTH = "User not authenticated";

    public DrinkCntFavDAO() {
        db = FirebaseFirestore.getInstance();
        drinkCntFavRef = db.collection("drinkCntFav");
    }

    private Map<String, Object> convertDrinkCntFavToMap(DrinkCntFav drinkCntFav) {
        if (drinkCntFav.getUuid() == null || drinkCntFav.getDrinkId() == null) {
            throw new IllegalArgumentException("DrinkCntFav must have uuid and drinkId");
        }
        if (drinkCntFav.getCreatedAtTimestamp() == null) {
            drinkCntFav.setCreatedAtTimestamp(Timestamp.now());
        }
        if (drinkCntFav.getUpdatedAtTimestamp() == null) {
            drinkCntFav.setUpdatedAtTimestamp(Timestamp.now());
        }
        Map<String, Object> data = new HashMap<>();
        data.put("uuid", drinkCntFav.getUuid());
        data.put("drinkId", drinkCntFav.getDrinkId());
        data.put("count", drinkCntFav.getCount());
        data.put("createdAt", drinkCntFav.getCreatedAtTimestamp());
        data.put("updatedAt", drinkCntFav.getUpdatedAtTimestamp());
        return data;
    }

    private DrinkCntFav convertDocumentToDrinkCntFav(DocumentSnapshot doc) {
        DrinkCntFav drinkCntFav = new DrinkCntFav();
        drinkCntFav.setUuid(doc.getString("uuid"));
        drinkCntFav.setDrinkId(doc.getString("drinkId"));
        
        Long count = doc.getLong("count");
        drinkCntFav.setCount(count != null ? count.intValue() : 0);
        
        Timestamp createdAt = doc.getTimestamp("createdAt");
        if (createdAt != null) {
            drinkCntFav.setCreatedAtTimestamp(createdAt);
        }
        
        Timestamp updatedAt = doc.getTimestamp("updatedAt");
        if (updatedAt != null) {
            drinkCntFav.setUpdatedAtTimestamp(updatedAt);
        }
        
        return drinkCntFav;
    }

    public interface DrinkCntFavCallback {
        void onDrinkCntFavLoaded(DrinkCntFav drinkCntFav);
        void onError(Exception e);
    }

    public interface DrinkCntFavListCallback {
        void onDrinkCntFavListLoaded(List<DrinkCntFav> drinkCntFavs);
        void onError(Exception e);
    }

    public void addDrinkCntFav(DrinkCntFav drinkCntFav, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        if (drinkCntFav.getUuid() == null || drinkCntFav.getUuid().isEmpty()) {
            drinkCntFav.generateUUID();
        }
        Map<String, Object> data = convertDrinkCntFavToMap(drinkCntFav);
        drinkCntFavRef.document(drinkCntFav.getUuid())
                .set(data)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getDrinkCntFav(String uuid, DrinkCntFavCallback callback) {
        drinkCntFavRef.document(uuid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    callback.onDrinkCntFavLoaded(convertDocumentToDrinkCntFav(documentSnapshot));
                })
                .addOnFailureListener(callback::onError);
    }

    public void getDrinkCntFavByDrinkId(String drinkId, DrinkCntFavCallback callback) {
        drinkCntFavRef.whereEqualTo("drinkId", drinkId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                        DrinkCntFav drinkCntFav = convertDocumentToDrinkCntFav(doc);
                        callback.onDrinkCntFavLoaded(drinkCntFav);
                    } else {
                        callback.onDrinkCntFavLoaded(null);
                    }
                })
                .addOnFailureListener(callback::onError);
    }

    public void getAllDrinkCntFavs(DrinkCntFavListCallback callback) {
        drinkCntFavRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<DrinkCntFav> drinkCntFavs = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        DrinkCntFav drinkCntFav = convertDocumentToDrinkCntFav(doc);
                        if (drinkCntFav != null) {
                            drinkCntFavs.add(drinkCntFav);
                        }
                    }
                    callback.onDrinkCntFavListLoaded(drinkCntFavs);
                })
                .addOnFailureListener(callback::onError);
    }

    public void getTopDrinkCntFavourite(int limit, DrinkCntFavListCallback callback) {
        drinkCntFavRef.orderBy("count", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<DrinkCntFav> drinkCntFavs = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        DrinkCntFav drinkCntFav = convertDocumentToDrinkCntFav(doc);
                        if (drinkCntFav != null) {
                            drinkCntFavs.add(drinkCntFav);
                        }
                    }
                    callback.onDrinkCntFavListLoaded(drinkCntFavs);
                })
                .addOnFailureListener(callback::onError);
    }

    public void updateDrinkCntFav(DrinkCntFav updatedDrinkCntFav, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        Map<String, Object> data = convertDrinkCntFavToMap(updatedDrinkCntFav);
        drinkCntFavRef.document(updatedDrinkCntFav.getUuid())
                .set(data)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void incrementDrinkCntFav(String drinkId, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        drinkCntFavRef.whereEqualTo("drinkId", drinkId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Update existing document
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                        DrinkCntFav drinkCntFav = convertDocumentToDrinkCntFav(doc);
                        drinkCntFav.setCount(drinkCntFav.getCount() + 1);
                        updateDrinkCntFav(drinkCntFav, onSuccess, onFailure);
                    } else {
                        // Create new document
                        DrinkCntFav newDrinkCntFav = new DrinkCntFav(drinkId, 1);
                        addDrinkCntFav(newDrinkCntFav, onSuccess, onFailure);
                    }
                })
                .addOnFailureListener(onFailure);
    }

    public void decrementDrinkCntFav(String drinkId, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        drinkCntFavRef.whereEqualTo("drinkId", drinkId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                        DrinkCntFav drinkCntFav = convertDocumentToDrinkCntFav(doc);
                        int newCount = Math.max(0, drinkCntFav.getCount() - 1);
                        drinkCntFav.setCount(newCount);
                        if (newCount > 0) {
                            updateDrinkCntFav(drinkCntFav, onSuccess, onFailure);
                        } else {
                            deleteDrinkCntFav(drinkCntFav.getUuid(), onSuccess, onFailure);
                        }
                    }
                })
                .addOnFailureListener(onFailure);
    }

    public void deleteDrinkCntFav(String uuid, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        drinkCntFavRef.document(uuid)
                .delete()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }
}
