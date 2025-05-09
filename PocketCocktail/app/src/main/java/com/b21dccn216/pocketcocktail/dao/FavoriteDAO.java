package com.b21dccn216.pocketcocktail.dao;

import com.b21dccn216.pocketcocktail.model.Favorite;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FavoriteDAO {
    private final FirebaseFirestore db;
    private final CollectionReference favoriteRef;

    public static final String ERROR_USER_NOT_AUTH = "User not authenticated";

    public FavoriteDAO() {
        db = FirebaseFirestore.getInstance();
        favoriteRef = db.collection("favorite");
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
        favoriteRef.document(favorite.generateUUID())
                .set(favorite)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getFavorite(String favoriteUuid, OnSuccessListener<DocumentSnapshot> onSuccess, OnFailureListener onFailure) {
        favoriteRef.document(favoriteUuid).get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getFavorite(Favorite favorite, OnSuccessListener<DocumentSnapshot> onSuccess, OnFailureListener onFailure) {
        favoriteRef.document(favorite.getUuid()).get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getFavoritesByDrinkId(String drinkId, OnSuccessListener<QuerySnapshot> onSuccess, OnFailureListener onFailure) {
        favoriteRef.whereEqualTo("drinkId", drinkId)
                .get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getFavoritesByUserId(String userId, OnSuccessListener<QuerySnapshot> onSuccess, OnFailureListener onFailure) {
        favoriteRef.whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getAllFavorites(OnSuccessListener<QuerySnapshot> onSuccess, OnFailureListener onFailure) {
        favoriteRef.get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getAllFavorites(FavoriteListCallback callback) {
        favoriteRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Favorite> favorites = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Favorite favorite = doc.toObject(Favorite.class);
                        if (favorite != null) {
                            favorites.add(favorite);
                        }
                    }
                    callback.onFavoriteListLoaded(favorites);
                })
                .addOnFailureListener(callback::onError);
    }

    public void updateFavorite(Favorite updatedFavorite, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        favoriteRef.document(updatedFavorite.getUuid())
                .set(updatedFavorite)
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