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
    private final DrinkCntFavDAO drinkCntFavDAO;

    public static final String ERROR_USER_NOT_AUTH = "User not authenticated";

    public FavoriteDAO() {
        db = FirebaseFirestore.getInstance();
        favoriteRef = db.collection("favorite");
        drinkCntFavDAO = new DrinkCntFavDAO();
    }

    private Map<String, Object> convertFavoriteToMap(Favorite favorite) {
        if (favorite.getUuid() == null || favorite.getDrinkId() == null || favorite.getUserId() == null) {
            throw new IllegalArgumentException("Favorite must have uuid, drinkId, and userId");
        }
        if (favorite.getCreatedAtTimestamp() == null) {
            favorite.setCreatedAtTimestamp(Timestamp.now());
        }
        if (favorite.getUpdatedAtTimestamp() == null) {
            favorite.setUpdatedAtTimestamp(Timestamp.now());
        }
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
        if (favorite.getUuid() == null || favorite.getUuid().isEmpty()) {
            favorite.generateUUID();
        }
        Map<String, Object> data = convertFavoriteToMap(favorite);
        favoriteRef.document(favorite.getUuid())
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Increment the favorite count for the drink
                        drinkCntFavDAO.incrementDrinkCntFav(favorite.getDrinkId(),
                                new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        onSuccess.onSuccess(aVoid);
                                    }
                                },
                                onFailure);
                    }
                })
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
        favoriteRef
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Favorite> favorites = new ArrayList<>();
                    Log.d("favourite", "Size querySnapshot: " + querySnapshot.size());
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Favorite favorite = convertDocumentToFavorite(doc);
                        if (favorite != null) {
                            favorites.add(favorite);
                            Log.d("favourite", "getFavoriteUserId: " + favorite);
                        }
                    }
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
        // First get the old favorite to get the old drinkId
        favoriteRef.document(updatedFavorite.getUuid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Favorite oldFavorite = convertDocumentToFavorite(documentSnapshot);
                        String oldDrinkId = oldFavorite.getDrinkId();
                        String newDrinkId = updatedFavorite.getDrinkId();

                        // If drinkId has changed, update both counts
                        if (!oldDrinkId.equals(newDrinkId)) {
                            // Decrement old drink count
                            drinkCntFavDAO.decrementDrinkCntFav(oldDrinkId,
                                    new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            // Increment new drink count
                                            drinkCntFavDAO.incrementDrinkCntFav(newDrinkId,
                                                    new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            // Update the favorite document
                                                            Map<String, Object> data = convertFavoriteToMap(updatedFavorite);
                                                            favoriteRef.document(updatedFavorite.getUuid())
                                                                    .set(data)
                                                                    .addOnSuccessListener(onSuccess)
                                                                    .addOnFailureListener(onFailure);
                                                        }
                                                    },
                                                    onFailure);
                                        }
                                    },
                                    onFailure);
                        } else {
                            // If drinkId hasn't changed, just update the document
                            Map<String, Object> data = convertFavoriteToMap(updatedFavorite);
                            favoriteRef.document(updatedFavorite.getUuid())
                                    .set(data)
                                    .addOnSuccessListener(onSuccess)
                                    .addOnFailureListener(onFailure);
                        }
                    }
                })
                .addOnFailureListener(onFailure);
    }

    public void deleteFavorite(String uuid, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        // First get the favorite to get the drinkId
        favoriteRef.document(uuid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Favorite favorite = convertDocumentToFavorite(documentSnapshot);
                        String drinkId = favorite.getDrinkId();

                        // Delete the favorite document
                        favoriteRef.document(uuid)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Decrement the favorite count for the drink
                                        drinkCntFavDAO.decrementDrinkCntFav(drinkId,
                                                new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        onSuccess.onSuccess(aVoid);
                                                    }
                                                },
                                                onFailure);
                                    }
                                })
                                .addOnFailureListener(onFailure);
                    }
                })
                .addOnFailureListener(onFailure);
    }
} 