package com.b21dccn216.pocketcocktail.view.Main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.databinding.ItemHomeRecommendDrinkBinding;
import com.b21dccn216.pocketcocktail.model.Drink;
import com.b21dccn216.pocketcocktail.view.Main.model.DrinkWithCategoryDTO;
import com.bumptech.glide.Glide;

import java.util.List;

public class RecommendDrinkAdapter  extends RecyclerView.Adapter<RecommendDrinkAdapter.DrinkViewHolder> {

    private final List<DrinkWithCategoryDTO> drinkList;
    private final Context context;

    public RecommendDrinkAdapter(Context context, List<DrinkWithCategoryDTO> drinkList) {
        this.context = context;
        this.drinkList = drinkList;
    }

    @NonNull
    @Override
    public DrinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHomeRecommendDrinkBinding binding = ItemHomeRecommendDrinkBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new DrinkViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DrinkViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return drinkList.size();
    }

    class DrinkViewHolder extends RecyclerView.ViewHolder {
        private final ItemHomeRecommendDrinkBinding binding;

        public DrinkViewHolder(ItemHomeRecommendDrinkBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(int pos) {
            DrinkWithCategoryDTO drink = drinkList.get(pos);
            binding.title.setText(drink.getDrink().getName());
            binding.conent.setText(drink.getCategoryName());

//            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) binding.image.getLayoutParams();
//
//            if((pos + 1) % 4 == 0 || (pos + 1) % 4 == 1){
//                layoutParams.dimensionRatio = "2:1";
//            }else{
//                layoutParams.dimensionRatio = "1:1";
//            }

            Glide.with(context)
                    .load(drink.getDrink().getImage())
                    .placeholder(R.drawable.baseline_downloading_24) // Replace with your placeholder
                    .error(R.drawable.baseline_error_outline_24)             // Replace with your error drawable
                    .into(binding.image);
        }
    }
}