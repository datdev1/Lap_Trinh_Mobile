package com.b21dccn216.pocketcocktail.view.Main.fragment.Discover;

import com.b21dccn216.pocketcocktail.base.BaseContract;
import com.b21dccn216.pocketcocktail.model.Category;
import com.b21dccn216.pocketcocktail.model.Ingredient;

import java.util.List;

public interface DiscoverContract {
    interface View extends BaseContract.View{
        // define view methods
        void showCategoryList(List<Category> categoryList);
        void showIngredientList(List<Ingredient> ingredientList);
    }

    interface Presenter extends BaseContract.Presenter<View>{
        // Define presenter methods
    }
}
