package com.b21dccn216.pocketcocktail.view.Search.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.databinding.ItemSimilarDrinkBinding;
import com.b21dccn216.pocketcocktail.model.Drink;

import java.util.ArrayList;
import java.util.List;

public class DrinkAdapter extends RecyclerView.Adapter<DrinkAdapter.DrinkViewHolder> {

    private List<Drink> drinks = new ArrayList<>();
    private final Context context;
    private final OnDrinkClickListener listener;

    public interface OnDrinkClickListener {
        void onDrinkClick(Drink drink);
    }

    public DrinkAdapter(Context context, OnDrinkClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setDrinks(List<Drink> drinks) {
        this.drinks = drinks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DrinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ItemSimilarDrinkBinding binding = ItemSimilarDrinkBinding.inflate(inflater, parent, false);
        return new DrinkViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DrinkViewHolder holder, int position) {
        holder.bind(drinks.get(position));
    }

    @Override
    public int getItemCount() {
        return drinks.size();
    }

    class DrinkViewHolder extends RecyclerView.ViewHolder {
        private final ItemSimilarDrinkBinding binding;

        public DrinkViewHolder(@NonNull ItemSimilarDrinkBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Drink drink) {
            binding.nameDrink.setText(drink.getName());

            Glide.with(context)
                    .load(drink.getImage())
                    .placeholder(R.drawable.sample_cocktail_2)
                    .into(binding.imageDrink);

            binding.layout.setOnClickListener(v -> listener.onDrinkClick(drink));
        }
    }
}
