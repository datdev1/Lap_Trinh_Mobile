package com.b21dccn216.pocketcocktail.view.Main.fragment.Home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.base.BaseFragment;
import com.b21dccn216.pocketcocktail.dao.DrinkDAO;
import com.b21dccn216.pocketcocktail.databinding.FragmentHomeBinding;
import com.b21dccn216.pocketcocktail.model.Category;
import com.b21dccn216.pocketcocktail.model.Drink;
import com.b21dccn216.pocketcocktail.view.DetailDrink.DetailDrinkActivity;
import com.b21dccn216.pocketcocktail.view.Main.adapter.CocktailHomeItemAdapter;
import com.b21dccn216.pocketcocktail.view.Main.adapter.RecommendDrinkAdapter;
import com.b21dccn216.pocketcocktail.view.Main.model.DrinkWithCategoryDTO;
import com.b21dccn216.pocketcocktail.view.Main.model.DrinkWithFavCount;
import com.b21dccn216.pocketcocktail.view.Search.SearchActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private List<DrinkWithFavCount> recommendDrinkList = new ArrayList<>();
    private Drink bannerDrink;

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

        binding.featureContent.setOnClickListener(v -> {
            if(binding.featureContent.getMaxLines() == 2) {
                binding.featureContent.setMaxLines(Integer.MAX_VALUE);
            } else {
                binding.featureContent.setMaxLines(2);
            }
        });

        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //RELOAD DRINK LIST
                presenter.refreshScreen();
            }
        });


        binding.btnLatestSeeAll.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), SearchActivity.class);
            intent.putExtra(SearchActivity.SORT_FIELD, DrinkDAO.DRINK_FIELD.CREATED_AT.getValue());
            intent.putExtra(SearchActivity.SORT_ORDER, Query.Direction.DESCENDING);
            startActivity(intent);
        });

        binding.btnHighestRateSeeAll.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), SearchActivity.class);
            intent.putExtra(SearchActivity.SORT_FIELD, DrinkDAO.DRINK_FIELD.RATE.getValue());
            intent.putExtra(SearchActivity.SORT_ORDER, Query.Direction.DESCENDING);
            startActivity(intent);
        });

        binding.featureImage.setOnClickListener(v -> {
            ShowImageDialog dialog = new ShowImageDialog(getContext(), bannerDrink.getImage());
            dialog.show();
        });

//        presenter.refreshScreen();

        return binding.getRoot();
    }



    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void showOneCategoryDrinkList(Category category, List<Drink> drinkList) {
        if(binding == null) return;
        binding.titleMocktails.setText(category.getName());
        categoryDrinkList.clear();
        categoryDrinkList.addAll(drinkList);
        categoryCocktailAdapter.notifyDataSetChanged();
        binding.btnSeeAllMocktail.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), SearchActivity.class);
            intent.putExtra(SearchActivity.EXTRA_CATEGORY_OBJECT, category);
            intent.putExtra(SearchActivity.SORT_ORDER, Query.Direction.DESCENDING);
            startActivity(intent);
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void showHighestRateDrinkList(List<Drink> drinkList) {
        if(binding == null) return;
        highestRateDrinkList.clear();
        highestRateDrinkList.addAll(drinkList);
        highestRateDrinkAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void showLatestDrinkList(List<Drink> drinkList) {
        if(binding == null) return;
        latestDrinkList.clear();
        latestDrinkList.addAll(drinkList);
        latestCocktailAdapter.notifyDataSetChanged();
    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void showRecommendDrinkList(List<DrinkWithFavCount> drinkList) {
        if(binding == null) return;
        recommendDrinkList.clear();
        drinkList.sort((drinkWithFavCount, t1) -> Integer.compare(t1.getCount(), drinkWithFavCount.getCount()));


        recommendDrinkList.addAll(drinkList);
        recommendDrinkAdapter.notifyDataSetChanged();
    }

    @Override
    public void showBannerDrink(Drink drink) {
        if(binding == null) return;
        bannerDrink = drink;
        Glide
                .with(requireActivity())
                .load(bannerDrink.getImage())
                .placeholder(R.drawable.bg_btn_outline_pink_pastel) // Replace with your placeholder
                .error(R.drawable.baseline_error_outline_24)
                .into(binding.featureImage);
        binding.featureTitle.setText(bannerDrink.getName());
        binding.featureContent.setText(bannerDrink.getDescription());
        binding.btnSeeMore.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DetailDrinkActivity.class);
            intent.putExtra(DetailDrinkActivity.EXTRA_DRINK_OBJECT, bannerDrink);
            startActivity(intent);
        });

        binding.swipeRefreshLayout.setRefreshing(false);
    }
}


// 1, Trường sắp xếp (DrinkDao.DRINK_FIELD), Chiều sắp xếp (Query.Direction)
// 2, Category,
// 3, Ingredient
