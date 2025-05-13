package com.b21dccn216.pocketcocktail.view.Search;

import com.b21dccn216.pocketcocktail.base.BaseContract;
import com.b21dccn216.pocketcocktail.model.Drink;
import com.b21dccn216.pocketcocktail.model.Ingredient;
import com.google.firebase.firestore.Query;

import java.util.List;

public interface SearchContract {
    interface View extends BaseContract.View {
        void showMessage(String message);
        void showError(String message);
        void hideKeyboard();
        void showLoading();
        void hideLoading();
        void showDrinks(List<Drink> drinks);
        void showIngredients(List<Ingredient> ingredients);

    }

    interface Presenter extends BaseContract.Presenter<SearchContract.View>{
        void loadDrinks(String sortField, Query.Direction sortOrder);
        void loadDrinksByCategory(String categoryId);
        void loadDrinksByIngredient(String ingredientId);
        void loadIngredients();
        void searchDrinks(String categoryId, String query, List<String> ingredientIds,String sortField, Query.Direction sortOrder);
        void searchIngredients(String query);
        void onDestroy();
    }
}
