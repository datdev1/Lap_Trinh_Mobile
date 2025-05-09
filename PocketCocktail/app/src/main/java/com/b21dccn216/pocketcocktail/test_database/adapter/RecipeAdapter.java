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
import com.b21dccn216.pocketcocktail.model.Recipe;

import java.util.List;

public class RecipeAdapter extends ArrayAdapter<Recipe> {
    private Context context;
    private List<Recipe> recipes;

    public RecipeAdapter(Context context, List<Recipe> recipes) {
        super(context, R.layout.test_database_item_recipe, recipes);
        this.context = context;
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.test_database_item_recipe, parent, false);
        }

        Recipe recipe = recipes.get(position);

        TextView tvDrinkId = convertView.findViewById(R.id.tvDrinkId);
        TextView tvIngredientId = convertView.findViewById(R.id.tvIngredientId);
        TextView tvAmount = convertView.findViewById(R.id.tvAmount);

        tvDrinkId.setText("Drink ID: " + recipe.getDrinkId());
        tvIngredientId.setText("Ingredient ID: " + recipe.getIngredientId());
        tvAmount.setText("Amount: " + recipe.getAmount());

        return convertView;
    }
} 