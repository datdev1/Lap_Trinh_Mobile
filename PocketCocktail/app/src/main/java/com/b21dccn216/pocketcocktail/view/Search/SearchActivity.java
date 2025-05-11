package com.b21dccn216.pocketcocktail.view.Search;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.base.BaseAppCompatActivity;
import com.b21dccn216.pocketcocktail.databinding.ActivitySearchBinding;

public class SearchActivity extends BaseAppCompatActivity<SearchContract.View, SearchContract.Presenter>
    implements SearchContract.View{

    public static final String EXTRA_CATEGORY_OBJECT = "category_id";
    public static final String EXTRA_INGREDIENT_OBJECT = "ingredient_id";

    private ActivitySearchBinding binding;

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
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.filter.setOnClickListener(v -> {
           boolean isShow = binding.layoutFilter.getVisibility() == View.VISIBLE;
           binding.layoutFilter.setVisibility(isShow ? View.GONE : View.VISIBLE);
        });
    }
}