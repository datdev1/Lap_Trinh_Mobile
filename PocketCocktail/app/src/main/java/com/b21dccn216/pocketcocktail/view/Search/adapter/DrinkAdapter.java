package com.b21dccn216.pocketcocktail.view.Search.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.b21dccn216.pocketcocktail.view.Search.helper.DrinkDiffCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.databinding.ItemSearchDrinkBinding;
import com.b21dccn216.pocketcocktail.model.Drink;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

public class DrinkAdapter extends RecyclerView.Adapter<DrinkAdapter.DrinkViewHolder> {

    private final List<Drink> drinks = new ArrayList<>();
    private final Context context;
    private final OnDrinkClickListener listener;

    public interface OnDrinkClickListener {
        void onDrinkClick(Drink drink);
    }

    public DrinkAdapter(Context context, OnDrinkClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setDrinks(List<Drink> newDrinks) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DrinkDiffCallback(this.drinks, newDrinks));
        this.drinks.clear();
        this.drinks.addAll(newDrinks);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public DrinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ItemSearchDrinkBinding binding = ItemSearchDrinkBinding.inflate(inflater, parent, false);
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
        private final ItemSearchDrinkBinding binding;

        public DrinkViewHolder(@NonNull ItemSearchDrinkBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Drink drink) {
            binding.nameDrink.setText(drink.getName());

            Glide.with(context)
                    .load(drink.getImage())
                    .placeholder(R.drawable.s_white_background)
                    .error(R.drawable.sample_cocktail_2)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.imageDrink);

            binding.rateDrink.setText(String.format("%.1f", drink.getRate()));
            if (drink.getCreatedAt() != null) {
                String dateText = DateFormat.getDateInstance(DateFormat.MEDIUM).format(drink.getCreatedAt());
                binding.createAtDrink.setText(dateText);
            } else {
                binding.createAtDrink.setText("N/A");
            }

            binding.layout.setOnClickListener(v -> listener.onDrinkClick(drink));
        }
    }
}
