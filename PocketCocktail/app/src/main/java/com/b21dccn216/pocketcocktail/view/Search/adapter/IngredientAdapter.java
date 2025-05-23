package com.b21dccn216.pocketcocktail.view.Search.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.b21dccn216.pocketcocktail.view.Search.helper.IngredientDiffCallback;
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
    private List<Ingredient> originalIngredients = new ArrayList<>();

    public interface OnIngredientSelectedListener {
        void onIngredientSelected(List<String> selectedIngredientIds);
    }

    public IngredientAdapter(Context context, OnIngredientSelectedListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        if (ingredients == null) return;

        originalIngredients.clear();
        originalIngredients.addAll(ingredients);

        updateSortedIngredients();
    }
    private void updateSortedIngredients() {
        List<Ingredient> sorted = new ArrayList<>();

        for (Ingredient ingredient : originalIngredients) {
            if (selectedIngredientIds.contains(ingredient.getUuid())) {
                sorted.add(ingredient);
            }
        }

        for (Ingredient ingredient : originalIngredients) {
            if (!selectedIngredientIds.contains(ingredient.getUuid())) {
                sorted.add(ingredient);
            }
        }

        IngredientDiffCallback diffCallback = new IngredientDiffCallback(this.ingredients, sorted);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.ingredients = sorted;
        diffResult.dispatchUpdatesTo(this);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSelectedIngredients(List<Ingredient> selectedIngredients) {
        selectedIngredientIds.clear();
        if (selectedIngredients != null) {
            for (Ingredient ingredient : selectedIngredients) {
                selectedIngredientIds.add(ingredient.getUuid());
            }
        }
        notifyDataSetChanged();

        if (listener != null) {
            listener.onIngredientSelected(new ArrayList<>(selectedIngredientIds));
        }
    }


    public List<String> getSelectedIngredientIds() {
        return new ArrayList<>(selectedIngredientIds);
    }
    public List<Ingredient> getSelectedIngredientObject() {
        List<Ingredient> selectedIngredients = new ArrayList<>();
        for (Ingredient ingredient : originalIngredients) {
            if (selectedIngredientIds.contains(ingredient.getUuid())) {
                selectedIngredients.add(ingredient);
            }
        }
        return selectedIngredients;
    }


    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemIngredientSearchBinding binding = ItemIngredientSearchBinding.inflate(
                LayoutInflater.from(context), parent, false);
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

        IngredientViewHolder(ItemIngredientSearchBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Ingredient ingredient) {
            // Set ingredient name
            binding.nameIngredient.setText(ingredient.getName());

            // Load image with Glide
            Glide.with(context)
                    .load(ingredient.getImage())
                    .placeholder(R.drawable.sample_ingredient)
                    .error(R.drawable.sample_ingredient)
                    .into(binding.imageIngredient);

            // Update selection state
            boolean isSelected = selectedIngredientIds.contains(ingredient.getUuid());
            updateSelectionUI(isSelected);

            // Handle item click
            binding.layout.setOnClickListener(v -> {
                toggleSelection(ingredient.getUuid());
                boolean nowSelected = selectedIngredientIds.contains(ingredient.getUuid());
                updateSelectionUI(nowSelected);
                if (listener != null) {
                    listener.onIngredientSelected(new ArrayList<>(selectedIngredientIds));
                }
            });
        }

        private void toggleSelection(String ingredientId) {
            if (selectedIngredientIds.contains(ingredientId)) {
                selectedIngredientIds.remove(ingredientId);
            } else {
                selectedIngredientIds.add(ingredientId);
            }

            updateSortedIngredients();
            if (listener != null) {
                listener.onIngredientSelected(new ArrayList<>(selectedIngredientIds));
            }
        }

        private void updateSelectionUI(boolean isSelected) {
            binding.layout.setStrokeWidth(isSelected ?
                    context.getResources().getDimensionPixelSize(R.dimen.selected_stroke_width) : 0);
            binding.selectedCheck.setVisibility(isSelected ? View.VISIBLE : View.GONE);
        }
    }
}