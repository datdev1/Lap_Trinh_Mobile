package com.b21dccn216.pocketcocktail.view.DetailDrink;

import com.b21dccn216.pocketcocktail.model.Drink;
import com.b21dccn216.pocketcocktail.base.BaseContract;

import java.util.List;

public interface DetailDrinkContract {
    interface View extends BaseContract.View {
        void showDrinkDetail(Drink drink);
        void showIngredient(String ingredientText);
        void showInstruction(String instructionText);
        void updateFavoriteIcon(boolean isFavorite);
        void showShareIntent(String text);
        void showSimilarDrinks(List<Drink> drinks);
    }

    interface Presenter extends BaseContract.Presenter<View> {
        void loadDrinkDetails(Drink drink);
        void checkFavorite(String drinkId);
        void toggleFavorite(Drink drink);
        void shareDrink(Drink drink);
    }
}
