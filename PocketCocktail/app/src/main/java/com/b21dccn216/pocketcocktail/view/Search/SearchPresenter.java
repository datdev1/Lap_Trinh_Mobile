package com.b21dccn216.pocketcocktail.view.Search;

import com.b21dccn216.pocketcocktail.base.BasePresenter;
import com.b21dccn216.pocketcocktail.dao.DrinkDAO;
import com.b21dccn216.pocketcocktail.dao.IngredientDAO;
import com.b21dccn216.pocketcocktail.dao.RecipeDAO;
import com.b21dccn216.pocketcocktail.model.Category;
import com.b21dccn216.pocketcocktail.model.Drink;
import com.b21dccn216.pocketcocktail.model.Ingredient;
import com.b21dccn216.pocketcocktail.model.Recipe;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SearchPresenter extends BasePresenter<SearchContract.View>
    implements SearchContract.Presenter{
    private final DrinkDAO drinkDAO;
    private final IngredientDAO ingredientDAO;
    private final RecipeDAO recipeDAO;

    public SearchPresenter() {
        this.drinkDAO = new DrinkDAO();
        this.ingredientDAO = new IngredientDAO();
        this.recipeDAO = new RecipeDAO();
    }

    @Override
    public void loadDrinksByCategory(String categoryId) {
        drinkDAO.getDrinksByCategoryId(categoryId, new DrinkDAO.DrinkListCallback() {
            @Override
            public void onDrinkListLoaded(List<Drink> drinks) {
                view.hideLoading();
                view.showDrinks(drinks);
            }

            @Override
            public void onError(Exception e) {
                view.hideLoading();
                view.showError("Failed to load drinks: " + e.getMessage());
            }
        });
    }

    @Override
    public void loadIngredients() {
        ingredientDAO.getAllIngredients(new IngredientDAO.IngredientListCallback() {
            @Override
            public void onIngredientListLoaded(List<Ingredient> ingredients) {
                view.showIngredients(ingredients);
            }

            @Override
            public void onError(Exception e) {
                view.showError("Failed to load ingredients: " + e.getMessage());
            }
        });
    }

    @Override
    public void searchDrinks(String categoryId, String query, List<String> ingredientIds) {
        drinkDAO.getDrinksByCategoryId(categoryId, new DrinkDAO.DrinkListCallback() {
            @Override
            public void onDrinkListLoaded(List<Drink> drinks) {
                List<Drink> filteredDrinks = new ArrayList<>();

                for (Drink drink : drinks) {
                    List<String> listIngredient = new ArrayList<>();
                    recipeDAO.getRecipesByDrinkId(drink.getUuid(), new RecipeDAO.RecipeListCallback() {
                        @Override
                        public void onRecipeListLoaded(List<Recipe> recipes) {
                            for (Recipe recipe : recipes) {
                                ingredientDAO.getIngredient(recipe.getIngredientId(), new IngredientDAO.IngredientCallback() {
                                    @Override
                                    public void onIngredientLoaded(Ingredient ingredient) {
                                        listIngredient.add(ingredient.getName()) ;
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                    }

                                });
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            view.showError(e.getMessage());
                        }
                    });
                    boolean matchesQuery = query == null || query.isEmpty()
                            || drink.getName().toLowerCase().contains(query.toLowerCase());

                    boolean matchesIngredients = ingredientIds == null || ingredientIds.isEmpty()
                            || (!listIngredient.isEmpty() && new HashSet<>(listIngredient).containsAll(ingredientIds));

                    if (matchesQuery && matchesIngredients) {
                        filteredDrinks.add(drink);
                    }
                }

                view.hideLoading();
                view.showDrinks(filteredDrinks);
            }

            @Override
            public void onError(Exception e) {
                view.hideLoading();
                view.showError("Search failed: " + e.getMessage());
            }
        });
    }

    @Override
    public void searchIngredients(String query) {
        ingredientDAO.getAllIngredients(new IngredientDAO.IngredientListCallback() {
            @Override
            public void onIngredientListLoaded(List<Ingredient> ingredients) {
                List<Ingredient> filtered = new ArrayList<>();
                for (Ingredient ingredient : ingredients) {
                    if (ingredient.getName().toLowerCase().contains(query.toLowerCase())) {
                        filtered.add(ingredient);
                    }
                }
                view.showIngredients(filtered);
            }

            @Override
            public void onError(Exception e) {
                view.showError("Ingredient search failed: " + e.getMessage());
            }
        });
    }

    @Override
    public void updateDrinkList(Category choosenCategory, List<Ingredient> ingredientList, String searchName) {
        if(choosenCategory != null && searchName != null){
            if(!ingredientList.isEmpty()){

            }else{
                //TODO:: Change to get drink by categoryId and search name
                drinkDAO.getDrinksByCategoryId(choosenCategory.getUuid(), new DrinkDAO.DrinkListCallback() {
                    @Override
                    public void onDrinkListLoaded(List<Drink> drinks) {
                        view.hideLoading();
                        view.showDrinks(drinks);
                    }

                    @Override
                    public void onError(Exception e) {
                        view.hideLoading();
                        view.showError("Failed to load drinks: " + e.getMessage());
                    }
                });
            }
        }else{
            if(!ingredientList.isEmpty()){

            }else{

            }
        }
    }
}
