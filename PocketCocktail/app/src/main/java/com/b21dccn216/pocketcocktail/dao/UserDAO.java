package com.b21dccn216.pocketcocktail.dao;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.b21dccn216.pocketcocktail.model.Drink;
import com.b21dccn216.pocketcocktail.model.Favorite;
import com.b21dccn216.pocketcocktail.model.Review;
import com.b21dccn216.pocketcocktail.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class UserDAO {
    private final FirebaseFirestore db;
    private final CollectionReference userRef;
    private final ImageDAO imageDAO;
    private static final String IMGUR_REFRESH_TOKEN = "2396b095b3402713d6dd7895146265c06f22fc71"; // Add your refresh token here
    private boolean isAuthenticated = false;
    private final CountDownLatch authLatch = new CountDownLatch(1);

    public static final String ERROR_USER_NOT_AUTH = "User not authenticated";

    public UserDAO() {
        db = FirebaseFirestore.getInstance();
        userRef = db.collection("user");
        imageDAO = new ImageDAO();
        authenticateImgur();
    }

    private void authenticateImgur() {
        imageDAO.authenticate(IMGUR_REFRESH_TOKEN, new ImageDAO.AuthCallback() {
            @Override
            public void onSuccess() {
                Log.d("UserDAO", "Imgur authentication successful");
                isAuthenticated = true;
                authLatch.countDown();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("UserDAO", "Imgur authentication failed: " + e.getMessage());
                isAuthenticated = false;
                authLatch.countDown();
            }
        });
    }

    private void waitForAuthentication() {
        try {
            authLatch.await();
        } catch (InterruptedException e) {
            Log.e("UserDAO", "Authentication wait interrupted", e);
        }
    }

    private Map<String, Object> convertUserToMap(User user) {
        Map<String, Object> data = new HashMap<>();
        data.put("uuid", user.getUuid());
        data.put("saveUuidFromAuthen", user.getSaveUuidFromAuthen());
        data.put("name", user.getName());
        data.put("email", user.getEmail());
        data.put("role", user.getRole());
        data.put("password", user.getPassword());
        data.put("image", user.getImage());
        data.put("createdAt", user.getCreatedAtTimestamp());
        data.put("updatedAt", user.getUpdatedAtTimestamp());
        return data;
    }

    private User convertDocumentToUser(DocumentSnapshot doc) {
        User user = new User();
        user.setUuid(doc.getString("uuid"));
        user.setSaveUuidFromAuthen(doc.getString("saveUuidFromAuthen"));
        user.setName(doc.getString("name"));
        user.setEmail(doc.getString("email"));
        user.setRole(doc.getString("role"));
        user.setPassword(doc.getString("password"));
        user.setImage(doc.getString("image"));
        
        Timestamp createdAt = doc.getTimestamp("createdAt");
        if (createdAt != null) {
            user.setCreatedAtTimestamp(createdAt);
        }
        
        Timestamp updatedAt = doc.getTimestamp("updatedAt");
        if (updatedAt != null) {
            user.setUpdatedAtTimestamp(updatedAt);
        }
        
        return user;
    }

    public interface UserCallback {
        void onUserLoaded(User user);
        void onError(Exception e);
    }

    public interface UserListCallback {
        void onUserListLoaded(List<User> users);
        void onError(Exception e);
    }

    public void addUser(User user, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        user.generateUUID();
        Map<String, Object> data = convertUserToMap(user);
        userRef.document(user.getUuid())
                .set(data)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void addUserWithImage(Context context, User user, Uri imageUri,
                                OnSuccessListener<Void> onSuccess,
                                OnFailureListener onFailure) {
        //waitForAuthentication();
        if (!isAuthenticated) {
            onFailure.onFailure(new Exception("Imgur authentication failed"));
            return;
        }

        String title = ImageDAO.ImageDaoFolderForAvatar + "_" + user.getName() + "_" + user.getUuid();
        imageDAO.uploadImageToImgur(context, imageUri, title, new ImageDAO.UploadCallback() {
            @Override
            public void onSuccess(String imageUrl) {
                user.generateUUID();
                user.setImage(imageUrl);

                userRef.document(user.getUuid())
                        .set(user)
                        .addOnSuccessListener(onSuccess)
                        .addOnFailureListener(onFailure);
            }

            @Override
            public void onFailure(Exception e) {

                onFailure.onFailure(e);
            }
        });
    }

    public void getUser(String userUuid, UserCallback callback) {
        userRef.document(userUuid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    User user = convertDocumentToUser(documentSnapshot);
                    callback.onUserLoaded(user);
                })
                .addOnFailureListener(callback::onError);
    }


    public void getUserByEmail(String email, UserCallback callback) {
        userRef.whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                        User user = convertDocumentToUser(doc);
                        callback.onUserLoaded(user);
                    } else {
                        callback.onUserLoaded(null);
                    }
                })
                .addOnFailureListener(callback::onError);
    }

