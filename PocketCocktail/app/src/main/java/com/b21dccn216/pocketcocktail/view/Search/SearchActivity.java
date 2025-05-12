package com.b21dccn216.pocketcocktail.view.Search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.recyclerview.widget.GridLayoutManager;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.base.BaseAppCompatActivity;
import com.b21dccn216.pocketcocktail.databinding.ActivitySearchBinding;
import com.b21dccn216.pocketcocktail.model.Category;
import com.b21dccn216.pocketcocktail.model.Drink;
import com.b21dccn216.pocketcocktail.model.Ingredient;
import com.b21dccn216.pocketcocktail.view.Search.adapter.DrinkAdapter;
import com.b21dccn216.pocketcocktail.view.Search.adapter.IngredientAdapter;
import com.b21dccn216.pocketcocktail.view.DetailDrink.DetailDrinkActivity;
import com.b21dccn216.pocketcocktail.view.Search.helper.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseAppCompatActivity<SearchContract.View, SearchContract.Presenter>
    implements SearchContract.View{

    public static final String EXTRA_CATEGORY_OBJECT = "category_object";
    public static final String EXTRA_INGREDIENT_OBJECT = "ingredient_object";
    public static final String EXTRA_DRINK_OBJECT = "drink_id";
    public static final String SORT_FIELD = "search_field";
    public static final String SORT_ORDER = "sort_order";


    private ActivitySearchBinding binding;;
    private DrinkAdapter drinkAdapter;
    private IngredientAdapter ingredientAdapter;

    private Category category;

    private Ingredient ingredient;

    private List<Ingredient> choosenIngredientList = new ArrayList<>();
    private String searchName = "";


    @Override
    protected SearchContract.Presenter createPresenter() {
        return new SearchPresenter();
    }

    @Override
    protected SearchContract.View getView() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        presenter = createPresenter();
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setUpDrinkRecycler();
        setUpIngredientRecycler();

        category = (Category) getIntent().getSerializableExtra(EXTRA_CATEGORY_OBJECT);
        ingredient = (Ingredient) getIntent().getSerializableExtra(EXTRA_INGREDIENT_OBJECT);
        presenter.loadIngredients();
        if (category != null) {
            Log.e("Category", category.toString());
            presenter.loadDrinksByCategory(category.getUuid());
        }
        if(category == null && ingredient == null){
            presenter.loadDrinks();
        }


        //Search drink
        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();

                List<String> selectedIngredientIds = ingredientAdapter.getSelectedIngredientIds();
                if (category != null) {
                    presenter.searchDrinks(category.getUuid(), query, selectedIngredientIds);
                } else {
                    presenter.searchDrinks(null, query, selectedIngredientIds);
                }
                binding.clearButton.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.clearButton.setOnClickListener(v -> {
            binding.searchEditText.setText("");
            if (category != null) {
                presenter.loadDrinksByCategory(category.getUuid());
            } else {
                presenter.loadDrinks();
            }
            // Hide keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(binding.searchEditText.getWindowToken(), 0);
            }
        });


        //Search ingredient
        binding.ingredientSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (!query.isEmpty()) {
                    presenter.searchIngredients(query);
//                    binding.clearIngredientSearch.setVisibility(View.VISIBLE);
                } else {
                    presenter.loadIngredients();
//                    binding.clearIngredientSearch.setVisibility(View.GONE);
                }
                binding.clearIngredientSearch.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.clearIngredientSearch.setOnClickListener(v -> {
            binding.ingredientSearch.setText("");
            presenter.loadIngredients();
            // Hide keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(binding.ingredientSearch.getWindowToken(), 0);
            }
        });
    }

    private void setUpDrinkRecycler(){
        drinkAdapter = new DrinkAdapter(this, drink -> {
            Intent intent = new Intent(this, DetailDrinkActivity.class);
            intent.putExtra(EXTRA_DRINK_OBJECT, drink);
            startActivity(intent);
        });

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        binding.drinksRecyclerView.setLayoutManager(layoutManager);
        binding.drinksRecyclerView.setAdapter(drinkAdapter);

        int horizontalSpacing = getResources().getDimensionPixelSize(R.dimen.recycler_spacing);
        int verticalSpacing = getResources().getDimensionPixelSize(R.dimen.recycler_vertical_spacing);

        binding.drinksRecyclerView.addItemDecoration(
                new GridSpacingItemDecoration(3, horizontalSpacing, verticalSpacing, true)
        );

        binding.drinksRecyclerView.setClipToPadding(false);
        binding.drinksRecyclerView.setPadding(0, 0, verticalSpacing, verticalSpacing);
    }

    private void updateDrinkList(){
//        presenter.updateDrinkList(choosenCategory, choosenIngredientList, searchName);
    }

    private void setUpIngredientRecycler() {
        ingredientAdapter = new IngredientAdapter(this, selectedIngredients -> {
            String query = binding.searchEditText.getText().toString().trim();

            if (category != null) {
                presenter.searchDrinks(category.getUuid(), query, selectedIngredients);
            } else {
                presenter.searchDrinks(null, query, selectedIngredients);
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        binding.ingredientsRecyclerView.setLayoutManager(layoutManager);
        binding.ingredientsRecyclerView.setAdapter(ingredientAdapter);
        binding.ingredientsRecyclerView.setItemAnimator(null);

        int horizontalSpacing = getResources().getDimensionPixelSize(R.dimen.recycler_spacing);
        int verticalSpacing = getResources().getDimensionPixelSize(R.dimen.recycler_vertical_spacing);
        binding.ingredientsRecyclerView.addItemDecoration(
                new GridSpacingItemDecoration(3, horizontalSpacing, verticalSpacing, true)
        );
        binding.ingredientsRecyclerView.setClipToPadding(false);
        binding.ingredientsRecyclerView.setPadding(0, 0, verticalSpacing, verticalSpacing);
    }


    @Override
    public void showMessage(String message) {

    }

    @Override
    public void showDrinks(List<Drink> drinks) {
        if (drinkAdapter != null) {
            drinkAdapter.setDrinks(drinks);
        }
        Log.e("DrinkDAO", "List drink" + drinks);
    }

    @Override
    public void showIngredients(List<Ingredient> ingredients) {
        if (ingredients != null) {
            Log.e("Ingredients", ingredients.toString());
        } else {
            Log.e("Ingredients", "ingredients null");
        }
        ingredientAdapter.setIngredients(ingredients);
    }

    @Override
    public void hideKeyboard() {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }
}