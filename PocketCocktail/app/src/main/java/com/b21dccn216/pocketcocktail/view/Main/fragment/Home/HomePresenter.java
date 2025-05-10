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
        getHighestRateDrinks();
        getRecommendDrinkList();

        getLatestDrinkList();
        getBannerDrink();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void getBannerDrink(){

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
        drinkDAO.getAllDrinks(new DrinkDAO.DrinkListCallback() {
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
        drinkDAO.getDrinksByCategoryId(category.getUuid(),
                queryDocumentSnapshots -> {
                    List<Drink> drinkList = new ArrayList<>();
                    for (DocumentSnapshot drinkSnapshot : queryDocumentSnapshots.getDocuments()) {
                        Drink drink = drinkSnapshot.toObject(Drink.class);
                        drinkList.add(drink);
                        if(drinkList.size() == 10) break;
                    }
                    Log.d("datdev1", "loadOneCategoryDrinkList: " + drinkList.size());
                    view.showOneCategoryDrinkList(category.getName(), drinkList);
                },
                e -> {

                });
    }

    private void getRecommendDrinkList(){
        drinkDAO.getAllDrinks(new DrinkDAO.DrinkListCallback() {
            @Override
            public void onDrinkListLoaded(List<Drink> drinkList) {
                List<DrinkWithCategoryDTO> recommendDrinkList = new ArrayList<>();

                for (Drink drink : drinkList) {
                    categoryDAO.getCategory(drink.getCategoryId(),
                            documentSnapshot -> {
                                Category category = documentSnapshot.toObject(Category.class);
                                if(category == null){
                                    Log.d("datdev1", "category == null-> drinkName: " + drink.getName() + " --- CateName: " + drink.getCategoryId());
                                    recommendDrinkList.add(new DrinkWithCategoryDTO(drink.getCategoryId(), drink));
                                }else{
                                    Log.d("datdev1", "onDrinkListLoaded: " + drink.getName() + " " + category.getName());
                                    recommendDrinkList.add(new DrinkWithCategoryDTO(category.getName(), drink));
                                }
                                view.showRecommendDrinkList(recommendDrinkList);
                            },
                            e -> {
                                Log.d("datdev1", "categoryDAO.getCategory(drink.getCategoryId() error : " + e.getMessage());
                            });
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }




}
