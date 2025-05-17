package com.b21dccn216.pocketcocktail.view.Search;

import static com.b21dccn216.pocketcocktail.view.DetailDrink.DetailDrinkActivity.EXTRA_INGREDIENT_LIST;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.base.BaseAppCompatActivity;
import com.b21dccn216.pocketcocktail.dao.DrinkDAO;
import com.b21dccn216.pocketcocktail.databinding.ActivitySearchBinding;
import com.b21dccn216.pocketcocktail.model.Category;
import com.b21dccn216.pocketcocktail.model.Drink;
import com.b21dccn216.pocketcocktail.model.Ingredient;
import com.b21dccn216.pocketcocktail.view.Search.adapter.DrinkAdapter;
import com.b21dccn216.pocketcocktail.view.Search.adapter.IngredientAdapter;
import com.b21dccn216.pocketcocktail.view.DetailDrink.DetailDrinkActivity;
import com.b21dccn216.pocketcocktail.view.Search.helper.GridSpacingItemDecoration;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.firestore.Query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseAppCompatActivity<SearchContract.View, SearchContract.Presenter>
    implements SearchContract.View{

    public static final String EXTRA_CATEGORY_OBJECT = "category_object";
    public static final String EXTRA_INGREDIENT_OBJECT = "ingredient_object";
    public static final String EXTRA_DRINK_OBJECT = "drink_id";
    public static final String SORT_FIELD = "sort_field";
    public static final String SORT_ORDER = "sort_order";


    private BottomSheetBehavior bottomSheetBehavior;
    private ActivitySearchBinding binding;;
    private DrinkAdapter drinkAdapter;
    private IngredientAdapter ingredientAdapter;

    private Category category;

    private Ingredient ingredient;
    private String sortField = DrinkDAO.DRINK_FIELD.NAME.getValue();
    private  Query.Direction sortOrder = Query.Direction.ASCENDING;



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

        // NestedScrollView
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet);
        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        // Set Up RecycleView
        setUpDrinkRecycler();
        setUpIngredientRecycler();


        // Received data from intend
        category = (Category) getIntent().getSerializableExtra(EXTRA_CATEGORY_OBJECT);
        ingredient = (Ingredient) getIntent().getSerializableExtra(EXTRA_INGREDIENT_OBJECT);
        if(getIntent().getStringExtra(SORT_FIELD) != null){
            sortField = getIntent().getStringExtra(SORT_FIELD);
        }
        if(getIntent().getSerializableExtra(SORT_ORDER) != null){
            sortOrder = (Query.Direction) getIntent().getSerializableExtra(SORT_ORDER);
        }
        Log.d("SORT_FIELD", "SORT_FIELD: " + sortField);
        Log.d("SORT_ORDER", "SORT_ORDER: " + sortOrder);

        //Load Data
        presenter.loadIngredients();
        if (category != null) {
            presenter.searchDrinks(category.getUuid(), "", null, sortField, sortOrder);
        } else if (ingredient != null) {
            List<Ingredient> initialSelection = new ArrayList<>();
            initialSelection.add(ingredient);
            ingredientAdapter.setSelectedIngredients(initialSelection);

            List<String> selectedIngredientIds = new ArrayList<>();
            selectedIngredientIds.add(ingredient.getUuid());
            presenter.searchDrinks(null, "", selectedIngredientIds, sortField, sortOrder);
        } else {
            presenter.searchDrinks(null, "", null, sortField, sortOrder);
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
                    presenter.searchDrinks(category.getUuid(), query, selectedIngredientIds, sortField, sortOrder);
                } else {
                    presenter.searchDrinks(null, query, selectedIngredientIds, sortField, sortOrder);
                }
                binding.clearButton.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        binding.clearButton.setOnClickListener(v -> {
            binding.searchEditText.setText("");
            if (category != null) {
                presenter.searchDrinks(category.getUuid(), "", null, sortField, sortOrder);
            } else {
                presenter.searchDrinks(null, "", null, sortField, sortOrder);
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
                } else {
                    presenter.loadIngredients();
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

        // Filter
        binding.sortByContainer.setOnClickListener(v -> showSortByMenu());
        binding.sortOrderButton.setOnClickListener(v -> toggleSortOrder());
        binding.sortByText.setText(sortField);
        if(sortOrder == Query.Direction.ASCENDING){
            binding.sortOrderText.setText("Tăng dần");
        }
        else{
            binding.sortOrderText.setText("Giảm dần");
        }

        //Fix margin nested scroll view
        ViewCompat.setOnApplyWindowInsetsListener(binding.mainScrollview, (view, insets) -> {
            boolean keyboardVisible = insets.isVisible(WindowInsetsCompat.Type.ime());
            int topInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;

            int bottomPadding;
            if (keyboardVisible) {
                // Keyboard on
                bottomPadding = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom;
            } else {
                // keyboard off
                bottomPadding = dpToPx(80, view.getContext());
            }

            ViewCompat.setPaddingRelative(view, view.getPaddingStart(), topInset, view.getPaddingEnd(), bottomPadding);

            return insets;
        });
        BottomSheetBehavior.from(binding.bottomSheet).addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    binding.mainScrollview.setPadding(
                            binding.mainScrollview.getPaddingLeft(),
                            binding.mainScrollview.getPaddingTop(),
                            binding.mainScrollview.getPaddingRight(),
                            0
                    );
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
    }

    public static int dpToPx(int dp, Context context) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }


    // Pop up sort
    private void showSortByMenu() {
        PopupMenu popup = new PopupMenu(this, binding.sortByContainer);
        popup.getMenuInflater().inflate(R.menu.sort_menu, popup.getMenu());

        switch (sortField) {
            case "rate":
                popup.getMenu().findItem(R.id.sort_by_rating).setChecked(true);
                break;
            case "name":
                popup.getMenu().findItem(R.id.sort_by_name).setChecked(true);
                break;
            case "createdAt":
                popup.getMenu().findItem(R.id.sort_by_date).setChecked(true);
                break;
        }

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.sort_by_rating) {
                sortField = "rate";
                binding.sortByText.setText("Đánh giá");
            } else if (itemId == R.id.sort_by_name) {
                sortField = "name";
                binding.sortByText.setText("Tên");
            } else if (itemId == R.id.sort_by_date) {
                sortField = "createdAt";
                binding.sortByText.setText("Mới nhất");
            }

            applySorting();
            return true;
        });

        popup.show();
    }
    private void toggleSortOrder() {
        sortOrder = (sortOrder == Query.Direction.DESCENDING)
                ? Query.Direction.ASCENDING
                : Query.Direction.DESCENDING;
        updateSortOrderUI();
        applySorting();
    }
    private void updateSortOrderUI() {
        if (sortOrder == Query.Direction.DESCENDING) {
            binding.sortOrderText.setText("Giảm dần");
            binding.sortOrderIcon.setImageResource(R.drawable.s_ic_sort_desc);
        } else {
            binding.sortOrderText.setText("Tăng dần");
            binding.sortOrderIcon.setImageResource(R.drawable.s_ic_sort_asc);
        }
    }
    private void applySorting() {
        String query = binding.searchEditText.getText().toString().trim();
        List<String> selectedIngredientIds = ingredientAdapter.getSelectedIngredientIds();

        if (category != null) {
            presenter.searchDrinks(category.getUuid(), query, selectedIngredientIds, sortField, sortOrder);
        } else {
            presenter.searchDrinks(null, query, selectedIngredientIds, sortField, sortOrder);
        }
    }




    //Set up recycle view
    private void setUpDrinkRecycler(){
        drinkAdapter = new DrinkAdapter(this, drink -> {
            Intent intent = new Intent(this, DetailDrinkActivity.class);
            intent.putExtra(EXTRA_DRINK_OBJECT, drink);
            List<Ingredient> selectedList = ingredientAdapter.getSelectedIngredientObject();
            intent.putExtra(EXTRA_INGREDIENT_LIST, (Serializable) selectedList);
            startActivity(intent);
        });

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        binding.drinksRecyclerView.setLayoutManager(layoutManager);
        binding.drinksRecyclerView.setAdapter(drinkAdapter);

//        int horizontalSpacing = getResources().getDimensionPixelSize(R.dimen.recycler_spacing);
//        int verticalSpacing = getResources().getDimensionPixelSize(R.dimen.recycler_vertical_spacing);
//
//        binding.drinksRecyclerView.addItemDecoration(
//                new TestItemDecoration(2, 45, true)
//        );
        int horizontalSpacing = getResources().getDimensionPixelSize(R.dimen.recycler_spacing);
        int verticalSpacing = getResources().getDimensionPixelSize(R.dimen.recycler_vertical_spacing);

        binding.drinksRecyclerView.addItemDecoration(
                new GridSpacingItemDecoration(2, horizontalSpacing, verticalSpacing, true)
//                new TestItemDecoration(2, 50, true)
        );

        binding.drinksRecyclerView.setClipToPadding(false);
        binding.drinksRecyclerView.setPadding(0, 0, verticalSpacing, verticalSpacing);
    }

    private void setUpIngredientRecycler() {
        ingredientAdapter = new IngredientAdapter(this, selectedIngredients -> {
            String query = binding.searchEditText.getText().toString().trim();
            List<String> selectedIngredientIds = ingredientAdapter.getSelectedIngredientIds();
            if (category != null) {
                presenter.searchDrinks(category.getUuid(), query, selectedIngredientIds, sortField, sortOrder);
            } else {
                presenter.searchDrinks(null, query, selectedIngredientIds, sortField, sortOrder);
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        binding.ingredientsRecyclerView.setLayoutManager(layoutManager);
        binding.ingredientsRecyclerView.setAdapter(ingredientAdapter);
        binding.ingredientsRecyclerView.setItemAnimator(null);

//        int horizontalSpacing = getResources().getDimensionPixelSize(R.dimen.recycler_spacing);
//        int verticalSpacing = getResources().getDimensionPixelSize(R.dimen.recycler_vertical_spacing);
//        binding.ingredientsRecyclerView.addItemDecoration(
//                new TestItemDecoration(3, 50, true)
//        );

        int horizontalSpacing = getResources().getDimensionPixelSize(R.dimen.recycler_spacing);
        int verticalSpacing = getResources().getDimensionPixelSize(R.dimen.recycler_vertical_spacing);

        binding.ingredientsRecyclerView.addItemDecoration(
                new GridSpacingItemDecoration(3, horizontalSpacing, verticalSpacing, true)
//                new TestItemDecoration(2, 50, true)
        );

        binding.ingredientsRecyclerView.setClipToPadding(false);
        binding.ingredientsRecyclerView.setPadding(0, 0, verticalSpacing, verticalSpacing);
    }



    // Exit NestedScrollView
    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }



    // Load data
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
    public void showMessage(String message) {

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