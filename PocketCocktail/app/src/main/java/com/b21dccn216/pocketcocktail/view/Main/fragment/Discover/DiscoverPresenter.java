package com.b21dccn216.pocketcocktail.view.Main.fragment.Discover;

import com.b21dccn216.pocketcocktail.base.BasePresenter;
import com.b21dccn216.pocketcocktail.dao.CategoryDAO;
import com.b21dccn216.pocketcocktail.dao.IngredientDAO;
import com.b21dccn216.pocketcocktail.model.Category;
import com.b21dccn216.pocketcocktail.model.Ingredient;

import java.util.List;

public class DiscoverPresenter extends BasePresenter<DiscoverContract.View>
        implements DiscoverContract.Presenter
{
    private CategoryDAO categoryDAO = new CategoryDAO();
    private IngredientDAO ingredientDAO  = new IngredientDAO();

    public DiscoverPresenter(){
        super();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        getCategories();
        getIngredients();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void getCategories() {
        categoryDAO.getAllCategorys(new CategoryDAO.CategoryListCallback() {
            @Override
            public void onCategoryListLoaded(List<Category> categorys) {
                if(view == null) return;
                view.showCategoryList(categorys);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    private void getIngredients() {
        ingredientDAO.getAllIngredients(new IngredientDAO.IngredientListCallback() {
            @Override
            public void onIngredientListLoaded(List<Ingredient> ingredients) {
                if(view == null) return;
                view.showIngredientList(ingredients);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

}