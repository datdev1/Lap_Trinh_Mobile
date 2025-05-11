package com.b21dccn216.pocketcocktail.view.Main.fragment.Favorite;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.base.BaseFragment;
import com.b21dccn216.pocketcocktail.databinding.FragmentFavoriteBinding;
import com.b21dccn216.pocketcocktail.model.Favorite;
import com.b21dccn216.pocketcocktail.test_database.adapter.FavoriteAdapter;
import com.b21dccn216.pocketcocktail.view.Main.adapter.ItemFavoriteAdapter;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends BaseFragment<FavoriteContract.View, FavoriteContract.Presenter>
    implements FavoriteContract.View{

    private FragmentFavoriteBinding binding;
    private int column = 2;
   // private ItemFavoriteAdapter itemFavoriteAdapter;
    private List<Favorite> favoriteList = new ArrayList<>();
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
    public View onCreatView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        binding = FragmentFavoriteBinding.inflate(inflater, container, false);
        //itemFavoriteAdapter = new ItemFavoriteAdapter(favoriteList);
   // }
    @Override
    public void showFavoriteList(List<Favorite> favoriteList) {

    }
}