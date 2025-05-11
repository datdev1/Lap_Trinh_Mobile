package com.b21dccn216.pocketcocktail.test_database.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.dao.DrinkDAO;
import com.b21dccn216.pocketcocktail.dao.IngredientDAO;
import com.b21dccn216.pocketcocktail.model.Drink;
import com.b21dccn216.pocketcocktail.model.Ingredient;
import com.b21dccn216.pocketcocktail.model.Recipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeAdapter extends ArrayAdapter<Recipe> {
    private Context context;
    private List<Recipe> recipes;
    private DrinkDAO drinkDAO;
    private IngredientDAO ingredientDAO;
    private Map<String, String> drinkNames;
    private Map<String, String> ingredientNames;

    public RecipeAdapter(Context context, List<Recipe> recipes) {
        super(context, R.layout.test_database_item_recipe, recipes);
        this.context = context;
        this.recipes = recipes;
        this.drinkDAO = new DrinkDAO();
        this.ingredientDAO = new IngredientDAO();
        this.drinkNames = new HashMap<>();
        this.ingredientNames = new HashMap<>();
        loadDrinksAndIngredients();
    }

    private void loadDrinksAndIngredients() {
        // Load drinks
        drinkDAO.getAllDrinks(new DrinkDAO.DrinkListCallback() {
            @Override
            public void onDrinkListLoaded(List<Drink> drinks) {
                for (Drink drink : drinks) {
                    drinkNames.put(drink.getUuid(), drink.getName());
                }
                notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                // Handle error
            }
        });

        // Load ingredients
        ingredientDAO.getAllIngredients(new IngredientDAO.IngredientListCallback() {
            @Override
            public void onIngredientListLoaded(List<Ingredient> ingredients) {
                for (Ingredient ingredient : ingredients) {
                    ingredientNames.put(ingredient.getUuid(), ingredient.getName());
                }
                notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                // Handle error
            }
        });
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.test_database_item_recipe, parent, false);
        }

        Recipe recipe = recipes.get(position);

        TextView tvDrinkId = convertView.findViewById(R.id.tvDrinkId);
        TextView tvDrinkName = convertView.findViewById(R.id.tvDrinkName);
        TextView tvIngredientId = convertView.findViewById(R.id.tvIngredientId);
        TextView tvIngredientName = convertView.findViewById(R.id.tvIngredientName);
        TextView tvAmount = convertView.findViewById(R.id.tvAmount);

        String drinkName = drinkNames.getOrDefault(recipe.getDrinkId(), "Unknown Drink");
        String ingredientName = ingredientNames.getOrDefault(recipe.getIngredientId(), "Unknown Ingredient");

        tvDrinkId.setText("Drink ID: " + recipe.getDrinkId());
        tvDrinkName.setText("Drink Name: " + drinkName);
        tvIngredientId.setText("Ingredient ID: " + recipe.getIngredientId());
        tvIngredientName.setText("Ingredient Name: " + ingredientName);
        tvAmount.setText("Amount: " + recipe.getAmount());

        return convertView;
    }
} 