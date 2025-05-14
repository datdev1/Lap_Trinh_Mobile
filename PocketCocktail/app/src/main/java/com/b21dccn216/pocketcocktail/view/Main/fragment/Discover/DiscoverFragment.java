package com.b21dccn216.pocketcocktail.view.Main.fragment.Discover;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.b21dccn216.pocketcocktail.base.BaseFragment;
import com.b21dccn216.pocketcocktail.databinding.FragmentDiscoverBinding;
import com.b21dccn216.pocketcocktail.model.Category;
import com.b21dccn216.pocketcocktail.model.Ingredient;
import com.b21dccn216.pocketcocktail.view.Main.adapter.CategoryDiscoverAdapter;;
import com.b21dccn216.pocketcocktail.view.Main.adapter.IngredientDiscoverAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DiscoverFragment extends BaseFragment<DiscoverContract.View, DiscoverContract.Presenter>
        implements DiscoverContract.View{

    private FragmentDiscoverBinding binding;

    private CategoryDiscoverAdapter categoryDiscoverAdapter;
    private IngredientDiscoverAdapter ingredientDiscoverAdapter;
    private int column = 3;
    private List<Category> categoryList = new ArrayList<>();
    private List<Ingredient> ingredientList = new ArrayList<>();

    public DiscoverFragment(){
        // Required empty public constructor
    }

    @Override
    protected DiscoverContract.Presenter createPresenter() {
        return new DiscoverPresenter();
    }

    @Override
    protected DiscoverContract.View getViewImpl() {
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            categoryList = (List<Category>) savedInstanceState.getSerializable("categoryList");
            ingredientList = (List<Ingredient>) savedInstanceState.getSerializable("ingredientList");
        }
        presenter.refreshScreen();
        Log.e("datdev1", "D onCreate: ");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("categoryList", (Serializable) categoryList);
        outState.putSerializable("ingredientList", (Serializable) ingredientList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e("datdev1", "D onCreateView: ");
        binding = FragmentDiscoverBinding.inflate(inflater, container, false);
        categoryDiscoverAdapter = new CategoryDiscoverAdapter(getActivity(), categoryList);
        ingredientDiscoverAdapter = new IngredientDiscoverAdapter(getActivity(), ingredientList);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), column, LinearLayoutManager.VERTICAL, false);
        binding.recyclerCategories.setLayoutManager(gridLayoutManager);
        binding.recyclerCategories.setAdapter(categoryDiscoverAdapter);

        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(getActivity(), column, LinearLayoutManager.VERTICAL, false);
        binding.recyclerIngredients.setLayoutManager(gridLayoutManager1);
        binding.recyclerIngredients.setAdapter(ingredientDiscoverAdapter);



        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void showCategoryList(List<Category> categoriesList) {
        categoryList.clear();
        categoryList.addAll(categoriesList);
        categoryDiscoverAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void showIngredientList(List<Ingredient> ingredientsList) {
        ingredientList.clear();
        ingredientList.addAll(ingredientsList);
        ingredientDiscoverAdapter.notifyDataSetChanged();
    }
}