package com.b21dccn216.pocketcocktail.dao;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.b21dccn216.pocketcocktail.model.Category;
import com.b21dccn216.pocketcocktail.model.Drink;
import com.b21dccn216.pocketcocktail.model.Favorite;
import com.b21dccn216.pocketcocktail.model.Recipe;
import com.b21dccn216.pocketcocktail.model.Review;
import com.b21dccn216.pocketcocktail.helper.DialogHelper;
import com.b21dccn216.pocketcocktail.helper.HelperDialog;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class DrinkDAO {
    private static final String COLLECTION_NAME = "drink";
    private final FirebaseFirestore db;
    private final CollectionReference drinkRef;

//    private final ImageDAO imageDAO;
    private ImageDAO imageDAO; // Khai báo để tránh lỗi các hàm Imagur, comment lại và dùng private final ImageDAO imageDAO; để dùng lại Imgur
    private static final String IMGUR_REFRESH_TOKEN = "2396b095b3402713d6dd7895146265c06f22fc71";
    private boolean isAuthenticated = false;
    private final CountDownLatch authLatch = new CountDownLatch(1);

    public static final String ERROR_USER_NOT_AUTH = "User not authenticated";

    public DrinkDAO() {
        db = FirebaseFirestore.getInstance();
        drinkRef = db.collection(COLLECTION_NAME);
        Log.d("vietdung", "Initialized with collection: " + COLLECTION_NAME);
//        imageDAO = new ImageDAO();
//        authenticateImgur();
    }

    private void authenticateImgur() {
        imageDAO.authenticate(IMGUR_REFRESH_TOKEN, new ImageDAO.AuthCallback() {
            @Override
            public void onSuccess() {
                Log.d("vietdung", "Imgur authentication successful");
                isAuthenticated = true;
                authLatch.countDown();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("vietdung", "Imgur authentication failed: " + e.getMessage());
                isAuthenticated = false;
                authLatch.countDown();
            }
        });
    }

    private void waitForAuthentication() {
        try {
            authLatch.await();
        } catch (InterruptedException e) {
            Log.e("vietdung", "Authentication wait interrupted", e);
        }
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

    private Drink convertDocumentToDrink(DocumentSnapshot doc){
        Drink drink = new Drink();
        drink.setUuid(doc.getString("uuid"));
        drink.setName(doc.getString("name"));
        drink.setUserId(doc.getString("userId"));
        drink.setImage(doc.getString("image"));
        drink.setCategoryId(doc.getString("categoryId"));
        drink.setInstruction(doc.getString("instruction"));
        drink.setDescription(doc.getString("description"));

        Double rate = doc.getDouble("rate");
        drink.setRate(rate != null ? rate : 0.0);
        
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
        drink.generateUUID();
        Map<String, Object> data = convertDrinkToMap(drink);
        drinkRef.document(drink.getUuid())
                .set(data)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void addDrinkWithImageWithImgur(Context context, Drink drink, Uri imageUri,
                                  OnSuccessListener<Void> onSuccess,
                                  OnFailureListener onFailure) {
        String title = ImageDAO.ImageDaoFolderForDrink + "_" + drink.getName() + "_" + drink.getUuid();

        if (isAuthenticated) {
            imageDAO.uploadImageAuthenticated(context, imageUri, title, new ImageDAO.UploadCallback() {
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
        } else {
            imageDAO.uploadImageAnonymous(context, imageUri, title, new ImageDAO.UploadCallback() {
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
    }

    public void updateDrink(Drink updatedDrink, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        Map<String, Object> data = convertDrinkToMap(updatedDrink);
        drinkRef.document(updatedDrink.getUuid())
                .set(data)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void updateDrinkWithImageWithImgur(Context context, Drink updatedDrink, @Nullable Uri newImageUri,
                                     OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        if (newImageUri != null) {
            String title = ImageDAO.ImageDaoFolderForDrink + "_" + updatedDrink.getName() + "_" + updatedDrink.getUuid();
            
            if (isAuthenticated) {
                // Delete old image first if it exists
                if (updatedDrink.getImage() != null && !updatedDrink.getImage().isEmpty()) {
                    imageDAO.deleteImageFromImgur(updatedDrink.getImage(), new ImageDAO.DeleteCallback() {
                        @Override
                        public void onSuccess() {
                            uploadNewImageAuthenticatedWithImgur(context, updatedDrink, newImageUri, title, onSuccess, onFailure);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e("vietdung", "Failed to delete old image: " + e.getMessage());
                            uploadNewImageAuthenticatedWithImgur(context, updatedDrink, newImageUri, title, onSuccess, onFailure);
                        }
                    });
                } else {
                    uploadNewImageAuthenticatedWithImgur(context, updatedDrink, newImageUri, title, onSuccess, onFailure);
                }
            } else {
                // Anonymous upload without deleting old image
                uploadNewImageAnonymousWithImgur(context, updatedDrink, newImageUri, title, onSuccess, onFailure);
            }
        } else {
            updateDrink(updatedDrink, onSuccess, onFailure);
        }
    }

    private void uploadNewImageAuthenticatedWithImgur(Context context, Drink updatedDrink, Uri newImageUri,
                                           String title, OnSuccessListener<Void> onSuccess, 
                                           OnFailureListener onFailure) {
        imageDAO.uploadImageAuthenticated(context, newImageUri, title, new ImageDAO.UploadCallback() {
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
    }

    private void uploadNewImageAnonymousWithImgur(Context context, Drink updatedDrink, Uri newImageUri,
                                       String title, OnSuccessListener<Void> onSuccess, 
                                       OnFailureListener onFailure) {
        imageDAO.uploadImageAnonymous(context, newImageUri, title, new ImageDAO.UploadCallback() {
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
    }

    private void checkDrinkDependencies(String drinkId, OnSuccessListener<Boolean> onSuccess, OnFailureListener onFailure) {
        // Check Recipe dependencies
        RecipeDAO recipeDAO = new RecipeDAO();
        recipeDAO.getRecipesByDrinkId(drinkId, new RecipeDAO.RecipeListCallback() {
            @Override
            public void onRecipeListLoaded(List<Recipe> recipes) {
                if (!recipes.isEmpty()) {
                    onFailure.onFailure(new Exception("Cannot delete drink: It has associated recipes"));
                    return;
                }
                
                // Check Review dependencies
                ReviewDAO reviewDAO = new ReviewDAO();
                reviewDAO.getReviewsByDrinkId(drinkId, new ReviewDAO.ReviewListCallback() {
                    @Override
                    public void onReviewListLoaded(List<Review> reviews) {
                        if (!reviews.isEmpty()) {
                            onFailure.onFailure(new Exception("Cannot delete drink: It has associated reviews"));
                            return;
                        }
                        
                        // Check Favorite dependencies
                        FavoriteDAO favoriteDAO = new FavoriteDAO();
                        favoriteDAO.getFavoriteDrinkId(drinkId, new FavoriteDAO.FavoriteListCallback() {
                            @Override
                            public void onFavoriteListLoaded(List<Favorite> favorites) {
                                if (!favorites.isEmpty()) {
                                    onFailure.onFailure(new Exception("Cannot delete drink: It has associated favorites"));
                                    return;
                                }
                                
                                // If no dependencies found, allow deletion
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

    public void deleteDrinkWithImgur(String uuid, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        // First check dependencies
        checkDrinkDependencies(uuid, 
            canDelete -> {
                if (canDelete) {
                    drinkRef.document(uuid).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                Drink drink = convertDocumentToDrink(documentSnapshot);
                                if (drink != null && drink.getImage() != null && !drink.getImage().isEmpty()) {
                                    if (isAuthenticated) {
                                        // Delete image if authenticated
                                        imageDAO.deleteImageFromImgur(drink.getImage(), new ImageDAO.DeleteCallback() {
                                            @Override
                                            public void onSuccess() {
                                                deleteDrinkDocument(uuid, onSuccess, onFailure);
                                            }

                                            @Override
                                            public void onFailure(Exception e) {
                                                Log.e("vietdung", "Failed to delete image: " + e.getMessage());
                                                deleteDrinkDocument(uuid, onSuccess, onFailure);
                                            }
                                        });
                                    } else {
                                        // If not authenticated, just delete drink document
                                        deleteDrinkDocument(uuid, onSuccess, onFailure);
                                    }
                                } else {
                                    // If no image, just delete the drink
                                    deleteDrinkDocument(uuid, onSuccess, onFailure);
                                }
                            })
                            .addOnFailureListener(onFailure);
                }
            },
            onFailure
        );
    }

    private void deleteDrinkDocument(String uuid, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        drinkRef.document(uuid)
                .delete()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getDrink(String drinkUuid, DrinkDAO.DrinkCallback callback) {
        drinkRef.document(drinkUuid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot == null) {
                        callback.onError(new Exception("Drink not found"));
                        return;
                    }
                    Drink drink = convertDocumentToDrink(documentSnapshot);
                    callback.onDrinkLoaded(drink);
                })
                .addOnFailureListener(callback::onError);
    }

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
                    Log.d("vietdung", "getAllDrinks: " + drinks);
                    callback.onDrinkListLoaded(drinks);
                })
                .addOnFailureListener(callback::onError);
    }

    public void getDrinksByUserId(String userId, DrinkListCallback callback) {
        drinkRef.whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Drink> drinks = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Drink drink = convertDocumentToDrink(doc);
                        if (drink != null) {
                            drinks.add(drink);
                        }
                    }
                    Log.d("vietdung", "getAllDrinks: " + drinks);
                    drinks.sort((a, b) -> a.getName().compareTo(b.getName()));
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
                    Log.d("vietdung", "getAllDrinks: " + drinks);
                    callback.onDrinkListLoaded(drinks);
                })
                .addOnFailureListener(callback::onError);
    }

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
                    Log.d("vietdung", "getAllDrinks: " + drinks);
                    callback.onDrinkListLoaded(drinks);
                })
                .addOnFailureListener(callback::onError);
    }

    public void getDrinksSorted(String sortField, Query.Direction sortDirection, DrinkListCallback callback) {
        Query query = drinkRef;

        if (sortField != null && sortDirection != null) {
            query = drinkRef.orderBy(sortField, sortDirection);
        }

        query.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Drink> drinks = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Drink drink = convertDocumentToDrink(doc);
                        if (drink != null) {
                            drinks.add(drink);
                        }
                    }

                    Log.d("vietdung", "Loaded " + drinks.size() + " drinks, sorted by: " + sortField);
                    callback.onDrinkListLoaded(drinks);
                })
                .addOnFailureListener(e -> {
                    Log.e("vietdung", "Error getting drinks", e);
                    callback.onError(e);
                });
    }

    public void getFeatureDrink(DrinkCallback callback) {
        drinkRef
                .limit(20)
                .get()

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
        Log.d("vietdung", "Getting all drinks with sort field: " + sortField.getValue() + ", order: " + sortOrder);
        
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

                    Log.d("vietdung", "Query returned " + queryDocumentSnapshots.size() + " documents");

                    for (DocumentSnapshot drinkSnapshot : queryDocumentSnapshots.getDocuments()) {
                        Log.d("vietdung", "Document data: " + drinkSnapshot.getData());
                        Drink drink = convertDocumentToDrink(drinkSnapshot);
                        if (drink != null) {
                            drinkList.add(drink);
                            lastVisible = drinkSnapshot;
                            Log.d("vietdung", "Added drink: " + drink.getName());
                        } else {
                            Log.e("vietdung", "Failed to convert document to Drink object");
                        }
                    }
                    
                    Log.d("vietdung", "Final list size: " + drinkList.size());
                    callback.onDrinkListLoaded(drinkList, lastVisible);
                })
                .addOnFailureListener(e -> {
                    Log.e("vietdung", "Error executing query", e);
                    callback.onError(e);
                });
    }

    public void searchDrinksWithSort(String query, int limit, DocumentSnapshot startAfter, 
                                   DRINK_FIELD sortField, Query.Direction sortOrder,
                                   DrinkListWithLastDocCallback callback) {
        String searchQuery = query.toLowerCase();
        Log.d("vietdung", "Searching with query: " + searchQuery);
        
        drinkRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Drink> allDrinks = new ArrayList<>();
                    List<Drink> filteredDrinks = new ArrayList<>();
                    DocumentSnapshot lastVisible = null;

                    Log.d("vietdung", "Total documents: " + queryDocumentSnapshots.size());

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

                    Log.d("vietdung", "Filtered list size: " + filteredDrinks.size());
                    Log.d("vietdung", "Paginated list size: " + paginatedDrinks.size());

                    callback.onDrinkListLoaded(paginatedDrinks, lastVisible);
                })
                .addOnFailureListener(e -> {
                    Log.e("vietdung", "Error searching drinks", e);
                    callback.onError(e);
                });
    }

    private void sortDrinks(List<Drink> drinks, DRINK_FIELD sortField, Query.Direction sortOrder) {
        Log.d("vietdung", "Sorting drinks by: " + sortField.getValue() + " in " + sortOrder + " order");
        Log.d("vietdung", "Before sorting: " + drinks.size() + " drinks");
        
        drinks.sort((d1, d2) -> {
            int comparison = 0;
            try {
                switch (sortField) {
                    case NAME:
                        comparison = d1.getName().compareToIgnoreCase(d2.getName());
                        break;
                    case DESCRIPTION:
                        comparison = d1.getDescription().compareToIgnoreCase(d2.getDescription());
                        break;
                    case RATE:
                        comparison = Double.compare(d1.getRate(), d2.getRate());
                        break;
                    case CREATED_AT:
                        comparison = d1.getCreatedAtTimestamp().compareTo(d2.getCreatedAtTimestamp());
                        break;
                    case UPDATED_AT:
                        comparison = d1.getUpdatedAtTimestamp().compareTo(d2.getUpdatedAtTimestamp());
                        break;
                    case CATEGORY_ID:
                        comparison = d1.getCategoryId().compareToIgnoreCase(d2.getCategoryId());
                        break;
                    case USER_ID:
                        comparison = d1.getUserId().compareToIgnoreCase(d2.getUserId());
                        break;
                }
            } catch (NullPointerException e) {
                Log.e("vietdung", "Error comparing drinks: " + e.getMessage());
                return 0;
            }
            
            // If sortOrder is DESCENDING, reverse the comparison
            return sortOrder == Query.Direction.DESCENDING ? -comparison : comparison;
        });
        
        Log.d("vietdung", "After sorting: " + drinks.size() + " drinks");
        // Log first few items to verify sorting
        for (int i = 0; i < Math.min(3, drinks.size()); i++) {
            Drink drink = drinks.get(i);
            Log.d("vietdung", "Sorted item " + i + ": " +
                (sortField == DRINK_FIELD.NAME ? drink.getName() :
                 sortField == DRINK_FIELD.RATE ? String.valueOf(drink.getRate()) :
                 sortField == DRINK_FIELD.CREATED_AT ? drink.getCreatedAtTimestamp().toString() :
                 sortField == DRINK_FIELD.UPDATED_AT ? drink.getUpdatedAtTimestamp().toString() :
                 sortField == DRINK_FIELD.CATEGORY_ID ? drink.getCategoryId() :
                 sortField == DRINK_FIELD.USER_ID ? drink.getUserId() :
                 sortField == DRINK_FIELD.DESCRIPTION ? drink.getDescription() : "unknown"));
        }
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
            Log.d("vietdung", "Drink matches search query: " + drink.getName());
        }

        return matches;
    }

    // Trường hợp 2  Nếu có Category / Name và không có list IngredientID
    public void searchDrinksByCategory(String query, String categoryId, DrinkListCallback callback) {
        String searchQuery = query != null ? query.toLowerCase() : "";
        Log.d("vietdung", "Searching with query: " + searchQuery);
        Log.d("vietdung", "Category ID: " + categoryId);

        Query drinkQuery = drinkRef;
        if (categoryId != null && !categoryId.isEmpty()) {
            drinkQuery = drinkQuery.whereEqualTo("categoryId", categoryId);
        }

        drinkQuery.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Drink> filteredDrinks = new ArrayList<>();

                    Log.d("vietdung", "Total documents: " + queryDocumentSnapshots.size());

                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Drink drink = convertDocumentToDrink(document);
                        if (drink != null && (searchQuery.isEmpty() || drink.getName().toLowerCase().contains(searchQuery))) {
                            filteredDrinks.add(drink);
                        }
                    }
                    Log.d("vietdung", "Success Truong hop 2: " + formatDrinksListForLog(filteredDrinks));
                    callback.onDrinkListLoaded(filteredDrinks);
                })
                .addOnFailureListener(e -> {
                    Log.e("vietdung", "Trường hợp 2  Nếu có Category / Name và không có list IngredientID" + e.toString());
                    callback.onError(e);
                });
    }

    // Trường hợp 1: Nếu có Category / Name và có list IngredientID
    public void searchDrinksByCategoryAndIngredientID(String query, String categoryId, List<String> ingredientIds, DrinkListCallback callback) {
        searchDrinksByCategory(query, categoryId, new DrinkListCallback() {
            @Override
            public void onDrinkListLoaded(List<Drink> drinks) {
                RecipeDAO recipeDAO = new RecipeDAO();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    recipeDAO.searchDrinkIDByIngredient(drinks.stream().map(Drink::getUuid).toList(), ingredientIds, new RecipeDAO.DrinkIDListCallback() {
                        @Override
                        public void onDrinkIDListLoaded(List<String> drinkIds) {
                            List<Drink> filteredDrinks = new ArrayList<>();
                            for (Drink drink : drinks) {
                                if (drinkIds.contains(drink.getUuid())) {
                                    filteredDrinks.add(drink);
                                }
                            }
                            Log.d("vietdung", "Success Truong hop 1: " + formatDrinksListForLog(filteredDrinks));
                            callback.onDrinkListLoaded(filteredDrinks);
                        }

                        @Override
                        public void onError(Exception e) {
                            callback.onError(e);
                        }
                    });
                }
            }
            @Override
            public void onError(Exception e) {
                Log.e("vietdung"," Trường hợp 1: Nếu có Category / Name và có list IngredientID" + e.toString());
                callback.onError(e);
            }
        });
    }

    // Trường hợp 3: Nếu không có Category / Name và có list IngredientID
    // Hàm phục vụ cho trường hợp 3
    public void getAllDrinkWithListDrinkID(List<String> drinkIds, DrinkListCallback callback) {
        if (drinkIds == null || drinkIds.isEmpty()) {
            callback.onDrinkListLoaded(new ArrayList<>());
            return;
        }

        drinkRef.whereIn("uuid", drinkIds)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Drink> drinks = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Drink drink = convertDocumentToDrink(doc);
                        if (drink != null) {
                            drinks.add(drink);
                        }
                    }
                    // Sort drinks by name for consistent ordering
                    drinks.sort(Comparator.comparing(Drink::getName));
                    callback.onDrinkListLoaded(drinks);
                })
                .addOnFailureListener(e -> {
                    Log.e("vietdung", "Error getting drinks by IDs" +  e.toString());
                    callback.onError(e);
                });
    }

    public void getAllDrinkWithListIngredientID(List<String> ingredientIds, DrinkListCallback callback)
    {
        RecipeDAO recipeDAO = new RecipeDAO();
        recipeDAO.searchDrinkIDByIngredient(ingredientIds, new RecipeDAO.DrinkIDListCallback() {

            @Override
            public void onDrinkIDListLoaded(List<String> drinkIds) {
                getAllDrinkWithListDrinkID(drinkIds, new DrinkListCallback() {
                    @Override
                    public void onDrinkListLoaded(List<Drink> drinks) {
                        Log.d("vietdung", "Success Truong hop 3: " + formatDrinksListForLog(drinks));
                        callback.onDrinkListLoaded(drinks);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("vietdung"," Trường hợp 3, giai đoạn 1: Nếu không có Category / Name và có list IngredientID" + e.toString());
                        callback.onError(e);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Log.e("vietdung","Trường hợp 3,giai đoạn 2: Nếu không có Category / Name và có list IngredientID" + e.toString());
                callback.onError(e);
            }
        });

    }

    // Trường hợp 4: Nếu không có Category / Name và không có list IngredientID
    
    public void getAllDrinkWithLimit(int limit, DrinkListCallback callback) {
        drinkRef.limit(limit)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Drink> drinks = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Drink drink = convertDocumentToDrink(doc);
                        if (drink != null) {
                            drinks.add(drink);
                        }
                    }
                    Log.d("vietdung", "Success Truong hop 4: " + formatDrinksListForLog(drinks));
                    callback.onDrinkListLoaded(drinks);
                })
                .addOnFailureListener(e -> {
                    Log.e("vietdung", "Trường hợp 4: Nếu không có Category / Name và không có list IngredientID" + e.toString());
                    callback.onError(e);
                });
    }

    //Xử lý tổng quát 4 trường hợp
    public void searchDrinkTotal(String query, @Nullable String categoryId, @Nullable List<String> ingredientIds, int limit, DrinkListCallback callback) {
        Log.d("vietdung", "------------------------------------------------");
        Log.d("vietdung", "searchDrinkTotal: \n");
        Log.d("vietdung", "query: " + query);
        Log.d("vietdung", "categoryId: " + categoryId);
        Log.d("vietdung", "List<String> ingredientIds: " + ingredientIds);
        Log.d("vietdung", "limit: " + limit);
        Log.d("vietdung", "------------------------------------------------");
        // Trường hợp 1: Nếu có Category / Name và có list IngredientID
        if ((categoryId != null && !categoryId.isEmpty() || (query != null && !query.isEmpty())) && ingredientIds != null && !ingredientIds.isEmpty()) {
            searchDrinksByCategoryAndIngredientID(query, categoryId, ingredientIds, callback);
        }
        // Trường hợp 2: Nếu có Category / Name và không có list IngredientID
        else if (categoryId != null && !categoryId.isEmpty() || (query != null && !query.isEmpty())) {
            searchDrinksByCategory(query, categoryId, callback);
        }
        // Trường hợp 3: Nếu không có Category / Name và có list IngredientID
        else if ((categoryId == null || categoryId.isEmpty()) && ingredientIds != null && !ingredientIds.isEmpty()) {
            getAllDrinkWithListIngredientID(ingredientIds, callback);
        }
        // Trường hợp 4: Nếu không có Category / Name và không có list IngredientID
        else {
            getAllDrinkWithLimit(limit, callback);
        }
    }

    public void searchDrinkTotalWithSort(@Nullable String query, @Nullable String categoryId, @Nullable List<String> ingredientIds, int limit, @Nullable DRINK_FIELD sortTag, @Nullable Query.Direction sortOrder, DrinkListCallback callback) {
        Log.d("vietdung", "------------------------------------------------");
        Log.d("vietdung", "searchDrinkTotal: \n");
        Log.d("vietdung", "query: " + query);
        Log.d("vietdung", "categoryId: " + categoryId);
        Log.d("vietdung", "List<String> ingredientIds: " + ingredientIds);
        Log.d("vietdung", "limit: " + limit);
        Log.d("vietdung", "sortField: " + (sortTag != null ? sortTag.getValue() : "null"));
        Log.d("vietdung", "sortOrder: " + (sortOrder != null ? sortOrder : "null"));
        Log.d("vietdung", "------------------------------------------------");
        
        if (query == null) {
            query = "";
        }

        // Create a wrapper callback to handle sorting
        DrinkListCallback sortingCallback = new DrinkListCallback() {
            @Override
            public void onDrinkListLoaded(List<Drink> drinks) {
                // Sort the drinks list only if both sortTag and sortOrder are not null
                if (sortTag != null && sortOrder != null) {
                    sortDrinks(drinks, sortTag, sortOrder);
                }
                // Return the list (sorted or unsorted)
                callback.onDrinkListLoaded(drinks);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        };

        // Trường hợp 1: Nếu có Category / Name và có list IngredientID
        if ((categoryId != null && !categoryId.isEmpty() || (query != null && !query.isEmpty())) && ingredientIds != null && !ingredientIds.isEmpty()) {
            searchDrinksByCategoryAndIngredientID(query, categoryId, ingredientIds, sortingCallback);
        }
        // Trường hợp 2: Nếu có Category / Name và không có list IngredientID
        else if (categoryId != null && !categoryId.isEmpty() || (query != null && !query.isEmpty())) {
            searchDrinksByCategory(query, categoryId, sortingCallback);
        }
        // Trường hợp 3: Nếu không có Category / Name và có list IngredientID
        else if ((categoryId == null || categoryId.isEmpty()) && ingredientIds != null && !ingredientIds.isEmpty()) {
            getAllDrinkWithListIngredientID(ingredientIds, sortingCallback);
        }
        // Trường hợp 4: Nếu không có Category / Name và không có list IngredientID
        else {
            getAllDrinkWithLimit(limit, sortingCallback);
        }
    }

    private String formatDrinksListForLog(List<Drink> drinks) {
        if (drinks == null || drinks.isEmpty()) {
            return "Empty drinks list";
        }

        StringBuilder sb = new StringBuilder("\n");
        sb.append("Total drinks: ").append(drinks.size()).append("\n");
        sb.append("----------------------------------------\n");
        
        for (int i = 0; i < drinks.size(); i++) {
            Drink drink = drinks.get(i);
            sb.append(i + 1).append(". ")
              .append("Name: ").append(drink.getName())
              .append(" | ID: ").append(drink.getUuid())
              .append(" | Category: ").append(drink.getCategoryId())
              .append(" | Rate: ").append(drink.getRate())
              .append(" | CreatedAt: ").append(drink.getCreatedAtTimestamp())
              .append(" | UpdatedAt: ").append(drink.getUpdatedAtTimestamp())
              .append("\n");
        }
        sb.append("----------------------------------------");
        
        return sb.toString();
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
        public static DRINK_FIELD fromString(String text) {
            Log.d("vietdung", "Sortquery: " + text);
            if (text == null || text.isEmpty()) {
                return NAME;
            }

            for (DRINK_FIELD field : DRINK_FIELD.values()) {
                if (field.value.equalsIgnoreCase(text)) {
                    return field;
                }
            }

            return NAME;
        }
    }

    public void addDrinkWithImage(Context context, Drink drink, Uri imageUri,
                                 OnSuccessListener<Void> onSuccess,
                                 OnFailureListener onFailure) {
        String title = drink.getName().replaceAll("\\s+", "_") + "_" + drink.getUuid();
        ImageDAOCloudinary imageDAO = new ImageDAOCloudinary(context);
        
        imageDAO.uploadImage(context, imageUri, ImageDAOCloudinary.ImageDaoFolderForDrink, title, new ImageDAOCloudinary.ImageUploadCallback() {
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

    public void updateDrinkWithImage(Context context, Drink updatedDrink, Uri newImageUri,
                                    OnSuccessListener<Void> onSuccess,
                                    OnFailureListener onFailure) {
        ImageDAOCloudinary imageDAO = new ImageDAOCloudinary(context);
        
        // Delete old image if exists
        if (updatedDrink.getImage() != null && !updatedDrink.getImage().isEmpty()) {
            imageDAO.deleteImageByUrl(updatedDrink.getImage(), new ImageDAOCloudinary.DeleteCallback() {
                @Override
                public void onSuccess() {
                    uploadNewImage(context, updatedDrink, newImageUri, onSuccess, onFailure);
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("vietdung", "Failed to delete old image: " + e.getMessage());
                    // Continue with upload even if delete fails
                    uploadNewImage(context, updatedDrink, newImageUri, onSuccess, onFailure);
                }
            });
        } else {
            uploadNewImage(context, updatedDrink, newImageUri, onSuccess, onFailure);
        }
    }

    private void uploadNewImage(Context context, Drink drink, Uri imageUri,
                              OnSuccessListener<Void> onSuccess,
                              OnFailureListener onFailure) {
        String title = drink.getName().replaceAll("\\s+", "_") + "_" + drink.getUuid();
        ImageDAOCloudinary imageDAO = new ImageDAOCloudinary(context);
        
        imageDAO.uploadImage(context, imageUri, ImageDAOCloudinary.ImageDaoFolderForDrink, title, new ImageDAOCloudinary.ImageUploadCallback() {
            @Override
            public void onSuccess(String imageUrl) {
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

    public void deleteDrink(Context context, String uuid, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        // First check dependencies
        checkDrinkDependencies(uuid, 
            canDelete -> {
                if (canDelete) {
                    drinkRef.document(uuid).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                Drink drink = convertDocumentToDrink(documentSnapshot);
                                if (drink != null && drink.getImage() != null && !drink.getImage().isEmpty()) {
                                    // Try to delete image
                                    ImageDAOCloudinary imageDAO = new ImageDAOCloudinary(context);
                                    imageDAO.deleteImageByUrl(drink.getImage(), new ImageDAOCloudinary.DeleteCallback() {
                                        @Override
                                        public void onSuccess() {
                                            deleteDrinkDocument(uuid, onSuccess, onFailure);
                                        }

                                        @Override
                                        public void onFailure(Exception e) {
                                            Log.e("vietdung", "Failed to delete image: " + e.getMessage());
                                            // Continue with drink deletion even if image deletion fails
                                            deleteDrinkDocument(uuid, onSuccess, onFailure);
                                        }
                                    });
                                } else {
                                    // If no image, just delete the drink
                                    deleteDrinkDocument(uuid, onSuccess, onFailure);
                                }
                            })
                            .addOnFailureListener(onFailure);
                }
            },
            onFailure
        );
    }
} 