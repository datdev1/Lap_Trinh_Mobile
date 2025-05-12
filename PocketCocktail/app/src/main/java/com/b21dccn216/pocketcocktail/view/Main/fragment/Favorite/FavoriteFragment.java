package com.b21dccn216.pocketcocktail.view.Main.fragment.Favorite;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.base.BaseFragment;
import com.b21dccn216.pocketcocktail.databinding.FragmentFavoriteBinding;
import com.b21dccn216.pocketcocktail.model.Drink;
import com.b21dccn216.pocketcocktail.model.Favorite;
import com.b21dccn216.pocketcocktail.test_database.adapter.FavoriteAdapter;
import com.b21dccn216.pocketcocktail.view.Main.adapter.FavoriteCreateAdapter;
import com.b21dccn216.pocketcocktail.view.Main.adapter.ItemFavoriteAdapter;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends BaseFragment<FavoriteContract.View, FavoriteContract.Presenter>
    implements FavoriteContract.View{

    private FragmentFavoriteBinding binding;
    private int column = 2;
    private ItemFavoriteAdapter itemFavoriteAdapter;
    private FavoriteCreateAdapter favoriteCreateAdapter;
    private List<Drink> favoriteList = new ArrayList<>();
    private List<Drink> favoriteCreateList = new ArrayList<>();

    @Override
    protected FavoriteContract.Presenter createPresenter() {
        return new FavoritePresenter();
    }

    @Override
    protected FavoriteContract.View getViewImpl() {
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        binding = FragmentFavoriteBinding.inflate(inflater, container, false);
        itemFavoriteAdapter = new ItemFavoriteAdapter(getActivity(), favoriteList);
        favoriteCreateAdapter = new FavoriteCreateAdapter(getActivity(), favoriteCreateList);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), column);
        binding.recyclerFavorites.setLayoutManager(gridLayoutManager);
        binding.recyclerFavorites.setAdapter(itemFavoriteAdapter);

        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(getActivity(), column);
        binding.recyclerDrinkCreateByUser.setLayoutManager(gridLayoutManager1);
        binding.recyclerDrinkCreateByUser.setAdapter(favoriteCreateAdapter);

        return binding.getRoot();
    }

    @Override
    public void showFavoriteDrinkList(List<Drink> list) {
        favoriteList.clear();
        favoriteList.addAll(list);
        itemFavoriteAdapter.notifyDataSetChanged();
    }

    @Override
    public void showFavoriteDrinkCreateByUserId(List<Drink> favoriteDrinkCreateListByUserId) {
        favoriteCreateList.clear();
        favoriteCreateList.addAll(favoriteDrinkCreateListByUserId);
        favoriteCreateAdapter.notifyDataSetChanged();
    }
}