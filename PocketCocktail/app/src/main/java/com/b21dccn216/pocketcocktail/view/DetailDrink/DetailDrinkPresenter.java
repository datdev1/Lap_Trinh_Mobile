package com.b21dccn216.pocketcocktail.view.DetailDrink;

import com.b21dccn216.pocketcocktail.dao.DrinkDAO;
import com.b21dccn216.pocketcocktail.dao.IngredientDAO;
import com.b21dccn216.pocketcocktail.dao.RecipeDAO;
import com.b21dccn216.pocketcocktail.model.Drink;
import com.b21dccn216.pocketcocktail.model.Ingredient;
import com.b21dccn216.pocketcocktail.model.Recipe;
import com.google.firebase.firestore.DocumentSnapshot;

public class DetailDrinkPresenter implements DetailDrinkContract.Presenter{
    private DetailDrinkContract.View view;
    private final DrinkDAO drinkDAO = new DrinkDAO();
    private final RecipeDAO recipeDAO = new RecipeDAO();
    private final IngredientDAO ingredientDAO = new IngredientDAO();
    @Override
    public void attachView(DetailDrinkContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void loadDrinkDetails(Drink drink) {
        if (view == null || drink == null) return;

        //Load drink info
        view.showDrinkDetail(drink);

        // Load instruction
        for (String instruction : drink.getInstruction().split("\n")) {
            view.showInstruction(instruction);
        }

        // Load ingredient
        recipeDAO.getRecipesByDrinkId(drink.getUuid(), recipeSnapshots -> {
            for (DocumentSnapshot doc : recipeSnapshots.getDocuments()) {
                Recipe recipe = doc.toObject(Recipe.class);
                if (recipe != null) {
                    ingredientDAO.getIngredient(recipe.getIngredientId(), ingredientSnapshot -> {
                        Ingredient ingredient = ingredientSnapshot.toObject(Ingredient.class);
                        if (ingredient != null) {
                            String line = ingredient.getName() + " (" + recipe.getAmount() + " " + ingredient.getUnit() + ")";
                            view.showIngredient(line);
                        }
                    }, e -> view.showError(e.getMessage()));
                }
            }
        }, e -> view.showError(e.getMessage()));
    }
}
