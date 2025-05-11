package com.b21dccn216.pocketcocktail.view.DetailDrink;

import com.b21dccn216.pocketcocktail.model.Drink;
import com.b21dccn216.pocketcocktail.base.BaseContract;
public interface DetailDrinkContract {
    interface View extends BaseContract.View {
        void showDrinkDetail(Drink drink);
        void showIngredient(String ingredientText);
        void showInstruction(String instructionText);
    }

    interface Presenter extends BaseContract.Presenter<View> {
        void loadDrinkDetails(Drink drink);
    }
}
