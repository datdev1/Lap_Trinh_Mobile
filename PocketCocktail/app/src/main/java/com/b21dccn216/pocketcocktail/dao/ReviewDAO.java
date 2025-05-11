package com.b21dccn216.pocketcocktail.dao;

import com.b21dccn216.pocketcocktail.model.Review;
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

public class ReviewDAO {
    private final FirebaseFirestore db;
    private final CollectionReference reviewRef;

    public static final String ERROR_USER_NOT_AUTH = "User not authenticated";

    public ReviewDAO() {
        db = FirebaseFirestore.getInstance();
        reviewRef = db.collection("review");
    }

    private Map<String, Object> convertReviewToMap(Review review) {
        Map<String, Object> data = new HashMap<>();
        data.put("uuid", review.getUuid());
        data.put("drinkId", review.getDrinkId());
        data.put("userId", review.getUserId());
        data.put("rate", review.getRate());
        data.put("comment", review.getComment());
        data.put("createdAt", review.getCreatedAtTimestamp());
        data.put("updatedAt", review.getUpdatedAtTimestamp());
        return data;
    }

    private Review convertDocumentToReview(DocumentSnapshot doc) {
        Review review = new Review();
        review.setUuid(doc.getString("uuid"));
        review.setDrinkId(doc.getString("drinkId"));
        review.setUserId(doc.getString("userId"));
        review.setRate(doc.getDouble("rate"));
        review.setComment(doc.getString("comment"));
        
        Timestamp createdAt = doc.getTimestamp("createdAt");
        if (createdAt != null) {
            review.setCreatedAtTimestamp(createdAt);
        }
        
        Timestamp updatedAt = doc.getTimestamp("updatedAt");
        if (updatedAt != null) {
            review.setUpdatedAtTimestamp(updatedAt);
        }
        
        return review;
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
        review.generateUUID();
        Map<String, Object> data = convertReviewToMap(review);
        reviewRef.document(review.getUuid())
                .set(data)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getReview(String reviewUuid, ReviewCallback callback) {
        reviewRef.document(reviewUuid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Review review = convertDocumentToReview(documentSnapshot);
                    callback.onReviewLoaded(review);
                })
                .addOnFailureListener(callback::onError);
    }

    public void getReviewsByDrinkId(String drinkId, ReviewListCallback callback) {
        reviewRef.whereEqualTo("drinkId", drinkId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Review> reviews = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Review review = convertDocumentToReview(doc);
                        if (review != null) {
                            reviews.add(review);
                        }
                    }
                    callback.onReviewListLoaded(reviews);
                })
                .addOnFailureListener(callback::onError);
    }

    public void getReviewsByUserId(String userId, ReviewListCallback callback) {
        reviewRef.whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Review> reviews = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Review review = convertDocumentToReview(doc);
                        if (review != null) {
                            reviews.add(review);
                        }
                    }
                    callback.onReviewListLoaded(reviews);
                })
                .addOnFailureListener(callback::onError);
    }

//    public void getAllReviews(OnSuccessListener<QuerySnapshot> onSuccess, OnFailureListener onFailure) {
//        reviewRef.get()
//                .addOnSuccessListener(onSuccess)
//                .addOnFailureListener(onFailure);
//    }

    public void getAllReviews(ReviewListCallback callback) {
        reviewRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Review> reviews = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Review review = convertDocumentToReview(doc);
                        if (review != null) {
                            reviews.add(review);
                        }
                    }
                    callback.onReviewListLoaded(reviews);
                })
                .addOnFailureListener(callback::onError);
    }

    public void updateReview(Review updatedReview, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        Map<String, Object> data = convertReviewToMap(updatedReview);
        reviewRef.document(updatedReview.getUuid())
                .set(data)
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