//    public void getUserByUuidAuthen(String uuidAuthen, OnSuccessListener<QuerySnapshot> onSuccess, OnFailureListener onFailure) {
//        userRef.whereEqualTo("saveUuidFromAuthen", uuidAuthen)
//                .get()
//                .addOnSuccessListener(onSuccess)
//                .addOnFailureListener(onFailure);
//    }

    public void getUserByUuidAuthen(String uuidAuthen, UserListCallback callback) {
        userRef.whereEqualTo("saveUuidFromAuthen", uuidAuthen)
                .get()
                .addOnSuccessListener(
                        querySnapshot -> {
                            List<User> users = new ArrayList<>();
                            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                User user = convertDocumentToUser(doc);
                                if (user != null)
                                    users.add(user);

                            }
                            callback.onUserListLoaded(users);
                        }
                )
                .addOnFailureListener(callback::onError);
    }

    public void getUserByUuidAuthen(String uuidAuthen, UserCallback callback) {
        userRef.whereEqualTo("saveUuidFromAuthen", uuidAuthen)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                        User user = doc.toObject(User.class);
                        if (user != null) {
                            callback.onUserLoaded(user);
                            return;
                        }
                    }
                    callback.onError(new Exception(ERROR_USER_NOT_AUTH));
                })
                .addOnFailureListener(callback::onError);
    }

    public void getAllUsers(OnSuccessListener<QuerySnapshot> onSuccess, OnFailureListener onFailure) {
        userRef.get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getAllUsers(UserListCallback callback) {
        userRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<User> users = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        User user = convertDocumentToUser(doc);
                        if (user != null) {
                            users.add(user);
                        }
                    }
                    callback.onUserListLoaded(users);
                })
                .addOnFailureListener(callback::onError);
    }

    public void updateUser(User updatedUser, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        Map<String, Object> data = convertUserToMap(updatedUser);
        userRef.document(updatedUser.getUuid())
                .set(data)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void updateUserWithImage(Context context, User updatedUser, Uri newImageUri,
                                  OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        //waitForAuthentication();
        if (!isAuthenticated) {
            onFailure.onFailure(new Exception("Imgur authentication failed"));
            return;
        }

        if (newImageUri != null) {
            // First delete the old image if it exists
            if (updatedUser.getImage() != null && !updatedUser.getImage().isEmpty()) {
                imageDAO.deleteImageFromImgur(updatedUser.getImage(), new ImageDAO.DeleteCallback() {
                    @Override
                    public void onSuccess() {
                        // After deleting old image, upload new one
                        uploadNewImage(context, updatedUser, newImageUri, onSuccess, onFailure);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // Even if delete fails, continue with new image upload
                        Log.e("UserDAO", "Failed to delete old image for user " + updatedUser.getUuid() + ": " + e.getMessage());
                        uploadNewImage(context, updatedUser, newImageUri, onSuccess, onFailure);
                    }
                });
            } else {
                // If no old image, just upload new one
                uploadNewImage(context, updatedUser, newImageUri, onSuccess, onFailure);
            }
        } else {
            updateUser(updatedUser, onSuccess, onFailure);
        }
    }

    private void uploadNewImage(Context context, User updatedUser, Uri newImageUri,
                              OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        String title = ImageDAO.ImageDaoFolderForAvatar + "_" + updatedUser.getName() + "_" + updatedUser.getUuid();
        imageDAO.uploadImageToImgur(context, newImageUri, title, new ImageDAO.UploadCallback() {
            @Override
            public void onSuccess(String imageUrl) {
                updatedUser.setImage(imageUrl);
                updateUser(updatedUser, onSuccess, onFailure);
            }

            @Override
            public void onFailure(Exception e) {
                onFailure.onFailure(e);
            }
        });
    }

    private void checkUserDependencies(String userId, OnSuccessListener<Boolean> onSuccess, OnFailureListener onFailure) {
        // Check for drinks created by user
        DrinkDAO drinkDAO = new DrinkDAO();
        drinkDAO.getDrinksByUserId(userId, new DrinkDAO.DrinkListCallback() {
            @Override
            public void onDrinkListLoaded(List<Drink> drinks) {
                if (!drinks.isEmpty()) {
                    onFailure.onFailure(new Exception("Cannot delete user: User has created drinks"));
                    return;
                }
                
                // Check for reviews by user
                ReviewDAO reviewDAO = new ReviewDAO();
                reviewDAO.getReviewsByUserId(userId, new ReviewDAO.ReviewListCallback() {
                    @Override
                    public void onReviewListLoaded(List<Review> reviews) {
                        if (!reviews.isEmpty()) {
                            onFailure.onFailure(new Exception("Cannot delete user: User has reviews"));
                            return;
                        }
                        
                        // Check for favorites by user
                        FavoriteDAO favoriteDAO = new FavoriteDAO();
                        favoriteDAO.getFavoritesByUserId(userId, new FavoriteDAO.FavoriteListCallback() {
                            @Override
                            public void onFavoriteListLoaded(List<Favorite> favorites) {
                                if (!favorites.isEmpty()) {
                                    onFailure.onFailure(new Exception("Cannot delete user: User has favorites"));
                                    return;
                                }
                                onSuccess.onSuccess(true);
                            }

                            @Override
                            public void onError(Exception e) {
                                onFailure.onFailure(e);
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        onFailure.onFailure(e);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                onFailure.onFailure(e);
            }
        });
    }

    public void deleteUser(String uuid, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        //waitForAuthentication();
        if (!isAuthenticated) {
            onFailure.onFailure(new Exception("Imgur authentication failed"));
            return;
        }

        // First check dependencies
        checkUserDependencies(uuid, 
            success -> {
                // If no dependencies, proceed with deletion
                // First get the user to get their image URL
                userRef.document(uuid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        User user = convertDocumentToUser(documentSnapshot);
                        if (user != null && user.getImage() != null && !user.getImage().isEmpty()) {
                            // Delete the image first
                            imageDAO.deleteImageFromImgur(user.getImage(), new ImageDAO.DeleteCallback() {
                                @Override
                                public void onSuccess() {
                                    // After image is deleted, delete the user document
                                    deleteUserDocument(uuid, onSuccess, onFailure);
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    // Even if image deletion fails, continue with user deletion
                                    Log.e("UserDAO", "Failed to delete image for user " + uuid + ": " + e.getMessage());
                                    deleteUserDocument(uuid, onSuccess, onFailure);
                                }
                            });
                        } else {
                            // If no image, just delete the user
                            deleteUserDocument(uuid, onSuccess, onFailure);
                        }
                    })
                    .addOnFailureListener(onFailure);
            },
            onFailure
        );
    }

    private void deleteUserDocument(String uuid, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        userRef.document(uuid)
                .delete()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }
} 