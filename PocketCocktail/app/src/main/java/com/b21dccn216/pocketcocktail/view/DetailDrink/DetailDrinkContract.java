package com.b21dccn216.pocketcocktail.view.DetailDrink;

import com.b21dccn216.pocketcocktail.model.Drink;
import com.b21dccn216.pocketcocktail.base.BaseContract;
import com.b21dccn216.pocketcocktail.model.Recipe;
import com.b21dccn216.pocketcocktail.model.Review;
import com.b21dccn216.pocketcocktail.model.User;
import com.b21dccn216.pocketcocktail.view.DetailDrink.model.IngredientWithAmountDTO;
import com.b21dccn216.pocketcocktail.view.DetailDrink.model.ReviewWithUserDTO;

import java.util.List;

public interface DetailDrinkContract {
    interface View extends BaseContract.View {
        void showMessage(String message);
        void showDrinkDetail(Drink drink);
        void showIngredient(String ingredientText);
        void showInstruction(String instructionText);
        void updateFavoriteIcon(boolean isFavorite);
        void showShareIntent(String text);
        void showSimilarDrinks(List<Drink> drinks);
        void showReviews(List<ReviewWithUserDTO> reviews);
        void showAddReviewSuccess();
        void showAddReviewError(String message);
        void showAddReviewDialog(String drinkId, Review review);
        void showCreatorInfo(User creator);
        void showCountFavourite(int count);
        void showUserHasReviewed(boolean hasReviewed);
    }


    interface Presenter extends BaseContract.Presenter<View> {
        void loadDrinkDetails(Drink drink);
        void checkFavorite(String drinkId);
        void toggleFavorite(Drink drink);
        void loadFavCount(String drinkId);
        void shareDrink(Drink drink);
        void addReview(Review review);
        void loadReviews(String drinkId);
        void loadSimilarDrinks(Drink drink);
        void onAddReviewClicked(String drinkId);
        void submitReview(String comment, String drinkId, float rating);
        void updateReview(String comment, String drinkId, float rating, Review review);
        void onEditReviewClicked(Review review);
        void onDeleteReviewClicked(Review review);
        void checkIfUserHasReviewed(String drinkId, String userId);

        List<Recipe> getRecipes();
    }
}
