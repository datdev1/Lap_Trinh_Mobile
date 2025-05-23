package com.b21dccn216.pocketcocktail.view.Main.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.databinding.ItemHomeRecommendDrinkBinding;
import com.b21dccn216.pocketcocktail.view.DetailDrink.DetailDrinkActivity;
import com.b21dccn216.pocketcocktail.view.Main.model.DrinkWithFavCount;
import com.bumptech.glide.Glide;

import java.util.List;

public class RecommendDrinkAdapter  extends RecyclerView.Adapter<RecommendDrinkAdapter.DrinkViewHolder> {

    private final List<DrinkWithFavCount> drinkList;
    private final Context context;

    public RecommendDrinkAdapter(Context context, List<DrinkWithFavCount> drinkList) {
        this.context = context;
        this.drinkList = drinkList;
    }

    @NonNull
    @Override
    public DrinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHomeRecommendDrinkBinding binding = ItemHomeRecommendDrinkBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new DrinkViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DrinkViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return drinkList.size();
    }

    class DrinkViewHolder extends RecyclerView.ViewHolder {
        private final ItemHomeRecommendDrinkBinding binding;

        public DrinkViewHolder(ItemHomeRecommendDrinkBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(int pos) {
            DrinkWithFavCount drink = drinkList.get(pos);
            binding.title.setText(drink.getDrink().getName());
            binding.countFav.setText(String.valueOf(drink.getCount()));
            binding.image.setOnClickListener(v -> {
                Intent intent = new Intent(context, DetailDrinkActivity.class);
                intent.putExtra(DetailDrinkActivity.EXTRA_DRINK_OBJECT, drink.getDrink());
                context.startActivity(intent);
            });

            Glide.with(context)
                    .load(drink.getDrink().getImage())
                    .placeholder(R.drawable.place_holder_drink) // Replace with your placeholder
                    .error(R.drawable.baseline_error_outline_24)             // Replace with your error drawable
                    .into(binding.image);
        }
    }
}