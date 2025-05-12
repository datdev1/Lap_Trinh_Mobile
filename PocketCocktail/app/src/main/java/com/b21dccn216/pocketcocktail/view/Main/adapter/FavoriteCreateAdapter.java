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
import com.b21dccn216.pocketcocktail.view.DetailDrink.DetailDrinkActivity;
import com.bumptech.glide.Glide;

import java.util.List;

public class FavoriteCreateAdapter extends RecyclerView.Adapter<FavoriteCreateAdapter.FavoriteCreateViewHolder> {
    private final List<Drink> favoriteCreateList;
    private final Context context;
    public FavoriteCreateAdapter(List<Drink> favoriteCreateList, Context context) {
        this.favoriteCreateList = favoriteCreateList;
        this.context = context;
    }

    @NonNull
    @Override
    public FavoriteCreateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFavoriteBinding binding = ItemFavoriteBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FavoriteCreateViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteCreateViewHolder holder, int position) {
        Drink favoriteCreate = favoriteCreateList.get(position);
        Glide.with(context)
                .load(favoriteCreate.getImage())
                .centerCrop()
                .error(R.drawable.baseline_error_outline_24)
                .into(holder.binding.image);

        holder.binding.name.setText(favoriteCreate.getName());

        holder.binding.layout.setOnClickListener(v ->{
            Intent intent = new Intent(context, DetailDrinkActivity.class);
            intent.putExtra(DetailDrinkActivity.EXTRA_DRINK_OBJECT, favoriteCreate);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return favoriteCreateList.size();
    }

    class FavoriteCreateViewHolder extends RecyclerView.ViewHolder{
        private final ItemFavoriteBinding binding;

        public FavoriteCreateViewHolder(ItemFavoriteBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Drink favoriteCreate){
            binding.name.setText(favoriteCreate.getName());
            Glide.with(context)
                    .load(favoriteCreate.getImage())
                    .into(binding.image);
        }
    }
}
