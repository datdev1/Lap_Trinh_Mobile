package com.b21dccn216.pocketcocktail.view.Main.fragment.Home;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.b21dccn216.pocketcocktail.base.BaseFragment;
import com.b21dccn216.pocketcocktail.databinding.FragmentHomeBinding;
import com.b21dccn216.pocketcocktail.model.Drink;
import com.b21dccn216.pocketcocktail.view.Main.adapter.CocktailHomeItemAdapter;
import com.b21dccn216.pocketcocktail.view.Main.adapter.RecommendDrinkAdapter;
import com.b21dccn216.pocketcocktail.view.Main.model.DrinkWithCategoryDTO;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends BaseFragment<HomeContract.View, HomeContract.Presenter> implements HomeContract.View {
    private FragmentHomeBinding binding;

    private CocktailHomeItemAdapter latestCocktailAdapter;
    private CocktailHomeItemAdapter highestRateDrinkAdapter;
    private CocktailHomeItemAdapter categoryCocktailAdapter;
    private RecommendDrinkAdapter recommendDrinkAdapter;

    private final int column = 3;

    private List<Drink> latestDrinkList = new ArrayList<>();
    private List<Drink> highestRateDrinkList = new ArrayList<>();
    private List<Drink> categoryDrinkList = new ArrayList<>();
    private List<DrinkWithCategoryDTO> recommendDrinkList = new ArrayList<>();

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

        latestCocktailAdapter = new CocktailHomeItemAdapter(getActivity(), latestDrinkList);
        highestRateDrinkAdapter = new CocktailHomeItemAdapter(getActivity(), highestRateDrinkList);
        categoryCocktailAdapter = new CocktailHomeItemAdapter(getActivity(), categoryDrinkList);
        recommendDrinkAdapter = new RecommendDrinkAdapter(getActivity(), recommendDrinkList);

        binding.recyclerLatest.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false)
        );
        binding.recyclerLatest.setAdapter(latestCocktailAdapter);

        binding.recyclerHighestRate.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false)
        );
        binding.recyclerHighestRate.setAdapter(highestRateDrinkAdapter);

        binding.recyclerMocktail.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false)
        );
        binding.recyclerMocktail.setAdapter(categoryCocktailAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), column, LinearLayoutManager.VERTICAL, false);
        binding.recyclerRecommend.setLayoutManager(gridLayoutManager);
        binding.recyclerRecommend.setAdapter(recommendDrinkAdapter);

//        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int pos) {
//                if((pos + 1) % 4 == 0 || (pos + 1) % 4 == 1){
//                    return 2;
//                }else{
//                    return 1;
//                }
//            }
//        });

        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getActivity(), "Refresh", Toast.LENGTH_SHORT).show();
                // TODO:: RELOAD DRINK LIST
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });


        return binding.getRoot();
    }



    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void showOneCategoryDrinkList(String cateName, List<Drink> drinkList) {
        binding.titleMocktails.setText(cateName);
        categoryDrinkList.clear();
        categoryDrinkList.addAll(drinkList);
        categoryCocktailAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void showHighestRateDrinkList(List<Drink> drinkList) {
        highestRateDrinkList.clear();
        highestRateDrinkList.addAll(drinkList);
        highestRateDrinkAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void showLatestDrinkList(List<Drink> drinkList) {
        latestDrinkList.clear();
        latestDrinkList.addAll(drinkList);
        latestCocktailAdapter.notifyDataSetChanged();
    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void showRecommendDrinkList(List<DrinkWithCategoryDTO> drinkList) {
        recommendDrinkList.clear();
        recommendDrinkList.addAll(drinkList);
        recommendDrinkAdapter.notifyDataSetChanged();
    }
}

