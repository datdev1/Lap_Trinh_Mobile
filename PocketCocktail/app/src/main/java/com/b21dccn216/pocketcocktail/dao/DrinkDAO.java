package com.b21dccn216.pocketcocktail.dao;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

import com.b21dccn216.pocketcocktail.model.Category;
import com.b21dccn216.pocketcocktail.model.Drink;
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
import java.util.Random;

public class DrinkDAO {
    private static final String COLLECTION_NAME = "drink";
    private final FirebaseFirestore db;
    private final CollectionReference drinkRef;

    private final ImageDAO imageDAO;
    public static final String ERROR_USER_NOT_AUTH = "User not authenticated";

    public DrinkDAO() {
        db = FirebaseFirestore.getInstance();
        drinkRef = db.collection(COLLECTION_NAME);
        Log.d("DrinkDAO", "Initialized with collection: " + COLLECTION_NAME);
        imageDAO = new ImageDAO();
    }

    private Map<String, Object> convertDrinkToMap(Drink drink) {
        Map<String, Object> data = new HashMap<>();
        data.put("uuid", drink.getUuid());
        data.put("name", drink.getName());
        data.put("userId", drink.getUserId());
        data.put("image", drink.getImage());
        data.put("categoryId", drink.getCategoryId());
        data.put("instruction", drink.getInstruction());
        data.put("description", drink.getDescription());
        data.put("rate", drink.getRate());
        data.put("createdAt", drink.getCreatedAtTimestamp());
        data.put("updatedAt", drink.getUpdatedAtTimestamp());
        return data;
    }

    private Drink convertDocumentToDrink(DocumentSnapshot doc) {
        Drink drink = new Drink();
        drink.setUuid(doc.getString("uuid"));
        drink.setName(doc.getString("name"));
        drink.setUserId(doc.getString("userId"));
        drink.setImage(doc.getString("image"));
        drink.setCategoryId(doc.getString("categoryId"));
        drink.setInstruction(doc.getString("instruction"));
        drink.setDescription(doc.getString("description"));
        drink.setRate(doc.getDouble("rate"));
        
        Timestamp createdAt = doc.getTimestamp("createdAt");
        if (createdAt != null) {
            drink.setCreatedAtTimestamp(createdAt);
        }
        
        Timestamp updatedAt = doc.getTimestamp("updatedAt");
        if (updatedAt != null) {
            drink.setUpdatedAtTimestamp(updatedAt);
        }
        
        return drink;
    }

    public interface DrinkCallback {
        void onDrinkLoaded(Drink drink);
        void onError(Exception e);
    }

    public interface DrinkListCallback {
        void onDrinkListLoaded(List<Drink> drinks);
        void onError(Exception e);
    }

    public interface DrinkListWithLastDocCallback {
        void onDrinkListLoaded(List<Drink> drinks, DocumentSnapshot lastVisible);
        void onError(Exception e);
    }

    public void addDrink(Drink drink, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        Map<String, Object> data = convertDrinkToMap(drink);
        drinkRef.document(drink.generateUUID())
                .set(data)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void addDrinkWithImage(Context context, Drink drink, Uri imageUri,
                                  OnSuccessListener<Void> onSuccess,
                                  OnFailureListener onFailure) {
        String title = ImageDAO.ImageDaoFolderForDrink + "_" + drink.getName() + "_" + drink.getUuid();
        new ImageDAO().uploadImageToImgur(context, imageUri, title, new ImageDAO.UploadCallback() {
            @Override
            public void onSuccess(String imageUrl) {
                drink.generateUUID();
                drink.setImage(imageUrl);
                Map<String, Object> data = convertDrinkToMap(drink);

                drinkRef.document(drink.getUuid())
                        .set(data)
                        .addOnSuccessListener(onSuccess)
                        .addOnFailureListener(onFailure);
            }

            @Override
            public void onFailure(Exception e) {
                onFailure.onFailure(e);
            }
        });
    }

    public void updateDrink(Drink updatedDrink, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        Map<String, Object> data = convertDrinkToMap(updatedDrink);
        drinkRef.document(updatedDrink.getUuid())
                .set(data)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void updateDrinkWithImage(Context context, Drink updatedDrink, @Nullable Uri newImageUri,
                                     OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        if (newImageUri != null) {
            String title = ImageDAO.ImageDaoFolderForDrink + "_" + updatedDrink.getName() + "_" + updatedDrink.getUuid();
            new ImageDAO().uploadImageToImgur(context, newImageUri, title, new ImageDAO.UploadCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    updatedDrink.setImage(imageUrl);
                    Map<String, Object> data = convertDrinkToMap(updatedDrink);

                    drinkRef.document(updatedDrink.getUuid())
                            .set(data)
                            .addOnSuccessListener(onSuccess)
                            .addOnFailureListener(onFailure);
                }

                @Override
                public void onFailure(Exception e) {
                    onFailure.onFailure(e);
                }
            });
        } else {
            updateDrink(updatedDrink, onSuccess, onFailure);
        }
    }

