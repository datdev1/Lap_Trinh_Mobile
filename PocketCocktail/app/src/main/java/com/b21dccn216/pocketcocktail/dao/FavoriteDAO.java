package com.b21dccn216.pocketcocktail.dao;

import android.util.Log;

import com.b21dccn216.pocketcocktail.model.Drink;
import com.b21dccn216.pocketcocktail.model.Favorite;
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

public class FavoriteDAO {
    private final FirebaseFirestore db;
    private final CollectionReference favoriteRef;

    public static final String ERROR_USER_NOT_AUTH = "User not authenticated";

    public FavoriteDAO() {
        db = FirebaseFirestore.getInstance();
        favoriteRef = db.collection("favorite");
    }

    private Map<String, Object> convertFavoriteToMap(Favorite favorite) {
        Map<String, Object> data = new HashMap<>();
        data.put("uuid", favorite.getUuid());
        data.put("drinkId", favorite.getDrinkId());
        data.put("userId", favorite.getUserId());
        data.put("createdAt", favorite.getCreatedAtTimestamp());
        data.put("updatedAt", favorite.getUpdatedAtTimestamp());
        return data;
    }

    private Favorite convertDocumentToFavorite(DocumentSnapshot doc) {
        Favorite favorite = new Favorite();
        favorite.setUuid(doc.getString("uuid"));
        favorite.setDrinkId(doc.getString("drinkId"));
        favorite.setUserId(doc.getString("userId"));
        
        Timestamp createdAt = doc.getTimestamp("createdAt");
        if (createdAt != null) {
            favorite.setCreatedAtTimestamp(createdAt);
        }
        
        Timestamp updatedAt = doc.getTimestamp("updatedAt");
        if (updatedAt != null) {
            favorite.setUpdatedAtTimestamp(updatedAt);
        }
        
        return favorite;
    }

    public interface FavoriteCallback {
        void onFavoriteLoaded(Favorite favorite);
        void onError(Exception e);
    }

    public interface FavoriteListCallback {
        void onFavoriteListLoaded(List<Favorite> favorites);
        void onError(Exception e);
    }

    public void addFavorite(Favorite favorite, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        favorite.generateUUID();
        Map<String, Object> data = convertFavoriteToMap(favorite);
        favoriteRef.document(favorite.getUuid())
                .set(data)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

//    public void getFavorite(String favoriteUuid, OnSuccessListener<DocumentSnapshot> onSuccess, OnFailureListener onFailure) {
//        favoriteRef.document(favoriteUuid).get()
//                .addOnSuccessListener(onSuccess)
//                .addOnFailureListener(onFailure);
//    }
//
//    public void getFavorite(Favorite favorite, OnSuccessListener<DocumentSnapshot> onSuccess, OnFailureListener onFailure) {
//        favoriteRef.document(favorite.getUuid()).get()
//                .addOnSuccessListener(onSuccess)
//                .addOnFailureListener(onFailure);
//    }

    public void getFavorite(Favorite favorite, FavoriteCallback callback) {
        favoriteRef.document(favorite.getUuid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    callback.onFavoriteLoaded(convertDocumentToFavorite(documentSnapshot));
                })
                .addOnFailureListener(callback::onError);
    }
//    public void getFavoritesByDrinkId(String drinkId, OnSuccessListener<QuerySnapshot> onSuccess, OnFailureListener onFailure) {
//        favoriteRef.whereEqualTo("drinkId", drinkId)
//                .get()
//                .addOnSuccessListener(onSuccess)
//                .addOnFailureListener(onFailure);
//    }

    public void getFavoriteDrinkId(String drinkId, FavoriteListCallback callback) {
        favoriteRef.whereEqualTo("drinkId", drinkId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Favorite> favorites = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Favorite favorite = convertDocumentToFavorite(doc);
                        if (favorite != null) {
                            favorites.add(favorite);
                        }
                    }
                    Log.e("load Drink", "getAllDrinks: " + favorites);
                    callback.onFavoriteListLoaded(favorites);
                })
                .addOnFailureListener(callback::onError);
    }

    public void getFavoritesByUserId(String userId, FavoriteListCallback callback) {
        favoriteRef.whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Favorite> favorites = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Favorite favorite = convertDocumentToFavorite(doc);
                        if (favorite != null) {
                            favorites.add(favorite);
                        }
                    }
                    callback.onFavoriteListLoaded(favorites);
                })
                .addOnFailureListener(callback::onError);
    }

//    public void getFavoritesByUserId(String userId, OnSuccessListener<QuerySnapshot> onSuccess, OnFailureListener onFailure) {
//        favoriteRef.whereEqualTo("userId", userId)
//                .get()
//                .addOnSuccessListener(onSuccess)
//                .addOnFailureListener(onFailure);
//    }

    public void getFavoriteUserId(String userId, FavoriteListCallback callback) {
        favoriteRef.whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Favorite> favorites = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Favorite favorite = convertDocumentToFavorite(doc);
                        if (favorite != null) {
                            favorites.add(favorite);
                        }
                    }
                    Log.e("load Drink", "getAllDrinks: " + favorites);
                    callback.onFavoriteListLoaded(favorites);
                })
                .addOnFailureListener(callback::onError);
    }

//    public void getAllFavorites(OnSuccessListener<QuerySnapshot> onSuccess, OnFailureListener onFailure) {
//        favoriteRef.get()
//                .addOnSuccessListener(onSuccess)
//                .addOnFailureListener(onFailure);
//    }

    public void getAllFavorites(FavoriteListCallback callback) {
        favoriteRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Favorite> favorites = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Favorite favorite = convertDocumentToFavorite(doc);
                        if (favorite != null) {
                            favorites.add(favorite);
                        }
                    }
                    callback.onFavoriteListLoaded(favorites);
                })
                .addOnFailureListener(callback::onError);
    }

    public void updateFavorite(Favorite updatedFavorite, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        Map<String, Object> data = convertFavoriteToMap(updatedFavorite);
        favoriteRef.document(updatedFavorite.getUuid())
                .set(data)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void deleteFavorite(String uuid, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        favoriteRef.document(uuid)
                .delete()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }
} 