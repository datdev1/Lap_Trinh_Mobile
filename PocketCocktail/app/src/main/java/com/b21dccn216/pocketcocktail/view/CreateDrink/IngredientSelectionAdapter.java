package com.b21dccn216.pocketcocktail.view.CreateDrink;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.model.Ingredient;
import com.bumptech.glide.Glide;

import java.util.List;
 
public class IngredientSelectionAdapter extends RecyclerView.Adapter<IngredientSelectionAdapter.IngredientViewHolder> {
    private List<Ingredient> ingredients;
    private OnIngredientSelectedListener listener;
    private int selectedPosition = -1;

    public interface OnIngredientSelectedListener {
        void onIngredientSelected(Ingredient ingredient);
    }

    public IngredientSelectionAdapter(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public void setOnIngredientSelectedListener(OnIngredientSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient_selection, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);
        holder.tvName.setText(ingredient.getName());
        holder.tvDescription.setText(ingredient.getDescription());
        holder.tvUnit.setText("Unit: " + ingredient.getUnit());

        if (ingredient.getImage() != null && !ingredient.getImage().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(ingredient.getImage())
                    .placeholder(R.drawable.place_holder_drink)
                    .error(R.drawable.error_icon)
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(R.drawable.place_holder_drink);
        }

        holder.itemView.setSelected(position == selectedPosition);
        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);
            if (listener != null) {
                listener.onIngredientSelected(ingredient);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public Ingredient getSelectedIngredient() {
        if (selectedPosition != -1) {
            return ingredients.get(selectedPosition);
        }
        return null;
    }

    static class IngredientViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName;
        TextView tvDescription;
        TextView tvUnit;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvName = itemView.findViewById(R.id.tvName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvUnit = itemView.findViewById(R.id.tvUnit);
        }
    }
} 
