package com.b21dccn216.pocketcocktail.view.Main.fragment.Favorite;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.base.BaseFragment;
import com.b21dccn216.pocketcocktail.databinding.FragmentFavoriteBinding;
import com.b21dccn216.pocketcocktail.model.Drink;
import com.b21dccn216.pocketcocktail.model.Favorite;
import com.b21dccn216.pocketcocktail.test_database.adapter.FavoriteAdapter;
import com.b21dccn216.pocketcocktail.view.Main.adapter.FavoriteCreateAdapter;
import com.b21dccn216.pocketcocktail.view.Main.adapter.ItemFavoriteAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends BaseFragment<FavoriteContract.View, FavoriteContract.Presenter>
    implements FavoriteContract.View{

    private FragmentFavoriteBinding binding;
    private int column = 2;
    private ItemFavoriteAdapter itemFavoriteAdapter;

    private TextView emptyText;
    private RecyclerView recyclerViewEmptyText;
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("favoriteList", (Serializable) favoriteList);
        outState.putSerializable("favoriteCreateList", (Serializable) favoriteCreateList);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            favoriteList = (List<Drink>) savedInstanceState.getSerializable("favoriteList");
            favoriteCreateList = (List<Drink>) savedInstanceState.getSerializable("favoriteCreateList");
        }
        presenter.refreshScreen();
        Log.e("datdev1", "F onCreate: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        Log.e("datdev1", "F onCreateView: ");
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
        if (list.isEmpty()) {
            binding.emptyFavorite.setVisibility(View.VISIBLE);
            binding.recyclerFavorites.setVisibility(View.GONE);
        } else {
            binding.emptyFavorite.setVisibility(View.GONE);
            binding.recyclerFavorites.setVisibility(View.VISIBLE);

            ItemDiffCallback diffCallback = new ItemDiffCallback(favoriteList, list);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
            favoriteList.clear();
            favoriteList.addAll(list);
            diffResult.dispatchUpdatesTo(itemFavoriteAdapter);
        }
    }


    @Override
    public void showFavoriteDrinkCreateByUserId(List<Drink> list) {
        if(list.isEmpty()){
            binding.emptyCreation.setVisibility(View.VISIBLE);
            binding.recyclerDrinkCreateByUser.setVisibility(View.GONE);
        }
        else{
            binding.emptyCreation.setVisibility(View.GONE);
            binding.recyclerDrinkCreateByUser.setVisibility(View.VISIBLE);

            ItemDiffCallback diffCallback = new ItemDiffCallback(favoriteCreateList, list);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
            favoriteCreateList.clear();
            favoriteCreateList.addAll(list);
            diffResult.dispatchUpdatesTo(favoriteCreateAdapter);
        }
    }

}