package com.b21dccn216.shop.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.b21dccn216.shop.Model.User;
import com.b21dccn216.shop.Model.UserFirebase;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class UserFirebaseRepository {
    private final DatabaseReference usersRef;

    public UserFirebaseRepository() {
        usersRef = FirebaseDatabase.getInstance().getReference("users");
    }

    public void addUser(String name, String dob, String email, String gender) {
        String id = usersRef.push().getKey();
        UserFirebase user = new UserFirebase(id, name, dob, gender, email);
        if (id != null) {
            usersRef.child(id).setValue(user);
        }
    }

    public LiveData<List<User>> getUsers() {
        MutableLiveData<List<User>> usersLiveData = new MutableLiveData<>();
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<User> users = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    User user = childSnapshot.getValue(User.class);
                    if (user != null) {
                        users.add(user);
                    }
                }
                usersLiveData.setValue(users);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
        return usersLiveData;
    }

    public LiveData<User> getUserByEmail(String email) {
        MutableLiveData<User> userLiveData = new MutableLiveData<>();

        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        User user = childSnapshot.getValue(User.class);
                        userLiveData.setValue(user);
                        break; // Return the first match
                    }
                } else {
                    userLiveData.setValue(null); // No user found
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                userLiveData.setValue(null);
            }
        });

        return userLiveData;
    }



}
