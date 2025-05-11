package com.b21dccn216.pocketcocktail.view.Main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.b21dccn216.pocketcocktail.databinding.ItemFavoriteBinding;
import com.b21dccn216.pocketcocktail.model.Favorite;
import com.bumptech.glide.Glide;

import java.util.List;

public class ItemFavoriteAdapter extends RecyclerView.Adapter<ItemFavoriteAdapter.FavoriteViewHolder> {

    private List<Favorite> favoriteList;
    private final Context context;

    public ItemFavoriteAdapter(Context context, List<Favorite> favoriteList) {
        this.context = context;
        this.favoriteList = favoriteList;
    }

    public void setFavoriteList(List<Favorite> favoriteList) {
        this.favoriteList = favoriteList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFavoriteBinding binding = ItemFavoriteBinding.inflate(LayoutInflater.from(context), parent, false);
        return new FavoriteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        Favorite favorite = favoriteList.get(position);
        holder.bind(favorite);
    }

    @Override
    public int getItemCount() {
        return favoriteList != null ? favoriteList.size() : 0;
    }

    static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        private final ItemFavoriteBinding binding;

        public FavoriteViewHolder(ItemFavoriteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Favorite favorite) {
            Glide.with(binding.getRoot().getContext())
                    .load(favorite.getUserId())
                    .into(binding.image);
        }
    }
}
