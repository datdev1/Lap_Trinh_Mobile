package com.b21dccn216.pocketcocktail.view.Main.fragment.Home;

import android.content.Context;
import android.util.Log;

import com.b21dccn216.pocketcocktail.base.BaseContract;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        getOneCategoryDrinkList();
        Log.d("datdev1", "onResume:");
    }


    private void getBannerDrink(){
        drinkDAO.getFeatureDrink(new DrinkDAO.DrinkCallback() {
            @Override
            public void onDrinkLoaded(Drink drink) {
                if(view == null) return;
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

                    CallBackLoadDrink callBackLoadDrink = new CallBackLoadDrink() {
                        @Override
                        public void onEmptyDrink() {
                            int index = new Random().nextInt(categoryList.size());
                            Category category = categoryList.get(index);
                            loadOneCategoryDrinkList(category, this);
                            Log.d("datdev1", "onCategoryListLoaded: " + category.getUuid());
                        }
                    };

                    callBackLoadDrink.onEmptyDrink();
                }
            }
            @Override
            public void onError(Exception e) {
                Log.e("datdev1", "getOneCategoryDrinkList-onError: " + e.getMessage());

            }
        });
    }

    private void getHighestRateDrinks(){
        drinkDAO.getDrinksSortAndLimit(
                DrinkDAO.DRINK_FIELD.RATE, Query.Direction.DESCENDING,10,
                new DrinkDAO.DrinkListCallback(){
                    @Override
                    public void onDrinkListLoaded(List<Drink> drinks) {
                        if(view == null) return;
                        view.showHighestRateDrinkList(drinks);
                    }
                    @Override
                    public void onError(Exception e) {
                        Log.e("datdev1", "getHighestRateDrinks-onError: " + e.getMessage());

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
                if(view == null) return;
                view.showLatestDrinkList(drinkList);
            }

            @Override
            public void onError(Exception e) {
                Log.e("datdev1", "getLatestDrinkList-onError: " + e.getMessage());

            }
        });
    }

    private void loadOneCategoryDrinkList(Category category, CallBackLoadDrink callBackLoadDrink){
        drinkDAO.getDrinksByCategoryIdWithLimit(category.getUuid(), 10,
                new DrinkDAO.DrinkListCallback()
                {
                    @Override
                    public void onDrinkListLoaded(List<Drink> drinkList) {
                        Log.d("datdev1", "loadOneCategoryDrinkList: " + drinkList.size());
                        if(view == null) return;
                        if(drinkList.isEmpty()){
                            callBackLoadDrink.onEmptyDrink();
                            return;
                        }
                        view.showOneCategoryDrinkList(category, drinkList);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("datdev1", "loadOneCategoryDrinkList-onError: " + e.getMessage());
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
                            if(view == null) return;
                            Log.d("datdev1", "2. onDrinkLoaded: " + drink.getUuid() + " Name: " + drink.getName());
                            list.add(new DrinkWithFavCount(drink, drinkCntFav.getCount()));
                            view.showRecommendDrinkList(list);
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e("datdev1", "getRecommendDrinkList-1onError: " + e.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("datdev1", "getRecommendDrinkList-2onError: " + e.getMessage());
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


    public interface CallBackLoadDrink{
        void onEmptyDrink();
    }
}
