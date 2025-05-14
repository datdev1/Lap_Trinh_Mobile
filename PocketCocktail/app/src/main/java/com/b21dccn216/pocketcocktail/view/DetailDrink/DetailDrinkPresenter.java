package com.b21dccn216.pocketcocktail.view.DetailDrink;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.b21dccn216.pocketcocktail.base.BasePresenter;
import com.b21dccn216.pocketcocktail.dao.DrinkCntFavDAO;
import com.b21dccn216.pocketcocktail.dao.DrinkDAO;
import com.b21dccn216.pocketcocktail.dao.FavoriteDAO;
import com.b21dccn216.pocketcocktail.dao.IngredientDAO;
import com.b21dccn216.pocketcocktail.dao.RecipeDAO;
import com.b21dccn216.pocketcocktail.dao.ReviewDAO;
import com.b21dccn216.pocketcocktail.dao.UserDAO;
import com.b21dccn216.pocketcocktail.helper.DialogHelper;
import com.b21dccn216.pocketcocktail.helper.SessionManager;
import com.b21dccn216.pocketcocktail.model.Drink;
import com.b21dccn216.pocketcocktail.model.DrinkCntFav;
import com.b21dccn216.pocketcocktail.model.Favorite;
import com.b21dccn216.pocketcocktail.model.Ingredient;
import com.b21dccn216.pocketcocktail.model.Recipe;
import com.b21dccn216.pocketcocktail.model.Review;
import com.b21dccn216.pocketcocktail.model.User;
import com.b21dccn216.pocketcocktail.view.CreateDrink.CreateDrinkActivity;
import com.b21dccn216.pocketcocktail.view.DetailDrink.model.IngredientWithAmountDTO;
import com.b21dccn216.pocketcocktail.view.DetailDrink.model.ReviewWithUserDTO;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DetailDrinkPresenter extends BasePresenter<DetailDrinkContract.View>
        implements DetailDrinkContract.Presenter {

    //
    //DAO
    private final FavoriteDAO favoriteDAO;
    private final DrinkCntFavDAO drinkCntFavDAO;
    private final DrinkDAO drinkDAO;
    private final RecipeDAO recipeDAO;
    private final IngredientDAO ingredientDAO;
    private final ReviewDAO reviewDAO;
    private final UserDAO userDAO;

    private boolean isFavorite = false;
    private boolean isFavoriteProcessing = false;
    private Favorite currentFavorite;
    private final String currentUserId;
    private final List<String> commentList = new ArrayList<>();
    private List<Recipe> ingredientList = new ArrayList<>();
    private List<Ingredient> ownedIngredientList = new ArrayList<>();
    private List<IngredientWithAmountDTO> ingredientWithAmountList = new ArrayList<>();


    public DetailDrinkPresenter() {
        favoriteDAO = new FavoriteDAO();
        drinkDAO = new DrinkDAO();
        recipeDAO = new RecipeDAO();
        ingredientDAO = new IngredientDAO();
        reviewDAO = new ReviewDAO();
        userDAO = new UserDAO();
        drinkCntFavDAO = new DrinkCntFavDAO();

        currentUserId = String.valueOf(SessionManager.getInstance().getUser().getUuid());
    }


    @Override
    public void loadDrinkDetails(Drink drink) {
        if (view == null || drink == null) return;

        // Load drink info
        view.showDrinkDetail(drink);

        // Load instruction
        for (String instruction : drink.getInstruction().split("\n")) {
            instruction = instruction.trim();
            if (!instruction.isEmpty()) {
                view.showInstruction(instruction);
            }
        }

        // Load ingredient
//        recipeDAO.getRecipesByDrinkId(drink.getUuid(), recipeSnapshots -> {
//            for (DocumentSnapshot doc : recipeSnapshots.getDocuments()) {
//                Recipe recipe = doc.toObject(Recipe.class);
//                if (recipe != null) {
//                    ingredientDAO.getIngredient(recipe.getIngredientId(), ingredientSnapshot -> {
//                        Ingredient ingredient = ingredientSnapshot.toObject(Ingredient.class);
//                        if (ingredient != null) {
//                            String line = ingredient.getName() + " (" + recipe.getAmount() + " " + ingredient.getUnit() + ")";
//                            view.showIngredient(line);
//                        }
//                    }, e -> view.showError(e.getMessage()));
//                }
//            }
//        }, e -> view.showError(e.getMessage()));

        recipeDAO.getRecipesByDrinkId(drink.getUuid(), new RecipeDAO.RecipeListCallback() {
            @Override
            public void onRecipeListLoaded(List<Recipe> recipes) {
                ingredientList.clear();
                ingredientList.addAll(recipes);
                ingredientWithAmountList.clear();
                for (Recipe recipe : recipes) {
                    ingredientDAO.getIngredient(recipe.getIngredientId(), new IngredientDAO.IngredientCallback() {
                        @Override
                        public void onIngredientLoaded(Ingredient ingredient) {
                            boolean isHas = ownedIngredientList.stream()
                                            .anyMatch(item -> item.getUuid().equals(ingredient.getUuid()));
                            Log.e("Ingredient", "Ingredient: " + ingredient.getName() + " isHas: " + isHas);
                            ingredientWithAmountList.add(new IngredientWithAmountDTO(
                                    ingredient,
                                    recipe.getAmount(),
                                    isHas
                            ));
                            //String line = ingredient.getName() + " (" + recipe.getAmount() + " " + ingredient.getUnit() + ")";
                            //Log.e("Ingredient", "Ingredient: " + line);
                            view.showIngredient(ingredientWithAmountList);
                        }

                        @Override
                        public void onError(Exception e) {
                            view.showError(e.getMessage());
                        }

                    });
                }
            }

            @Override
            public void onError(Exception e) {
                view.showError(e.getMessage());
            }
        });

        // Load similar drink
        loadSimilarDrinks(drink);

        //Load review
        loadReviews(drink.getUuid());

        // Load creator info
        loadCreatorInfo(drink.getUserId());
        // Load fav count
        loadFavCount(drink.getUuid());
    }

    @Override
    public void loadFavCount(String uuid) {
        drinkCntFavDAO.getDrinkCntFavByDrinkId(uuid, new DrinkCntFavDAO.DrinkCntFavCallback() {
            @Override
            public void onDrinkCntFavLoaded(DrinkCntFav drinkCntFav) {
                if(drinkCntFav == null) {
                    view.showCountFavourite(0);
                    return;
                }
                view.showCountFavourite(drinkCntFav.getCount());
            }

            @Override
            public void onError(Exception e) {
                view.showCountFavourite(0);
            }
        });
    }

    private void loadCreatorInfo(String userId) {
        if(userId == null) return;
        userDAO.getUser(userId, new UserDAO.UserCallback() {
            @Override
            public void onUserLoaded(User user) {
                if(view != null){
                    view.showCreatorInfo(user);
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }


    @Override
    public void checkFavorite(String drinkId) {
//        favoriteDAO.getFavoritesByUserId(currentUserId, querySnapshot -> {
//            isFavorite = false;
//            currentFavorite = null;
//            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
//                Favorite favorite = doc.toObject(Favorite.class);
//                if (favorite != null && favorite.getDrinkId().equals(drinkId)) {
//                    isFavorite = true;
//                    currentFavorite = favorite;
//                    break;
//                }
//            }
//            if (view != null) {
//                view.updateFavoriteIcon(isFavorite);
//            }
//        }, e -> {
//            if (view != null) view.showError(e.getMessage());
//        });
//
        favoriteDAO.getFavoritesByUserId(currentUserId, new FavoriteDAO.FavoriteListCallback() {

            @Override
            public void onFavoriteListLoaded(List<Favorite> favorites) {
                isFavorite = false;
                currentFavorite = null;
                for (Favorite favorite : favorites) {
                    if (favorite != null && favorite.getDrinkId().equals(drinkId)) {
                        isFavorite = true;
                        currentFavorite = favorite;
                        break;
                    }
                }
                if (view != null) {
                    view.updateFavoriteIcon(isFavorite);
                }
            }

            @Override
            public void onError(Exception e) {
                if (view != null) view.showError(e.getMessage());
            }
        });
    }


    @Override
    public void toggleFavorite(Drink drink) {
        if (drink == null || view == null || isFavoriteProcessing) return;

        isFavoriteProcessing = true;

        if (isFavorite && currentFavorite != null) {
            favoriteDAO.deleteFavorite(currentFavorite.getUuid(), unused -> {
                loadFavCount(drink.getUuid());
                isFavorite = false;
                currentFavorite = null;
                isFavoriteProcessing = false;
                view.updateFavoriteIcon(false);
            }, e -> {
                isFavoriteProcessing = false;
                view.showError(e.getMessage());
            });
        } else {
            Favorite favorite = new Favorite();
            favorite.setUserId(currentUserId);
            favorite.setDrinkId(drink.getUuid());
            favorite.generateUUID();

            favoriteDAO.addFavorite(favorite, unused -> {
                loadFavCount(drink.getUuid());
                isFavorite = true;
                currentFavorite = favorite;
                isFavoriteProcessing = false;
                view.updateFavoriteIcon(true);
            }, e -> {
                isFavoriteProcessing = false;
                view.showError(e.getMessage());
            });
        }
    }


    @Override
    public void shareDrink(Drink drink) {
        if (drink == null || view == null) return;

        String content = "Check out this drink: " + drink.getName() + "\n" + drink.getImage();
        view.showShareIntent(content);
    }

    @Override
    public void addReview(Review review) {
        reviewDAO.addReview(review, unused -> {
            if (view != null) {
                view.showAddReviewSuccess();
                loadReviews(review.getDrinkId());
            }
        }, e -> {
            if (view != null) {
                view.showError("Failed to add review: " + e.getMessage());
            }
        });
    }

    @Override
    public void loadReviews(String drinkId) {
//        reviewDAO.getReviewsByDrinkId(drinkId, snapshot -> {
//            List<Review> reviewList = new ArrayList<>();
//            List<ReviewWithUserDTO> reviewWithUsers = new ArrayList<>();
//
//            for (DocumentSnapshot doc : snapshot.getDocuments()) {
//                Review review = doc.toObject(Review.class);
//                if (review != null) {
//                    reviewList.add(review);
//                }
//            }
//
//            if (reviewList.isEmpty()) {
//                if (view != null) view.showReviews(reviewWithUsers);
//                return;
//            }
//
//            final int[] remaining = {reviewList.size()};
//            for (Review review : reviewList) {
//                userDAO.getUser(review.getUserId(), userSnapshot -> {
//                    User user = userSnapshot.toObject(User.class);
//                    reviewWithUsers.add(new ReviewWithUserDTO(review, user));
//                    if (--remaining[0] == 0 && view != null) {
//                        // sort by timestamp if needed
//                        view.showReviews(reviewWithUsers);
//                    }
//                }, e -> {
//                    if (--remaining[0] == 0 && view != null) {
//                        view.showReviews(reviewWithUsers);
//                    }
//                });
//            }
//
//        }, e -> {
//            if (view != null) {
//                view.showError("Failed to load reviews: " + e.getMessage());
//            }
//        });
        reviewDAO.getReviewsByDrinkId(drinkId, new ReviewDAO.ReviewListCallback() {
            @Override
            public void onReviewListLoaded(List<Review> reviewList) {
                List<ReviewWithUserDTO> reviewWithUsers = new ArrayList<>();

                if (reviewList.isEmpty()) {
                    if (view != null) view.showReviews(reviewWithUsers);
                    return;
                }

                final int[] remaining = {reviewList.size()};
                for (int i = 0; i < reviewList.size(); i++) {
                    final Review review = reviewList.get(i);
                    userDAO.getUser(review.getUserId(), new UserDAO.UserCallback() {

                        @Override
                        public void onUserLoaded(User user) {
                            Log.e("Review", "Loaded user for review: " + review.getUuid() + " -> " + user.getName());
                            reviewWithUsers.add(new ReviewWithUserDTO(review, user));
                            if (--remaining[0] == 0 && view != null) {
                                // sort by timestamp if needed
                                view.showReviews(reviewWithUsers);
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            if (--remaining[0] == 0 && view != null) {
                                view.showReviews(reviewWithUsers);
                            }
                        }
                    });
                }
            }
            @Override
            public void onError(Exception e) {
                if (view != null) {
                    view.showError("Failed to load reviews: " + e.getMessage());
                    Log.d("ReviewDAO", "Failed to load reviews: " + e.getMessage());
                }
            }

        });

    }

    @Override
    public void loadSimilarDrinks(Drink drink) {
        // drinkDAO.getDrinksByCategoryId(drink.getCategoryId(),
        //         querySnapshot -> {
        //             List<Drink> similarDrinks = new ArrayList<>();
        //             for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
        //                 Drink similarDrink = doc.toObject(Drink.class);
        //                 if (similarDrink != null && !similarDrink.getUuid().equals(drink.getUuid())) {
        //                     similarDrinks.add(similarDrink);
        //                 }
        //             }
        //             view.showSimilarDrinks(similarDrinks);
        //         },
        //         e -> {
        //             Log.e("DetailDrink", "Failed to load similar drinks", e);
        //             view.showError(e.getMessage());
        //         }
        // );
        drinkDAO.getDrinksByCategoryId(drink.getCategoryId(),
                new DrinkDAO.DrinkListCallback()
                {
                    @Override
                    public void onDrinkListLoaded(List<Drink> drinks) {
                        List<Drink> similarDrinks = new ArrayList<>();
                        for (Drink d : drinks) {
                            if (!d.getUuid().equals(drink.getUuid())) {
                                similarDrinks.add(d);
                            }
                        }
                        view.showSimilarDrinks(similarDrinks);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("DetailDrink", "Failed to load similar drinks", e);
                        view.showError(e.getMessage());
                    }
                }
        );
    }

    @Override
    public void onAddReviewClicked(String drinkId) {
        if (view != null) {
            view.showAddReviewDialog(drinkId, null);
        }
    }

    @Override
    public void submitReview(String comment, String drinkId, float rating) {
        Review review = new Review();
        review.setComment(comment);
        review.setDrinkId(drinkId);
        review.setUserId(currentUserId);
        review.setRate(rating);

        reviewDAO.addReview(review,
                unused -> {
                    view.showMessage("Đánh giá đã được gửi thành công");
                    loadReviews(drinkId);
                },
                e -> view.showMessage("Gửi đánh giá thất bại: " + e.getMessage())
        );
    }

    @Override
    public void updateReview(String comment, String drinkId, float rating, Review review) {
        review.setComment(comment);
        review.setRate(rating);

        reviewDAO.updateReview(review,
                unused -> {
                    view.showMessage("Đánh giá đã được cập nhật");
                    loadReviews(drinkId);
                },
                e -> view.showMessage("Cập nhật đánh giá thất bại: " + e.getMessage())
        );
    }

    @Override
    public void onEditReviewClicked(Review review) {
        view.showAddReviewDialog(review.getDrinkId(), review);
    }

    @Override
    public void onDeleteReviewClicked(Review review) {
        reviewDAO.deleteReview(review.getUuid(),
                unused -> {
                    view.showMessage("Đánh giá đã được xoá");
                    loadReviews(review.getDrinkId());
                },
                e -> view.showMessage("Xoá thất bại")
        );
    }

    @Override
    public List<Recipe> getRecipes() {
        return this.ingredientList;
    }

    @Override
    public void checkIfUserHasReviewed(String drinkId, String userId) {
        reviewDAO.getReviewByUserAndDrink(userId, drinkId, new ReviewDAO.ReviewCallback() {
            @Override
            public void onReviewLoaded(Review review) {
                view.showUserHasReviewed(review != null);
            }

            @Override
            public void onError(Exception e) {
                view.showError("Failed to check review: " + e.getMessage());
            }
        });
    }

    public List<Ingredient> getOwnedIngredientList() {
        return ownedIngredientList;
    }

    @Override
    public void setOwnedIngredientList(List<Ingredient> ownedIngredientList) {
        this.ownedIngredientList = ownedIngredientList;
    }
}
