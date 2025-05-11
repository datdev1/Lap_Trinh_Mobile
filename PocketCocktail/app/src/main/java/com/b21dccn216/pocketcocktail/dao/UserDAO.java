package com.b21dccn216.pocketcocktail.dao;

import android.content.Context;
import android.net.Uri;

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

public class UserDAO {
    private final FirebaseFirestore db;
    private final CollectionReference userRef;
    private final ImageDAO imageDAO;

    public static final String ERROR_USER_NOT_AUTH = "User not authenticated";

    public UserDAO() {
        db = FirebaseFirestore.getInstance();
        userRef = db.collection("user");
        imageDAO = new ImageDAO();
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
        Map<String, Object> data = convertUserToMap(user);
        userRef.document(user.generateUUID())
                .set(data)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void addUserWithImage(Context context, User user, Uri imageUri,
                                OnSuccessListener<Void> onSuccess,
                                OnFailureListener onFailure) {
        String title = ImageDAO.ImageDaoFolderForAvatar + "_" + user.getName() + "_" + user.getUuid();
        new ImageDAO().uploadImageToImgur(context, imageUri, title, new ImageDAO.UploadCallback() {
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

    public void getUser(User user, OnSuccessListener<DocumentSnapshot> onSuccess, OnFailureListener onFailure) {
        userRef.document(user.getUuid()).get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
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

    public void getUserByUuidAuthen(String uuidAuthen, OnSuccessListener<QuerySnapshot> onSuccess, OnFailureListener onFailure) {
        userRef.whereEqualTo("saveUuidFromAuthen", uuidAuthen)
                .get()
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
        if (newImageUri != null) {
            String title = ImageDAO.ImageDaoFolderForAvatar + "_" + updatedUser.getName() + "_" + updatedUser.getUuid();
            new ImageDAO().uploadImageToImgur(context, newImageUri, title, new ImageDAO.UploadCallback() {
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
        } else {
            updateUser(updatedUser, onSuccess, onFailure);
        }
    }

    public void deleteUser(String uuid, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        userRef.document(uuid)
                .delete()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }
} 