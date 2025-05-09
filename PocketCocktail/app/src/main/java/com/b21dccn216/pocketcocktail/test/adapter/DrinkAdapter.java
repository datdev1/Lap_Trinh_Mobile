package com.b21dccn216.pocketcocktail.test.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.model.Drink;
import com.bumptech.glide.Glide;

import java.util.List;

public class DrinkAdapter extends RecyclerView.Adapter<DrinkAdapter.DrinkViewHolder> {

    private List<Drink> drinkList;
    private final Context context;
    private OnDrinkClickListener listener;

    public interface OnDrinkClickListener {
        void onDrinkClick(Drink drink);
    }

    public DrinkAdapter(Context context, List<Drink> drinkList) {
        this.context = context;
        this.drinkList = drinkList;
    }

    public void setOnDrinkClickListener(OnDrinkClickListener listener) {
        this.listener = listener;
    }

    public void setDrinkList(List<Drink> drinks) {
        this.drinkList = drinks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DrinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_drink_for_test, parent, false);
        return new DrinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DrinkViewHolder holder, int position) {
        Drink drink = drinkList.get(position);

        holder.txtName.setText(drink.getName());
        holder.txtDescription.setText(drink.getDescription());
        holder.txtRate.setText("Đánh giá: " + drink.getRate());

        Glide.with(context)
                .load(drink.getImage())
                .placeholder(R.drawable.cocktail_logo) // ảnh tạm khi chưa load xong
                .error(R.drawable.error_icon) // ảnh lỗi
                .into(holder.imgDrink);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDrinkClick(drink);
            }
        });
    }

    @Override
    public int getItemCount() {
        return drinkList != null ? drinkList.size() : 0;
    }

    public static class DrinkViewHolder extends RecyclerView.ViewHolder {
        ImageView imgDrink;
        TextView txtName, txtDescription, txtRate;

        public DrinkViewHolder(@NonNull View itemView) {
            super(itemView);
            imgDrink = itemView.findViewById(R.id.imgDrink);
            txtName = itemView.findViewById(R.id.txtDrinkName);
            txtDescription = itemView.findViewById(R.id.txtDrinkDescription);
            txtRate = itemView.findViewById(R.id.txtDrinkRate);
        }
    }
}

