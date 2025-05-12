package com.b21dccn216.pocketcocktail.view.Search;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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
    private List<Ingredient> ingredientList;
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

        choosenCategory = (Category) getIntent().getSerializableExtra(EXTRA_CATEGORY_OBJECT);
        Ingredient ingredient = (Ingredient) getIntent().getSerializableExtra(EXTRA_INGREDIENT_OBJECT);
        if (ingredient != null) {
            ingredientList.add(ingredient);
        }

        if (choosenCategory != null) {
            Log.e("Category", choosenCategory.toString());
            presenter.loadDrinksByCategory(choosenCategory.getUuid());
        } else {
            Log.e("Category", "Category is null");
        }

        updateDrinkList();

        presenter.loadIngredients();
    }

    private void updateDrinkList(){
        presenter.updateDrinkList(choosenCategory, ingredientList, searchName);
    }

    private void setupDrinkRecycler() {

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
        ingredientAdapter = new IngredientAdapter(this, selectedIngredients -> {
            String query = binding.searchEditText.getText().toString();
            Category category = (Category) getIntent().getSerializableExtra(EXTRA_CATEGORY_OBJECT);
            if (category != null) {
                if(ingredients != null){
                    Log.e("Ingredients",ingredients.toString());
                }
                else{
                    Log.e("Ingredients","ingredients null");
                }
//                presenter.searchDrinks(category.getUuid(), query, selectedIngredients);
            }
        });


        ingredientAdapter.setIngredients(ingredients);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        binding.ingredientsRecyclerView.setLayoutManager(layoutManager);
        binding.ingredientsRecyclerView.setAdapter(ingredientAdapter);

        int horizontalSpacing = getResources().getDimensionPixelSize(R.dimen.recycler_spacing);
        int verticalSpacing = getResources().getDimensionPixelSize(R.dimen.recycler_vertical_spacing);

        binding.ingredientsRecyclerView.addItemDecoration(
                new GridSpacingItemDecoration(3, horizontalSpacing, verticalSpacing, true)
        );

        binding.ingredientsRecyclerView.setClipToPadding(false);
        binding.ingredientsRecyclerView.setPadding(0, 0, verticalSpacing, verticalSpacing);
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