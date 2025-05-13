package com.b21dccn216.pocketcocktail.view.Search;

import android.util.Log;

import com.b21dccn216.pocketcocktail.base.BasePresenter;
import com.b21dccn216.pocketcocktail.dao.DrinkDAO;
import com.b21dccn216.pocketcocktail.dao.IngredientDAO;
import com.b21dccn216.pocketcocktail.dao.RecipeDAO;
import com.b21dccn216.pocketcocktail.model.Category;
import com.b21dccn216.pocketcocktail.model.Drink;
import com.b21dccn216.pocketcocktail.model.Ingredient;
import com.b21dccn216.pocketcocktail.model.Recipe;


import java.util.ArrayList;
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


    // Load all drinks
    @Override
    public void loadDrinks() {
        drinkDAO.getAllDrinks(new DrinkDAO.DrinkListCallback() {
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

    // Load drinks by category
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

    // Load ingredients
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

    // Search drinks (4 case)
    @Override
    public void searchDrinks(String categoryId, String query, List<String> ingredientIds) {
        view.showLoading();

        // Tạo callback chung
        DrinkDAO.DrinkListCallback callback = new DrinkDAO.DrinkListCallback() {
            @Override
            public void onDrinkListLoaded(List<Drink> drinks) {
                if (drinks == null || drinks.isEmpty()) {
                    view.hideLoading();
                    view.showDrinks(new ArrayList<>());
                    return;
                }

                // Not ingredient
                if (ingredientIds == null || ingredientIds.isEmpty()) {
                    List<Drink> filteredDrinks = filterDrinksByQuery(drinks, query);
                    view.hideLoading();
                    view.showDrinks(filteredDrinks);
                    return;
                }

                // Have ingredient
                filterDrinksWithIngredients(drinks, query, ingredientIds);
            }

            @Override
            public void onError(Exception e) {
                view.hideLoading();
                view.showError("Search failed: " + e.getMessage());
            }
        };

        // Search by category or not
        if (categoryId != null && !categoryId.isEmpty()) {
            drinkDAO.getDrinksByCategoryId(categoryId, callback);
        } else {
            drinkDAO.getAllDrinks(callback);
        }
    }

    private List<Drink> filterDrinksByQuery(List<Drink> drinks, String query) {
        if (query == null || query.isEmpty()) {
            return drinks;
        }

        List<Drink> filtered = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        for (Drink drink : drinks) {
            if (drink.getName().toLowerCase().contains(lowerQuery)) {
                filtered.add(drink);
            }
        }
        return filtered;
    }

    private void filterDrinksWithIngredients(List<Drink> drinks, String query, List<String> ingredientIds) {
        List<Drink> filteredDrinks = new ArrayList<>();
        int[] pendingCount = {drinks.size()};

        for (Drink drink : drinks) {
            recipeDAO.getRecipesByDrinkId(drink.getUuid(), new RecipeDAO.RecipeListCallback() {
                @Override
                public void onRecipeListLoaded(List<Recipe> recipes) {
                    List<String> drinkIngredientIds = extractIngredientIds(recipes);
                    boolean matches = checkMatch(drink, query, ingredientIds, drinkIngredientIds);

                    if (matches) {
                        synchronized (filteredDrinks) {
                            filteredDrinks.add(drink);
                        }
                    }
                    checkCompletion(pendingCount, filteredDrinks);
                }

                @Override
                public void onError(Exception e) {
                    checkCompletion(pendingCount, filteredDrinks);
                }
            });
        }
    }

    private List<String> extractIngredientIds(List<Recipe> recipes) {
        List<String> ids = new ArrayList<>();
        if (recipes == null || recipes.isEmpty()) {
            return ids;
        }

        for (Recipe recipe : recipes) {
            ids.add(recipe.getIngredientId());
        }
        return ids;
    }

    private boolean checkMatch(Drink drink, String query, List<String> requiredIds, List<String> drinkIngredientIds) {
        boolean matchesQuery = query == null || query.isEmpty() ||
                drink.getName().toLowerCase().contains(query.toLowerCase());

        boolean matchesIngredients = requiredIds == null || requiredIds.isEmpty() ||
                drinkIngredientIds.containsAll(requiredIds);

        return matchesQuery && matchesIngredients;
    }

    private void checkCompletion(int[] pendingCount, List<Drink> filteredDrinks) {
        synchronized (pendingCount) {
            pendingCount[0]--;
            if (pendingCount[0] == 0) {
                view.hideLoading();
                view.showDrinks(filteredDrinks);
            }
        }
    }



    // Search ingredient
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
}
