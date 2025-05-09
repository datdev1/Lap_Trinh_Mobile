package com.b21dccn216.pocketcocktail.data.Dao;

import androidx.annotation.NonNull;

import com.b21dccn216.pocketcocktail.view.Main.Drink;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DrinksDao {

    private final DatabaseReference drinkRef;

    public static String ERROR_USER_NOT_AUTH = "User not authenticated";

    public DrinksDao() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        drinkRef = database.getReference("Drinks");

    }

    public interface DrinkCallback {
        void onSuccess(List<Drink> drinks);
        void onError(String errorMessage);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onError(String errorMessage);
    }

    // ✅ Get all drinks
    public void getAllDrinks(DrinkCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.onError(ERROR_USER_NOT_AUTH);
            return;
        }

        drinkRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Drink> drinks = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Drink drink = child.getValue(Drink.class);
                    if (drink != null) drinks.add(drink);
                }
                callback.onSuccess(drinks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }

    // ✅ Get all drinks
    // ✅ Get drinks that contain a specific ingredient ID



    // ✅ Add a new drink
    public void addDrink(Drink drink, SimpleCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.onError(ERROR_USER_NOT_AUTH);
            return;
        }

        drinkRef.child(String.valueOf(drink.id)).setValue(drink)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }


    // ✅ Update existing drink
    public void updateDrink(Drink drink, SimpleCallback callback) {
        addDrink(drink, callback); // reuse add logic (setValue replaces)
    }

    // ✅ Delete a drink
    public void deleteDrink(int drinkId, SimpleCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.onError(ERROR_USER_NOT_AUTH);
            return;
        }

        drinkRef.child(String.valueOf(drinkId)).removeValue()
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
}


/*
// Note for using DAO
DrinksDao drinksDao = new DrinksDao();

// Read
drinksDao.getAllDrinks(new DrinksDao.DrinkCallback() {
    @Override
    public void onSuccess(List<Drink> drinks) {
        // Use the drink list here
    }

    @Override
    public void onError(String errorMessage) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
    }
});

// Add
Drink newDrink = new Drink(10, 1, "Gin Tonic", "...", "desc", 4.9, "tip");
drinksDao.addDrink(newDrink, new DrinksDao.SimpleCallback() {
    @Override
    public void onSuccess() {
        Toast.makeText(context, "Added!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(String errorMessage) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
    }
});

 */

