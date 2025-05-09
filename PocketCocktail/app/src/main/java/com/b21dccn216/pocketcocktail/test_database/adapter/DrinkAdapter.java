package com.b21dccn216.pocketcocktail.test_database.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.model.Drink;
import com.bumptech.glide.Glide;

import java.util.List;

public class DrinkAdapter extends ArrayAdapter<Drink> {
    private Context context;
    private List<Drink> drinks;

    public DrinkAdapter(Context context, List<Drink> drinks) {
        super(context, R.layout.test_database_item_drink_new, drinks);
        this.context = context;
        this.drinks = drinks;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.test_database_item_drink_new, parent, false);
        }

        Drink drink = drinks.get(position);

        ImageView ivImage = convertView.findViewById(R.id.ivImage);
        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvDescription = convertView.findViewById(R.id.tvDescription);
        TextView tvRate = convertView.findViewById(R.id.tvRate);

        tvName.setText(drink.getName());
        tvDescription.setText(drink.getDescription());
        tvRate.setText(String.format("Rate: %.1f", drink.getRate()));

        if (drink.getImage() != null && !drink.getImage().isEmpty()) {
            Glide.with(context)
                .load(drink.getImage())
                .placeholder(R.drawable.cocktail_logo)
                .error(R.drawable.error_icon)
                .into(ivImage);
        } else {
            ivImage.setImageResource(0);
        }

        return convertView;
    }
}

