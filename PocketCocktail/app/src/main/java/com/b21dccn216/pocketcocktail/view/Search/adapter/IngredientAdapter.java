package com.b21dccn216.pocketcocktail.view.Search.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.databinding.ItemIngredientSearchBinding;
import com.b21dccn216.pocketcocktail.model.Ingredient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

    private final Context context;
    private final OnIngredientSelectedListener listener;
    private List<Ingredient> ingredients = new ArrayList<>();
    private final Set<String> selectedIngredientIds = new HashSet<>();

    public interface OnIngredientSelectedListener {
        void onIngredientSelected(List<String> selectedIngredientIds);
    }

    public IngredientAdapter(Context context, OnIngredientSelectedListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
        notifyDataSetChanged();
    }

    public List<String> getSelectedIngredientIds() {
        return new ArrayList<>(selectedIngredientIds);
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ItemIngredientSearchBinding binding = ItemIngredientSearchBinding.inflate(inflater, parent, false);
        return new IngredientViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        holder.bind(ingredients.get(position));
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    class IngredientViewHolder extends RecyclerView.ViewHolder {
        private final ItemIngredientSearchBinding binding;

        public IngredientViewHolder(ItemIngredientSearchBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Ingredient ingredient) {
            binding.nameIngredient.setText(ingredient.getName());

            Glide.with(context)
                    .load(ingredient.getImage())
                    .placeholder(R.drawable.sample_cocktail_2)
                    .into(binding.imageIngredient);

            boolean isSelected = selectedIngredientIds.contains(ingredient.getUuid());
            binding.layout.setCardBackgroundColor(ContextCompat.getColor(context,
                    isSelected ? R.color.secondary : R.color.white));

            binding.layout.setOnClickListener(v -> {
                if (isSelected) {
                    selectedIngredientIds.remove(ingredient.getUuid());
                } else {
                    selectedIngredientIds.add(ingredient.getUuid());
                }
                notifyItemChanged(getAdapterPosition());
                listener.onIngredientSelected(new ArrayList<>(selectedIngredientIds));
            });
        }
    }
}

