package com.b21dccn216.pocketcocktail.test.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.model.Drink;

import java.util.List;

public class DrinkAdapter extends RecyclerView.Adapter<DrinkAdapter.DrinkViewHolder> {
    private final List<Drink> drinkList;

    public DrinkAdapter(List<Drink> drinkList) {
        this.drinkList = drinkList;
    }

    @NonNull
    @Override
    public DrinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_drink_for_test, parent, false);
        return new DrinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DrinkViewHolder holder, int position) {
        Drink drink = drinkList.get(position);
        holder.nameTextView.setText(drink.getName());
        // bạn có thể load hình ảnh bằng Glide/Picasso tại đây
    }

    @Override
    public int getItemCount() {
        return drinkList.size();
    }

    static class DrinkViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;

        public DrinkViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tvDrinkName);
        }
    }
}