    public void deleteDrink(String uuid, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        drinkRef.document(uuid)
                .delete()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

//    public void getDrink(String drinkUuid, OnSuccessListener<DocumentSnapshot> onSuccess, OnFailureListener onFailure) {
//        drinkRef.document(drinkUuid).get()
//                .addOnSuccessListener(onSuccess)
//                .addOnFailureListener(onFailure);
//    }
//
//    public void getDrink(Drink drink, OnSuccessListener<DocumentSnapshot> onSuccess, OnFailureListener onFailure) {
//        drinkRef.document(drink.getUuid()).get()
//                .addOnSuccessListener(onSuccess)
//                .addOnFailureListener(onFailure);
//    }
    public void getDrink(String drinkUuid, DrinkDAO.DrinkCallback callback) {
        drinkRef.document(drinkUuid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Drink drink = convertDocumentToDrink(documentSnapshot);
                    callback.onDrinkLoaded(drink);
                })
                .addOnFailureListener(callback::onError);
    }
//    public void getDrinksByCategoryId(String categoryId, OnSuccessListener<QuerySnapshot> onSuccess, OnFailureListener onFailure) {
//        drinkRef.whereEqualTo("categoryId", categoryId)
//                .get()
//                .addOnSuccessListener(onSuccess)
//                .addOnFailureListener(onFailure);
//    }

    public void getDrinksByCategoryId(String categoryId, DrinkListCallback callback) {
        drinkRef.whereEqualTo("categoryId", categoryId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Drink> drinks = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Drink drink = convertDocumentToDrink(doc);
                        if (drink != null) {
                            drinks.add(drink);
                        }
                    }
                    Log.e("load Drink", "getAllDrinks: " + drinks);
                    callback.onDrinkListLoaded(drinks);
                })
                .addOnFailureListener(callback::onError);
    }


    public void getDrinksByCategoryIdWithLimit(String categoryId, int limit, DrinkListCallback callback) {
        drinkRef.whereEqualTo("categoryId", categoryId)
                .limit(limit)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Drink> drinks = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Drink drink = convertDocumentToDrink(doc);
                        if (drink != null) {
                            drinks.add(drink);
                        }
                    }
                    Log.e("load Drink", "getAllDrinks: " + drinks);
                    callback.onDrinkListLoaded(drinks);
                })
                .addOnFailureListener(callback::onError);
    }

