package com.b21dccn216.pocketcocktail.view.Main.fragment.Home;

import android.util.Log;

import com.b21dccn216.pocketcocktail.base.BasePresenter;
import com.b21dccn216.pocketcocktail.dao.CategoryDAO;
import com.b21dccn216.pocketcocktail.dao.DrinkDAO;
import com.b21dccn216.pocketcocktail.model.Category;
import com.b21dccn216.pocketcocktail.model.Drink;
import com.b21dccn216.pocketcocktail.view.Main.model.DrinkWithCategoryDTO;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class HomePresenter
    extends BasePresenter<HomeContract.View>
    implements HomeContract.Presenter
{

    private DrinkDAO drinkDAO = new DrinkDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();


    public HomePresenter() {
        super();

    }

    @Override
    public void onCreate() {
        super.onCreate();

        getHighestRateDrinks();
        getOneCategoryDrinkList();

        getRecommendDrinkList();
        getBannerDrink();
    }

    @Override
    public void onResume() {
        super.onResume();
        getBannerDrink();
        Log.d("datdev1", "onResume:");

    }

    private void getBannerDrink(){
        drinkDAO.getFeatureDrink(new DrinkDAO.DrinkCallback() {
            @Override
            public void onDrinkLoaded(Drink drink) {
                view.showBannerDrink(drink);
            }
            @Override
            public void onError(Exception e) {
                Log.e("datdev1", "getBannerDrink-onError: " + e.getMessage());
            }
        });
    }

    private void getOneCategoryDrinkList(){
        categoryDAO.getAllCategorys(new CategoryDAO.CategoryListCallback() {
            @Override
            public void onCategoryListLoaded(List<Category> categoryList) {
                if(categoryList != null && !categoryList.isEmpty()){
                    Category category = categoryList.get(0);
                    loadOneCategoryDrinkList(category);
                    Log.d("datdev1", "onCategoryListLoaded: " + category.getUuid());
                }
            }
            @Override
            public void onError(Exception e) {

            }
        });
    }

    private void getHighestRateDrinks(){
        drinkDAO.getDrinksSortAndLimit(
                DrinkDAO.DRINK_FIELD.RATE, Query.Direction.DESCENDING,10,
                new DrinkDAO.DrinkListCallback(){

                    @Override
                    public void onDrinkListLoaded(List<Drink> drinks) {
                        view.showHighestRateDrinkList(drinks);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                }
                );
    }

    private void getLatestDrinkList(){
        drinkDAO.getDrinksSortAndLimit(
                DrinkDAO.DRINK_FIELD.NAME, Query.Direction.ASCENDING,10,
                new DrinkDAO.DrinkListCallback() {
            @Override
            public void onDrinkListLoaded(List<Drink> drinkList) {
                view.showLatestDrinkList(drinkList);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }


    private void loadOneCategoryDrinkList(Category category){
//        drinkDAO.getDrinksByCategoryId(category.getUuid(),
//                queryDocumentSnapshots -> {
//                    List<Drink> drinkList = new ArrayList<>();
//                    for (DocumentSnapshot drinkSnapshot : queryDocumentSnapshots.getDocuments()) {
//                        Drink drink = drinkSnapshot.toObject(Drink.class);
//                        drinkList.add(drink);
//                        if(drinkList.size() == 10) break;
//                    }
//                    Log.d("datdev1", "loadOneCategoryDrinkList: " + drinkList.size());
//                    view.showOneCategoryDrinkList(category, drinkList);
//                },
//                e -> {
//
//                });
        drinkDAO.getDrinksByCategoryIdWithLimit(category.getUuid(), 10,
                new DrinkDAO.DrinkListCallback()
                {
                    @Override
                    public void onDrinkListLoaded(List<Drink> drinkList) {
                        Log.d("datdev1", "loadOneCategoryDrinkList: " + drinkList.size());
                        view.showOneCategoryDrinkList(category, drinkList);
                    }

                    @Override
                    public void onError(Exception e) {
                    }
                }
        );
    }

    private void getRecommendDrinkList(){
        drinkDAO.getDrinksSortAndLimit(
                DrinkDAO.DRINK_FIELD.NAME, Query.Direction.DESCENDING, 13,
                new DrinkDAO.DrinkListCallback() {
            @Override
            public void onDrinkListLoaded(List<Drink> drinkList) {
                List<DrinkWithCategoryDTO> recommendDrinkList = new ArrayList<>();
                for (Drink drink : drinkList) {
                    categoryDAO.getCategory(drink.getCategoryId(),
                            new CategoryDAO.CategoryCallback() {
                                @Override
                                public void onCategoryLoaded(Category category) {
                                    if (category == null) {
                                        //Log.d("datdev1", "category == null-> drinkName: " + drink.getName() + " --- CateName: " + drink.getCategoryId());
                                        recommendDrinkList.add(new DrinkWithCategoryDTO(drink.getCategoryId(), drink));
                                    } else {
                                        //Log.d("datdev1", "onDrinkListLoaded: " + drink.getName() + " " + category.getName());
                                        recommendDrinkList.add(new DrinkWithCategoryDTO(category.getName(), drink));
                                    }
                                    view.showRecommendDrinkList(recommendDrinkList);
                                }

                                @Override
                                public void onError(Exception e) {

                                }
                            });
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }


    @Override
    public void refreshScreen() {
        getBannerDrink();
        getOneCategoryDrinkList();
        getHighestRateDrinks();
        getLatestDrinkList();
        getRecommendDrinkList();
    }

}
