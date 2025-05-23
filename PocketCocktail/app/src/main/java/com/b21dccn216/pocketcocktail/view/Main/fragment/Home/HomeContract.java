package com.b21dccn216.pocketcocktail.view.Main.fragment.Home;

import com.b21dccn216.pocketcocktail.base.BaseContract;
import com.b21dccn216.pocketcocktail.model.Category;
import com.b21dccn216.pocketcocktail.model.Drink;
import com.b21dccn216.pocketcocktail.view.Main.model.DrinkWithFavCount;

import java.util.List;

public interface HomeContract {

    interface View extends BaseContract.View {
        // Define view methods

        void updateBannerDrink(Drink drink);
        void updateCateoryAndDrinkList(Category category, List<Drink> drinkList);
        void updateHighestRatedDrinks(List<Drink> drinkList);
        void showLatestDrinkList(List<Drink> drinkList);
        void showRecommendDrinkList(List<DrinkWithFavCount> drinkList);

    }

    interface Presenter extends BaseContract.Presenter<View> {
        // Define presenter methods
        void refreshScreen();

    }
}