//    public void getAllDrinks(OnSuccessListener<QuerySnapshot> onSuccess, OnFailureListener onFailure) {
//        drinkRef.get()
//                .addOnSuccessListener(onSuccess)
//                .addOnFailureListener(onFailure);
//    }

    public void getAllDrinks(DrinkListCallback callback) {
        drinkRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Drink> drinks = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Drink drink = convertDocumentToDrink(doc);
                        if (drink != null) {
                            drinks.add(drink);
                        }
                    }
                    Log.e("load Drink", "getAllDrinks: " + drinks);
                    callback.onDrinkListLoaded(drinks);
                })
                .addOnFailureListener(callback::onError);
    }



    public void getFeatureDrink(DrinkCallback callback) {
        drinkRef.get()
                .addOnSuccessListener(snapShot -> {
                    if (snapShot.isEmpty()) {
                        callback.onError(new Exception("Drink not found"));
                        return;
                    }
                    int index = new Random().nextInt(snapShot.size());
                    Drink drink = convertDocumentToDrink(snapShot.getDocuments().get(index));
                    callback.onDrinkLoaded(drink);
                })
                .addOnFailureListener(callback::onError);
    }

    public void getDrinksSortAndLimit(DRINK_FIELD sortTag, Query.Direction sortOrder, int limit,
                                      DrinkListCallback callback) {
        drinkRef
                .orderBy(sortTag.getValue(), sortOrder)
                .limit(limit)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Drink> drinkList = new ArrayList<>();
                    for (DocumentSnapshot drinkSnapshot : queryDocumentSnapshots.getDocuments()) {
                        Drink drink = convertDocumentToDrink(drinkSnapshot);
                        drinkList.add(drink);
                    }
                    callback.onDrinkListLoaded(drinkList);
                })
                .addOnFailureListener(callback::onError);
    }

    public void getAllDrinksWithLimitAndSort(DRINK_FIELD sortField, Query.Direction sortOrder, int limit,
                                           @Nullable DocumentSnapshot startAfter,
                                           DrinkListWithLastDocCallback callback) {
        Log.d("DrinkDAO", "Getting all drinks with sort field: " + sortField.getValue() + ", order: " + sortOrder);
        
        Query query;
        query = drinkRef
                .orderBy(sortField.getValue(), sortOrder)
                .limit(limit);


        if (startAfter != null) {
            query = query.startAfter(startAfter);
        }

        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Drink> drinkList = new ArrayList<>();
                    DocumentSnapshot lastVisible = null;

                    Log.d("DrinkDAO", "Query returned " + queryDocumentSnapshots.size() + " documents");

                    for (DocumentSnapshot drinkSnapshot : queryDocumentSnapshots.getDocuments()) {
                        Log.d("DrinkDAO", "Document data: " + drinkSnapshot.getData());
                        Drink drink = convertDocumentToDrink(drinkSnapshot);
                        if (drink != null) {
                            drinkList.add(drink);
                            lastVisible = drinkSnapshot;
                            Log.d("DrinkDAO", "Added drink: " + drink.getName());
                        } else {
                            Log.e("DrinkDAO", "Failed to convert document to Drink object");
                        }
                    }
                    
                    Log.d("DrinkDAO", "Final list size: " + drinkList.size());
                    callback.onDrinkListLoaded(drinkList, lastVisible);
                })
                .addOnFailureListener(e -> {
                    Log.e("DrinkDAO", "Error executing query", e);
                    callback.onError(e);
                });
    }


    public void searchDrinksWithSort(String query, int limit, DocumentSnapshot startAfter, 
                                   DRINK_FIELD sortField, Query.Direction sortOrder,
                                   DrinkListWithLastDocCallback callback) {
        String searchQuery = query.toLowerCase();
        Log.d("DrinkDAO", "Searching with query: " + searchQuery);
        
        drinkRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Drink> allDrinks = new ArrayList<>();
                    List<Drink> filteredDrinks = new ArrayList<>();
                    DocumentSnapshot lastVisible = null;

                    Log.d("DrinkDAO", "Total documents: " + queryDocumentSnapshots.size());

                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Drink drink = convertDocumentToDrink(document);
                        if (drink != null) {
                            allDrinks.add(drink);
                        }
                    }

                    if (searchQuery.isEmpty()) {
                        filteredDrinks.addAll(allDrinks);
                    } else {
                        for (Drink drink : allDrinks) {
                            if (matchesSearchQuery(drink, searchQuery)) {
                                filteredDrinks.add(drink);
                            }
                        }
                    }

                    sortDrinks(filteredDrinks, sortField, sortOrder);

                    int endIndex = Math.min(limit, filteredDrinks.size());
                    List<Drink> paginatedDrinks = filteredDrinks.subList(0, endIndex);

                    Log.d("DrinkDAO", "Filtered list size: " + filteredDrinks.size());
                    Log.d("DrinkDAO", "Paginated list size: " + paginatedDrinks.size());

                    callback.onDrinkListLoaded(paginatedDrinks, lastVisible);
                })
                .addOnFailureListener(e -> {
                    Log.e("DrinkDAO", "Error searching drinks", e);
                    callback.onError(e);
                });
    }

    private void sortDrinks(List<Drink> drinks, DRINK_FIELD sortField, Query.Direction sortOrder) {
        drinks.sort((d1, d2) -> {
            int comparison = 0;
            switch (sortField) {
                case NAME:
                    comparison = d1.getName().compareTo(d2.getName());
                    break;
                case DESCRIPTION:
                    comparison = d1.getDescription().compareTo(d2.getDescription());
                    break;
                case RATE:
                    comparison = Double.compare(d1.getRate(), d2.getRate());
                    break;
                case CREATED_AT:
                    comparison = d1.getCreatedAt().compareTo(d2.getCreatedAt());
                    break;
                case UPDATED_AT:
                    comparison = d1.getUpdatedAt().compareTo(d2.getUpdatedAt());
                    break;
                case CATEGORY_ID:
                    comparison = d1.getCategoryId().compareTo(d2.getCategoryId());
                    break;
                case USER_ID:
                    comparison = d1.getUserId().compareTo(d2.getUserId());
                    break;
            }
            return sortOrder == Query.Direction.ASCENDING ? comparison : -comparison;
        });
    }

    private boolean matchesSearchQuery(Drink drink, String searchQuery) {
        if (searchQuery.isEmpty()) {
            return true;
        }

        boolean matches = (drink.getUuid() != null && drink.getUuid().toLowerCase().contains(searchQuery)) ||
                (drink.getName() != null && drink.getName().toLowerCase().contains(searchQuery)) ||
                (drink.getDescription() != null && drink.getDescription().toLowerCase().contains(searchQuery)) ||
                (drink.getInstruction() != null && drink.getInstruction().toLowerCase().contains(searchQuery)) ||
                (drink.getCategoryId() != null && drink.getCategoryId().toLowerCase().contains(searchQuery)) ||
                (drink.getUserId() != null && drink.getUserId().toLowerCase().contains(searchQuery));

        if (matches) {
            Log.d("DrinkDAO", "Drink matches search query: " + drink.getName());
        }

        return matches;
    }

    public enum DRINK_FIELD {
        NAME("name"),
        DESCRIPTION("description"),
        RATE("rate"),
        CREATED_AT("createdAt"),
        UPDATED_AT("updatedAt"),
        CATEGORY_ID("categoryId"),
        USER_ID("userId");

        private final String value;

        DRINK_FIELD(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
