package com.b21dccn216.pocketcocktail.view.DetailDrink.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.databinding.ItemRecipeIngredientBinding;
import com.b21dccn216.pocketcocktail.view.DetailDrink.model.IngredientWithAmountDTO;
import com.bumptech.glide.Glide;

import java.util.List;

public class IngredientAmountAdapter extends RecyclerView.Adapter<IngredientAmountAdapter.ViewHolder>{
    private List<IngredientWithAmountDTO> ingredientList;
    private Context context;

    public IngredientAmountAdapter(Context context, List<IngredientWithAmountDTO> ingredientList) {
        this.ingredientList = ingredientList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecipeIngredientBinding binding = ItemRecipeIngredientBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(ingredientList.get(position));
    }

    @Override
    public int getItemCount() {
        return ingredientList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemRecipeIngredientBinding binding;

        public ViewHolder(@NonNull ItemRecipeIngredientBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public void bind(IngredientWithAmountDTO ingredient) {
            binding.nameText.setText(ingredient.getIngredient().getName());
            binding.unitText.setText(ingredient.getIngredient().getUnit());
            binding.valueText.setText(String.valueOf((int)ingredient.getAmount()));
            Glide
                    .with(context)
                    .load(ingredient.getIngredient().getImage())
                    .placeholder(R.drawable.baseline_downloading_24)
                    .into(binding.imgIcon);
        }
    }
}
