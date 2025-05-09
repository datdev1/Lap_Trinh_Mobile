package com.b21dccn216.pocketcocktail.test_database.fragment;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.dao.RecipeDAO;
import com.b21dccn216.pocketcocktail.model.Recipe;
import com.b21dccn216.pocketcocktail.test_database.adapter.RecipeAdapter;

import java.util.ArrayList;
import java.util.List;

public class RecipeFragment extends BaseModelFragment {
    private EditText etDrinkId, etIngredientId, etAmount;
    private Button btnSave, btnUpdate, btnDelete;
    private ListView lvRecipes;
    private RecipeAdapter adapter;
    private List<Recipe> recipes;
    private Recipe selectedRecipe;
    private RecipeDAO recipeDAO;

    @Override
    protected int getLayoutId() {
        return R.layout.test_database_fragment_recipe;
    }

    @Override
    protected void initViews() {
        etDrinkId = rootView.findViewById(R.id.etDrinkId);
        etIngredientId = rootView.findViewById(R.id.etIngredientId);
        etAmount = rootView.findViewById(R.id.etAmount);
        btnSave = rootView.findViewById(R.id.btnSave);
        btnUpdate = rootView.findViewById(R.id.btnUpdate);
        btnDelete = rootView.findViewById(R.id.btnDelete);
        lvRecipes = rootView.findViewById(R.id.lvRecipes);

        recipes = new ArrayList<>();
        adapter = new RecipeAdapter(getContext(), recipes);
        lvRecipes.setAdapter(adapter);
        recipeDAO = new RecipeDAO();

        setupListeners();
        loadData();
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveItem());
        btnUpdate.setOnClickListener(v -> updateItem());
        btnDelete.setOnClickListener(v -> deleteItem());

        lvRecipes.setOnItemClickListener((parent, view, position, id) -> {
            selectedRecipe = recipes.get(position);
            fillInputs(selectedRecipe);
            btnUpdate.setEnabled(true);
            btnDelete.setEnabled(true);
        });
    }

    @Override
    protected void loadData() {
        recipeDAO.getAllRecipes(new RecipeDAO.RecipeListCallback() {
            @Override
            public void onRecipeListLoaded(List<Recipe> recipeList) {
                recipes.clear();
                recipes.addAll(recipeList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                showToast("Error loading recipes: " + e.getMessage());
            }
        });
    }

    @Override
    protected void clearInputs() {
        etDrinkId.setText("");
        etIngredientId.setText("");
        etAmount.setText("");
        selectedRecipe = null;
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    @Override
    protected void fillInputs(Object item) {
        if (item instanceof Recipe) {
            Recipe recipe = (Recipe) item;
            etDrinkId.setText(recipe.getDrinkId());
            etIngredientId.setText(recipe.getIngredientId());
            etAmount.setText(String.valueOf(recipe.getAmount()));
        }
    }

    @Override
    protected void saveItem() {
        String drinkId = etDrinkId.getText().toString();
        String ingredientId = etIngredientId.getText().toString();
        String amountStr = etAmount.getText().toString();

        if (drinkId.isEmpty() || ingredientId.isEmpty() || amountStr.isEmpty()) {
            showToast("Please fill all required fields");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            showToast("Invalid amount value");
            return;
        }

        Recipe recipe = new Recipe(drinkId, ingredientId, amount);
        recipe.generateUUID();

        recipeDAO.addRecipe(recipe,
                aVoid -> {
                    showToast("Recipe added successfully");
                    clearInputs();
                    loadData();
                },
                e -> showToast("Error adding recipe: " + e.getMessage()));
    }

    @Override
    protected void updateItem() {
        if (selectedRecipe == null) {
            showToast("Please select a recipe first");
            return;
        }

        String drinkId = etDrinkId.getText().toString();
        String ingredientId = etIngredientId.getText().toString();
        String amountStr = etAmount.getText().toString();

        if (drinkId.isEmpty() || ingredientId.isEmpty() || amountStr.isEmpty()) {
            showToast("Please fill all required fields");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            showToast("Invalid amount value");
            return;
        }

        selectedRecipe.setDrinkId(drinkId);
        selectedRecipe.setIngredientId(ingredientId);
        selectedRecipe.setAmount(amount);

        recipeDAO.updateRecipe(selectedRecipe,
                aVoid -> {
                    showToast("Recipe updated successfully");
                    clearInputs();
                    loadData();
                },
                e -> showToast("Error updating recipe: " + e.getMessage()));
    }

    @Override
    protected void deleteItem() {
        if (selectedRecipe == null) {
            showToast("Please select a recipe first");
            return;
        }

        recipeDAO.deleteRecipe(selectedRecipe.getUuid(),
                aVoid -> {
                    showToast("Recipe deleted successfully");
                    clearInputs();
                    loadData();
                },
                e -> showToast("Error deleting recipe: " + e.getMessage()));
    }
} 