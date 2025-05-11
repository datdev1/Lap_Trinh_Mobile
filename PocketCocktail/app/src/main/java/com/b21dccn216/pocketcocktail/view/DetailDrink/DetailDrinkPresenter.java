package com.b21dccn216.pocketcocktail.view.DetailDrink;

import com.b21dccn216.pocketcocktail.base.BasePresenter;
import com.b21dccn216.pocketcocktail.dao.DrinkDAO;
import com.b21dccn216.pocketcocktail.dao.FavoriteDAO;
import com.b21dccn216.pocketcocktail.dao.IngredientDAO;
import com.b21dccn216.pocketcocktail.dao.RecipeDAO;
import com.b21dccn216.pocketcocktail.helper.SessionManager;
import com.b21dccn216.pocketcocktail.model.Drink;
import com.b21dccn216.pocketcocktail.model.Favorite;
import com.b21dccn216.pocketcocktail.model.Ingredient;
import com.b21dccn216.pocketcocktail.model.Recipe;
import com.google.firebase.firestore.DocumentSnapshot;

public class DetailDrinkPresenter extends BasePresenter<DetailDrinkContract.View> implements DetailDrinkContract.Presenter {

    private final FavoriteDAO favoriteDAO;
    private final DrinkDAO drinkDAO;
    private final RecipeDAO recipeDAO;
    private final IngredientDAO ingredientDAO;

    private boolean isFavorite = false;
    private Favorite currentFavorite;

    private final String currentUserId; // TODO: Lấy từ Firebase Auth sau

    public DetailDrinkPresenter() {
        favoriteDAO = new FavoriteDAO();
        drinkDAO = new DrinkDAO();
        recipeDAO = new RecipeDAO();
        ingredientDAO = new IngredientDAO();
        currentUserId = String.valueOf(SessionManager.getInstance().getUser());
    }


    @Override
    public void loadDrinkDetails(Drink drink) {
        if (view == null || drink == null) return;

        // Load drink info
        view.showDrinkDetail(drink);

        // Load instruction
        for (String instruction : drink.getInstruction().split("\n")) {
            instruction = instruction.trim();
            if (!instruction.isEmpty()) {
                view.showInstruction(instruction);
            }
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

    @Override
    public void checkFavorite(String drinkId) {
        favoriteDAO.getFavoritesByUserId(currentUserId, querySnapshot -> {
            isFavorite = false;
            currentFavorite = null;
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                Favorite favorite = doc.toObject(Favorite.class);
                if (favorite != null && favorite.getDrinkId().equals(drinkId)) {
                    isFavorite = true;
                    currentFavorite = favorite;
                    break;
                }
            }
            if (view != null) {
                view.updateFavoriteIcon(isFavorite);
            }
        }, e -> {
            if (view != null) view.showError(e.getMessage());
        });
    }

    @Override
    public void toggleFavorite(Drink drink) {
        if (drink == null || view == null) return;

        if (isFavorite && currentFavorite != null) {
            favoriteDAO.deleteFavorite(currentFavorite.getUuid(), unused -> {
                isFavorite = false;
                currentFavorite = null;
                view.updateFavoriteIcon(false);
            }, e -> view.showError(e.getMessage()));
        } else {
            Favorite favorite = new Favorite();
            favorite.setUserId(currentUserId);
            favorite.setDrinkId(drink.getUuid());
            favorite.generateUUID();

            favoriteDAO.addFavorite(favorite, unused -> {
                isFavorite = true;
                currentFavorite = favorite;
                view.updateFavoriteIcon(true);
            }, e -> view.showError(e.getMessage()));
        }
    }

    @Override
    public void shareDrink(Drink drink) {
        if (drink == null || view == null) return;

        String content = "Check out this drink: " + drink.getName() + "\n" + drink.getImage();
        view.showShareIntent(content);
    }
}
