package com.b21dccn216.pocketcocktail.view.Main.fragment.Home;

import android.content.Context;
import android.util.Log;

import com.b21dccn216.pocketcocktail.base.BasePresenter;
import com.b21dccn216.pocketcocktail.dao.CategoryDAO;
import com.b21dccn216.pocketcocktail.dao.DrinkCntFavDAO;
import com.b21dccn216.pocketcocktail.dao.DrinkDAO;
import com.b21dccn216.pocketcocktail.helper.DialogHelper;
import com.b21dccn216.pocketcocktail.helper.HelperDialog;
import com.b21dccn216.pocketcocktail.model.Category;
import com.b21dccn216.pocketcocktail.model.Drink;
import com.b21dccn216.pocketcocktail.model.DrinkCntFav;
import com.b21dccn216.pocketcocktail.view.Main.model.DrinkWithFavCount;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class HomePresenter
    extends BasePresenter<HomeContract.View>
    implements HomeContract.Presenter{

    private DrinkDAO drinkDAO = new DrinkDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();
    private DrinkCntFavDAO drinkCntFavDAO = new DrinkCntFavDAO();


    public HomePresenter() {
        super();

    }

    @Override
    public void onCreate() {
        super.onCreate();

        refreshScreen();
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
        //
        drinkDAO.getDrinksSortAndLimit(
                DrinkDAO.DRINK_FIELD.CREATED_AT, Query.Direction.DESCENDING,10,
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
        drinkCntFavDAO.getTopDrinkCntFavourite(12, new DrinkCntFavDAO.DrinkCntFavListCallback(){
            @Override
            public void onDrinkCntFavListLoaded(List<DrinkCntFav> drinkCntFavs) {
                List<DrinkWithFavCount> list = new ArrayList<>();
                for (DrinkCntFav drinkCntFav : drinkCntFavs) {
                    Log.d("datdev1", "1. getRecommendDrinkList: " + drinkCntFav.getDrinkId());
                    drinkDAO.getDrink(drinkCntFav.getDrinkId(), new DrinkDAO.DrinkCallback() {
                        @Override
                        public void onDrinkLoaded(Drink drink) {
                            Log.d("datdev1", "2. onDrinkLoaded: " + drink.getUuid() + " Name: " + drink.getName());
                            list.add(new DrinkWithFavCount(drink, drinkCntFav.getCount()));
                            view.showRecommendDrinkList(list);
                        }

                        @Override
                        public void onError(Exception e) {
                            DialogHelper.showAlertDialog(((Context) view).getApplicationContext(),
                                    "Error", e.getMessage(), HelperDialog.DialogType.ERROR);
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
