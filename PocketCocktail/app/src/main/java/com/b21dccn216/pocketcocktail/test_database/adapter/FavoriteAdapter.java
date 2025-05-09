package com.b21dccn216.pocketcocktail.test_database.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.model.Favorite;

import java.util.List;

public class FavoriteAdapter extends ArrayAdapter<Favorite> {
    private Context context;
    private List<Favorite> favorites;

    public FavoriteAdapter(Context context, List<Favorite> favorites) {
        super(context, R.layout.test_database_item_favorite, favorites);
        this.context = context;
        this.favorites = favorites;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.test_database_item_favorite, parent, false);
        }

        Favorite favorite = favorites.get(position);

        TextView tvUserId = convertView.findViewById(R.id.tvUserId);
        TextView tvDrinkId = convertView.findViewById(R.id.tvDrinkId);

        tvUserId.setText("User ID: " + favorite.getUserId());
        tvDrinkId.setText("Drink ID: " + favorite.getDrinkId());

        return convertView;
    }
} 