package com.b21dccn216.pocketcocktail.test_database.fragment;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.dao.DrinkDAO;
import com.b21dccn216.pocketcocktail.dao.IngredientDAO;
import com.b21dccn216.pocketcocktail.dao.RecipeDAO;
import com.b21dccn216.pocketcocktail.model.Drink;
import com.b21dccn216.pocketcocktail.model.Ingredient;
import com.b21dccn216.pocketcocktail.model.Recipe;
import com.b21dccn216.pocketcocktail.test_database.adapter.RecipeAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecipeFragment extends BaseModelFragment {
    private EditText etUuid, etCreatedAt, etUpdatedAt, etAmount;
    private Spinner spinnerDrink, spinnerIngredient;
    private Button btnSave, btnUpdate, btnDelete;
    private ListView lvRecipes;
    private RecipeAdapter adapter;
    private List<Recipe> recipes;
    private Recipe selectedRecipe;
    private RecipeDAO recipeDAO;
    private DrinkDAO drinkDAO;
    private IngredientDAO ingredientDAO;
    private SimpleDateFormat dateFormat;
    private List<Drink> drinks;
    private List<Ingredient> ingredients;

    @Override
    protected int getLayoutId() {
        return R.layout.test_database_fragment_recipe;
    }

    @Override
    protected void initViews() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        
        etUuid = rootView.findViewById(R.id.etUuid);
        etCreatedAt = rootView.findViewById(R.id.etCreatedAt);
        etUpdatedAt = rootView.findViewById(R.id.etUpdatedAt);
        etAmount = rootView.findViewById(R.id.etAmount);
        spinnerDrink = rootView.findViewById(R.id.spinnerDrink);
        spinnerIngredient = rootView.findViewById(R.id.spinnerIngredient);
        
        btnSave = rootView.findViewById(R.id.btnSave);
        btnUpdate = rootView.findViewById(R.id.btnUpdate);
        btnDelete = rootView.findViewById(R.id.btnDelete);
        lvRecipes = rootView.findViewById(R.id.lvRecipes);

        recipes = new ArrayList<>();
        drinks = new ArrayList<>();
        ingredients = new ArrayList<>();
        
        adapter = new RecipeAdapter(getContext(), recipes);
        lvRecipes.setAdapter(adapter);
        
        recipeDAO = new RecipeDAO();
        drinkDAO = new DrinkDAO();
        ingredientDAO = new IngredientDAO();

        setupSpinners();
        setupListeners();
        loadData();
    }

    private void setupSpinners() {
        // Setup Drink spinner
        List<String> drinkItems = new ArrayList<>();
        drinkItems.add(""); // Add empty option
        ArrayAdapter<String> drinkAdapter = new ArrayAdapter<>(
            getContext(),
            android.R.layout.simple_spinner_item,
            drinkItems
        );
        drinkAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDrink.setAdapter(drinkAdapter);

        // Setup Ingredient spinner
        List<String> ingredientItems = new ArrayList<>();
        ingredientItems.add(""); // Add empty option
        ArrayAdapter<String> ingredientAdapter = new ArrayAdapter<>(
            getContext(),
            android.R.layout.simple_spinner_item,
            ingredientItems
        );
        ingredientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIngredient.setAdapter(ingredientAdapter);

        // Load drinks and ingredients
        loadDrinks();
        loadIngredients();
    }

    private void loadDrinks() {
        drinkDAO.getAllDrinks(new DrinkDAO.DrinkListCallback() {
            @Override
            public void onDrinkListLoaded(List<Drink> drinkList) {
                drinks.clear();
                drinks.addAll(drinkList);
                
                List<String> drinkItems = new ArrayList<>();
                drinkItems.add(""); // Add empty option
                for (Drink drink : drinks) {
                    drinkItems.add(drink.getName() + " (" + drink.getUuid() + ")");
                }
                
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerDrink.getAdapter();
                adapter.clear();
                adapter.addAll(drinkItems);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                showToast("Error loading drinks: " + e.getMessage());
            }
        });
    }

    private void loadIngredients() {
        ingredientDAO.getAllIngredients(new IngredientDAO.IngredientListCallback() {
            @Override
            public void onIngredientListLoaded(List<Ingredient> ingredientList) {
                ingredients.clear();
                ingredients.addAll(ingredientList);
                
                List<String> ingredientItems = new ArrayList<>();
                ingredientItems.add(""); // Add empty option
                for (Ingredient ingredient : ingredients) {
                    ingredientItems.add(ingredient.getName() + " (" + ingredient.getUuid() + ")");
                }
                
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerIngredient.getAdapter();
                adapter.clear();
                adapter.addAll(ingredientItems);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                showToast("Error loading ingredients: " + e.getMessage());
            }
        });
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
        etUuid.setText("");
        etCreatedAt.setText("");
        etUpdatedAt.setText("");
        etAmount.setText("");
        spinnerDrink.setSelection(0);
        spinnerIngredient.setSelection(0);
        selectedRecipe = null;
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    @Override
    protected void fillInputs(Object item) {
        if (item instanceof Recipe) {
            Recipe recipe = (Recipe) item;
            etUuid.setText(recipe.getUuid());
            etCreatedAt.setText(dateFormat.format(recipe.getCreatedAt()));
            etUpdatedAt.setText(dateFormat.format(recipe.getUpdatedAt()));
            etAmount.setText(String.valueOf(recipe.getAmount()));
            
            // Set drink spinner
            String drinkId = recipe.getDrinkId();
            boolean drinkFound = false;
            for (int i = 0; i < drinks.size(); i++) {
                if (drinks.get(i).getUuid().equals(drinkId)) {
                    spinnerDrink.setSelection(i + 1); // +1 because of empty option
                    drinkFound = true;
                    break;
                }
            }
            if (!drinkFound) {
                spinnerDrink.setSelection(0);
            }
            
            // Set ingredient spinner
            String ingredientId = recipe.getIngredientId();
            boolean ingredientFound = false;
            for (int i = 0; i < ingredients.size(); i++) {
                if (ingredients.get(i).getUuid().equals(ingredientId)) {
                    spinnerIngredient.setSelection(i + 1); // +1 because of empty option
                    ingredientFound = true;
                    break;
                }
            }
            if (!ingredientFound) {
                spinnerIngredient.setSelection(0);
            }
        }
    }

    @Override
    protected void saveItem() {
        int drinkPosition = spinnerDrink.getSelectedItemPosition();
        int ingredientPosition = spinnerIngredient.getSelectedItemPosition();
        String amountStr = etAmount.getText().toString();

        if (drinkPosition <= 0 || ingredientPosition <= 0 || amountStr.isEmpty()) {
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

        String drinkId = drinks.get(drinkPosition - 1).getUuid();
        String ingredientId = ingredients.get(ingredientPosition - 1).getUuid();

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

        int drinkPosition = spinnerDrink.getSelectedItemPosition();
        int ingredientPosition = spinnerIngredient.getSelectedItemPosition();
        String amountStr = etAmount.getText().toString();

        if (drinkPosition <= 0 || ingredientPosition <= 0 || amountStr.isEmpty()) {
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

        String drinkId = drinks.get(drinkPosition - 1).getUuid();
        String ingredientId = ingredients.get(ingredientPosition - 1).getUuid();

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