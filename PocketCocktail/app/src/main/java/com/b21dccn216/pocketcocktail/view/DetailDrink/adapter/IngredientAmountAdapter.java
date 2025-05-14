package com.b21dccn216.pocketcocktail.view.DetailDrink.adapter;

import android.content.Context;
import android.graphics.Typeface;
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
    private static final int MIN_ITEMS = 3;
    private boolean showAll = false;


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
        return showAll ? ingredientList.size() : Math.min(ingredientList.size(), MIN_ITEMS);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemRecipeIngredientBinding binding;

        public ViewHolder(@NonNull ItemRecipeIngredientBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public void bind(IngredientWithAmountDTO ingredient) {
            binding.nameText.setText(ingredient.getIngredient().getName());
            String text = ingredient.getAmount() + " " + ingredient.getIngredient().getUnit();
            binding.quantityText.setText(text);
            if (!ingredient.isHave()) {
                binding.nameText.setTextColor(context.getResources().getColor(R.color.text_hint));
                binding.quantityText.setTextColor(context.getResources().getColor(R.color.text_hint));
                binding.nameText.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                binding.quantityText.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            }
            Glide
                    .with(context)
                    .load(ingredient.getIngredient().getImage())
                    .placeholder(R.drawable.baseline_downloading_24)
                    .into(binding.imgIcon);
        }
    }

    public void toggleShowAll() {
        showAll = !showAll;
        notifyDataSetChanged();
    }

    public boolean isShowingAll() {
        return showAll;
    }
}
