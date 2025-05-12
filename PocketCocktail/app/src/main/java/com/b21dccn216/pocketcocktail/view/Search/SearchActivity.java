package com.b21dccn216.pocketcocktail.view.Search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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

    private Category choosenCategory;
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

        setUpIngredientRecycler();

        Category category = (Category) getIntent().getSerializableExtra(EXTRA_CATEGORY_OBJECT);
        if (category != null) {
            Log.e("Category", category.toString());
            presenter.loadDrinksByCategory(category.getUuid());
            presenter.loadIngredients();
            choosenCategory = (Category) getIntent().getSerializableExtra(EXTRA_CATEGORY_OBJECT);
            Ingredient ingredient = (Ingredient) getIntent().getSerializableExtra(EXTRA_INGREDIENT_OBJECT);
            if (ingredient != null) {
                choosenIngredientList.add(ingredient);
            }

            if (choosenCategory != null) {
                Log.e("Category", choosenCategory.toString());
                presenter.loadDrinksByCategory(choosenCategory.getUuid());
            } else {
                Log.e("Category", "Category is null");
            }

            //Search drink
            binding.searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String query = s.toString().trim();

                    if (!query.isEmpty()) {
                        presenter.searchIngredients(query);
                        binding.clearButton.setVisibility(View.VISIBLE);
                    } else {
                        presenter.loadIngredients(); // Nếu không có gì, load lại toàn bộ
                        binding.clearButton.setVisibility(View.GONE);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            binding.clearButton.setOnClickListener(v -> {
                binding.searchEditText.setText("");
                presenter.loadIngredients();
                // Hide keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(binding.searchEditText.getWindowToken(), 0);
                }
            });


            //Search ingredient
            binding.ingredientSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String query = s.toString().trim();
                    if (!query.isEmpty()) {
                        presenter.searchIngredients(query);
                        binding.clearIngredientSearch.setVisibility(View.VISIBLE);


                    } else {
                        presenter.loadIngredients();
                        binding.clearIngredientSearch.setVisibility(View.GONE);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
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
            updateDrinkList();

            presenter.loadIngredients();
        }
    }

    private void updateDrinkList(){
        presenter.updateDrinkList(choosenCategory, choosenIngredientList, searchName);
    }

    private void setUpIngredientRecycler() {
        ingredientAdapter = new IngredientAdapter(this, selectedIngredients -> {
            String query = binding.searchEditText.getText().toString();
            Category category = (Category) getIntent().getSerializableExtra(EXTRA_CATEGORY_OBJECT);
            if (category != null) {
//                presenter.searchDrinks(category.getUuid(), query, selectedIngredients);
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
        drinkAdapter = new DrinkAdapter(this, drink -> {
            Intent intent = new Intent(this, DetailDrinkActivity.class);
            intent.putExtra(EXTRA_DRINK_OBJECT, drink);
            startActivity(intent);
        });
        drinkAdapter.setDrinks(drinks);

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