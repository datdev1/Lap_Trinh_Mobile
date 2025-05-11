package com.b21dccn216.pocketcocktail.view.DetailDrink;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import com.b21dccn216.pocketcocktail.R;

import com.b21dccn216.pocketcocktail.base.BaseAppCompatActivity;
import com.b21dccn216.pocketcocktail.databinding.ActivityDetailDrinkBinding;
import com.b21dccn216.pocketcocktail.model.Drink;
import com.bumptech.glide.Glide;


public class DetailDrinkActivity extends BaseAppCompatActivity<DetailDrinkContract.View, DetailDrinkContract.Presenter> implements DetailDrinkContract.View{
    private ActivityDetailDrinkBinding binding;
    public static final String EXTRA_DRINK_OBJECT = "drink_id";

    @Override
    protected DetailDrinkContract.Presenter createPresenter() {
        return new DetailDrinkPresenter();
    }

    @Override
    protected DetailDrinkContract.View getView() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        presenter = createPresenter();
        super.onCreate(savedInstanceState);
        binding = ActivityDetailDrinkBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Drink drink = (Drink) getIntent().getSerializableExtra(EXTRA_DRINK_OBJECT);
        if (drink != null) {
            presenter.loadDrinkDetails(drink);
        } else {
            Toast.makeText(this, "Drink data not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        binding.backButton.setOnClickListener(v -> finish());
    }

    private TextView createBulletTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextColor(ContextCompat.getColor(this, R.color.black));
        textView.setTextSize(16);
        textView.setPadding(4, 4, 4, 4);
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dtd_ic_bullet, 0, 0, 0);
        textView.setCompoundDrawablePadding(8);
        return textView;
    }

    @Override
    public void showDrinkDetail(Drink drink) {
        Glide.with(this).load(drink.getImage()).into(binding.drinkImage);
        binding.drinkTitle.setText(drink.getName());
        binding.drinkDescription.setText(drink.getDescription());
    }

    @Override
    public void showIngredient(String ingredientText) {
        TextView textView = createBulletTextView(ingredientText);
        binding.ingredientsLayout.addView(textView);
    }

    @Override
    public void showInstruction(String instructionText) {
        TextView textView = createBulletTextView(instructionText);
        binding.instructionsLayout.addView(textView);
    }
}