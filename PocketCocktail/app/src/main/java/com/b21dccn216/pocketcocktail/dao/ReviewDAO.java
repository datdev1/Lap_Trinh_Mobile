package com.b21dccn216.pocketcocktail.dao;

import com.b21dccn216.pocketcocktail.model.Review;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {
    private final FirebaseFirestore db;
    private final CollectionReference reviewRef;

    public static final String ERROR_USER_NOT_AUTH = "User not authenticated";

    public ReviewDAO() {
        db = FirebaseFirestore.getInstance();
        reviewRef = db.collection("review");
    }

    public interface ReviewCallback {
        void onReviewLoaded(Review review);
        void onError(Exception e);
    }

    public interface ReviewListCallback {
        void onReviewListLoaded(List<Review> reviews);
        void onError(Exception e);
    }

    public void addReview(Review review, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        reviewRef.document(review.generateUUID())
                .set(review)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getReview(String reviewUuid, OnSuccessListener<DocumentSnapshot> onSuccess, OnFailureListener onFailure) {
        reviewRef.document(reviewUuid).get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getReview(Review review, OnSuccessListener<DocumentSnapshot> onSuccess, OnFailureListener onFailure) {
        reviewRef.document(review.getUuid()).get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getReviewsByDrinkId(String drinkId, OnSuccessListener<QuerySnapshot> onSuccess, OnFailureListener onFailure) {
        reviewRef.whereEqualTo("drinkId", drinkId)
                .get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getReviewsByUserId(String userId, OnSuccessListener<QuerySnapshot> onSuccess, OnFailureListener onFailure) {
        reviewRef.whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getAllReviews(OnSuccessListener<QuerySnapshot> onSuccess, OnFailureListener onFailure) {
        reviewRef.get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getAllReviews(ReviewListCallback callback) {
        reviewRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Review> reviews = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Review review = doc.toObject(Review.class);
                        if (review != null) {
                            reviews.add(review);
                        }
                    }
                    callback.onReviewListLoaded(reviews);
                })
                .addOnFailureListener(callback::onError);
    }

    public void updateReview(Review updatedReview, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        reviewRef.document(updatedReview.getUuid())
                .set(updatedReview)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void deleteReview(String uuid, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        reviewRef.document(uuid)
                .delete()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }
} 