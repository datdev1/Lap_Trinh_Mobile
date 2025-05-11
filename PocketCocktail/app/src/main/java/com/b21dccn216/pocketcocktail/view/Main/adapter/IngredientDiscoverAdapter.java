package com.b21dccn216.pocketcocktail.view.Main.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.databinding.ItemDiscoverBinding;
import com.b21dccn216.pocketcocktail.model.Ingredient;
import com.b21dccn216.pocketcocktail.view.Search.SearchActivity;
import com.bumptech.glide.Glide;

import java.util.List;

public class IngredientDiscoverAdapter extends RecyclerView.Adapter<IngredientDiscoverAdapter.IngredientsViewHolder> {
    private final List<Ingredient> ingredientsList;

    private final Context context;

    public IngredientDiscoverAdapter(Context context, List<Ingredient> ingredientsList) {
        this.ingredientsList = ingredientsList;
        this.context = context;
    }

    @NonNull
    @Override
    public IngredientsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDiscoverBinding binding = ItemDiscoverBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new IngredientsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientsViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return ingredientsList.size();
    }

    class IngredientsViewHolder extends RecyclerView.ViewHolder{
        private final ItemDiscoverBinding binding;
        public IngredientsViewHolder(ItemDiscoverBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(int pos){
            Ingredient ingredient = ingredientsList.get(pos);


            binding.titleDiscover.setText(ingredient.getName());
            binding.layout.setOnClickListener(v -> {
                Intent intent = new Intent(context, SearchActivity.class);
                intent.putExtra(SearchActivity.EXTRA_INGREDIENT_OBJECT, ingredient);
                context.startActivity(intent);
            });

            Glide.with(context)
                    .load(ingredient.getImage())
                    .placeholder(R.drawable.baseline_downloading_24)
                    .error(R.drawable.baseline_error_outline_24)
                    .into(binding.imageDiscover);
        }
    }
}
