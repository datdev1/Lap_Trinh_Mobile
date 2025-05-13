package com.b21dccn216.pocketcocktail.view.Main.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.databinding.ItemFavoriteBinding;
import com.b21dccn216.pocketcocktail.model.Drink;
import com.b21dccn216.pocketcocktail.model.Favorite;
import com.b21dccn216.pocketcocktail.view.DetailDrink.DetailDrinkActivity;
import com.bumptech.glide.Glide;

import java.util.List;

public class ItemFavoriteAdapter extends RecyclerView.Adapter<ItemFavoriteAdapter.FavoriteViewHolder> {

    private List<Drink> favoriteList;
    private final Context context;

    public ItemFavoriteAdapter(Context context, List<Drink> favoriteList) {
        this.context = context;
        this.favoriteList = favoriteList;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFavoriteBinding binding = ItemFavoriteBinding.inflate(LayoutInflater.from(context), parent, false);
        return new FavoriteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        Drink favorite = favoriteList.get(position);
        Glide.with(context)
                .load(favorite.getImage())
                .centerCrop()
                .error(R.drawable.baseline_downloading_24)
                .into(holder.binding.image);

        holder.binding.name.setText(favorite.getName());


        holder.binding.layout.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailDrinkActivity.class);
            intent.putExtra(DetailDrinkActivity.EXTRA_DRINK_OBJECT, favorite);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return favoriteList != null ? favoriteList.size() : 0;
    }

    class FavoriteViewHolder extends RecyclerView.ViewHolder {
        private final ItemFavoriteBinding binding;

        public FavoriteViewHolder(ItemFavoriteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Drink favorite) {
            binding.name.setText(favorite.getName());
            Glide.with(context)
                    .load(favorite.getImage())
                    .into(binding.image);
        }
    }
}
