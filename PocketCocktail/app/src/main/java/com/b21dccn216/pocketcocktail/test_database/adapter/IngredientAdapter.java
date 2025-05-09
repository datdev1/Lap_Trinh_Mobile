package com.b21dccn216.pocketcocktail.test_database.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.model.Ingredient;
import com.bumptech.glide.Glide;

import java.util.List;

public class IngredientAdapter extends ArrayAdapter<Ingredient> {
    private Context context;
    private List<Ingredient> ingredients;

    public IngredientAdapter(Context context, List<Ingredient> ingredients) {
        super(context, R.layout.test_database_item_ingredient, ingredients);
        this.context = context;
        this.ingredients = ingredients;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.test_database_item_ingredient, parent, false);
            holder = new ViewHolder();
            holder.ivIngredientImage = convertView.findViewById(R.id.ivIngredientImage);
            holder.tvName = convertView.findViewById(R.id.tvName);
            holder.tvDescription = convertView.findViewById(R.id.tvDescription);
            holder.tvUnit = convertView.findViewById(R.id.tvUnit);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Ingredient ingredient = ingredients.get(position);

        holder.tvName.setText(ingredient.getName());
        holder.tvDescription.setText(ingredient.getDescription());
        holder.tvUnit.setText("Unit: " + ingredient.getUnit());

        if (ingredient.getImage() != null && !ingredient.getImage().isEmpty()) {
            Glide.with(context)
                    .load(ingredient.getImage())
                    .placeholder(R.drawable.cocktail_logo)
                    .error(R.drawable.error_icon)
                    .into(holder.ivIngredientImage);
        } else {
            holder.ivIngredientImage.setImageResource(R.drawable.cocktail_logo);
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView ivIngredientImage;
        TextView tvName;
        TextView tvDescription;
        TextView tvUnit;
    }
} 