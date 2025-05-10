package com.b21dccn216.pocketcocktail.view.Main.fragment.Discover;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.base.BaseFragment;
import com.b21dccn216.pocketcocktail.databinding.FragmentDiscoverBinding;
import com.b21dccn216.pocketcocktail.model.Category;
import com.b21dccn216.pocketcocktail.model.Ingredient;
import com.b21dccn216.pocketcocktail.test_database.adapter.CategoryAdapter;
import com.b21dccn216.pocketcocktail.test_database.adapter.IngredientAdapter;

import java.util.ArrayList;
import java.util.List;

public class DiscoverFragment extends BaseFragment<DiscoverContract.View, DiscoverContract.Presenter>
        implements DiscoverContract.View{

    private FragmentDiscoverBinding binding;

    private CategoryAdapter adapterCategory;
    private IngredientAdapter adapterIngredient;
    private int column = 3;
    private List<Category> categoryList = new ArrayList<>();
    private List<Ingredient> ingredientList = new ArrayList<>();

    public DiscoverFragment(){}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDiscoverBinding.inflate(inflater, container, false);
        adapterCategory = new CategoryAdapter(getActivity(), categoryList);

        loadDrinkTypes();

        return binding.getRoot();
    }

    private void loadDrinkTypes() {

    }

    @Override
    protected DiscoverContract.Presenter createPresenter() {
        return new DiscoverPresenter();
    }

    @Override
    protected DiscoverContract.View getViewImpl() {
        return null;
    }

    @Override
    public void showCategoryList(List<Category> categoryList) {

    }

    @Override
    public void showIngredientList(List<Ingredient> ingredientList) {

    }
}