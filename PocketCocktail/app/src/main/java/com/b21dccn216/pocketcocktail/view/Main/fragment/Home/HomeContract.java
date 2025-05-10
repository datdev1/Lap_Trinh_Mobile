package com.b21dccn216.pocketcocktail.view.Main.fragment.Home;

import com.b21dccn216.pocketcocktail.base.BaseContract;
import com.b21dccn216.pocketcocktail.model.Drink;
import com.b21dccn216.pocketcocktail.view.Main.model.DrinkWithCategoryDTO;

import java.util.List;

public interface HomeContract {
    interface View extends BaseContract.View {
        // Define view methods

        void showOneCategoryDrinkList(String cateName, List<Drink> drinkList);
        void showHighestRateDrinkList(List<Drink> drinkList);
        void showLatestDrinkList(List<Drink> drinkList);
        void showRecommendDrinkList(List<DrinkWithCategoryDTO> drinkList);

    }

    interface Presenter extends BaseContract.Presenter<View> {
        // Define presenter methods

    }
}
