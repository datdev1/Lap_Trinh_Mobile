package com.b21dccn216.pocketcocktail.view.Main.fragment.Home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.base.BaseFragment;
import com.b21dccn216.pocketcocktail.databinding.FragmentHomeBinding;
import com.b21dccn216.pocketcocktail.view.Main.Drink;
import com.b21dccn216.pocketcocktail.view.Main.adapter.CocktailHomeItemAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mig35.carousellayoutmanager.CarouselLayoutManager;
import com.mig35.carousellayoutmanager.CarouselZoomPostLayoutListener;
import com.mig35.carousellayoutmanager.CenterScrollListener;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends BaseFragment<HomeContract.View, HomeContract.Presenter> implements HomeContract.View {
    private FragmentHomeBinding binding;

    private CocktailHomeItemAdapter adapter;
    private int column = 2;

    private List<Drink> drinkList = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    protected HomeContract.Presenter createPresenter() {
        return new HomePresenter();
    }

    @Override
    protected HomeContract.View getViewImpl() {
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        adapter = new CocktailHomeItemAdapter(getActivity(), drinkList);
//      Linear layout test
//        binding.recyclerView.setLayoutManager(
//                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false)
//        );
        //binding.recyclerView.setAdapter(adapter);

        // Carousel layout test
        CarouselLayoutManager layoutManager = new CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL, true);
        layoutManager.setPostLayoutListener(new CarouselZoomPostLayoutListener());

        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.addOnScrollListener(new CenterScrollListener());

        return binding.getRoot();
    }

    @Override
    public void showDrinks(List<Drink> drinks) {
        drinks.clear();
        drinks.addAll(drinks);
        adapter.notifyDataSetChanged();
    }
}