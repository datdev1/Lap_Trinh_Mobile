package com.b21dccn216.pocketcocktail.view.DetailDrink.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.b21dccn216.pocketcocktail.databinding.ItemSimilarDrinkBinding;
import com.b21dccn216.pocketcocktail.model.Drink;

import java.util.List;

public class SimilarDrinkAdapter extends RecyclerView.Adapter<SimilarDrinkAdapter.ViewHolder> {

    private final List<Drink> drinkList;
    private final OnDrinkClickListener listener;

    public interface OnDrinkClickListener {
        void onDrinkClick(Drink drink);
    }

    public SimilarDrinkAdapter(List<Drink> drinkList, OnDrinkClickListener listener) {
        this.drinkList = drinkList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemSimilarDrinkBinding binding = ItemSimilarDrinkBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Drink drink = drinkList.get(position);
        holder.binding.nameDrink.setText(drink.getName());
        Glide.with(holder.binding.getRoot().getContext())
                .load(drink.getImage())
                .into(holder.binding.imageDrink);

        holder.binding.getRoot().setOnClickListener(v -> listener.onDrinkClick(drink));
    }

    @Override
    public int getItemCount() {
        return drinkList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemSimilarDrinkBinding binding;

        public ViewHolder(@NonNull ItemSimilarDrinkBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
