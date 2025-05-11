package com.b21dccn216.pocketcocktail.view.Main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.databinding.ItemDiscoverBinding;
import com.b21dccn216.pocketcocktail.model.Category;
import com.bumptech.glide.Glide;

import java.util.List;

public class CategoryDiscoverAdapter extends RecyclerView.Adapter<CategoryDiscoverAdapter.CategoryViewHolder>{
    private final List<Category> categoriesList;
    private final Context context;
    public CategoryDiscoverAdapter(Context context, List<Category> categoriesList){
        this.context = context;
        this.categoriesList = categoriesList;
    }


    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDiscoverBinding binding = ItemDiscoverBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder{
        private final ItemDiscoverBinding binding;
        public CategoryViewHolder(ItemDiscoverBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(int pos) {
            Category category = categoriesList.get(pos);
            binding.titleDiscover.setText(category.getName());

            Glide.with(context).load(category.getImage())
                    .placeholder(R.drawable.baseline_downloading_24)
                    .error(R.drawable.baseline_error_outline_24)
                    .into(binding.imageDiscover);
        }
    }

}
