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
import com.b21dccn216.pocketcocktail.model.User;
import com.bumptech.glide.Glide;

import java.util.List;

public class UserAdapter extends ArrayAdapter<User> {
    private Context context;
    private List<User> users;

    public UserAdapter(Context context, List<User> users) {
        super(context, R.layout.test_database_item_user, users);
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.test_database_item_user, parent, false);
        }

        User user = users.get(position);

        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvEmail = convertView.findViewById(R.id.tvEmail);
        ImageView ivUserImage = convertView.findViewById(R.id.ivUserImage);

        tvName.setText(user.getName());
        tvEmail.setText(user.getEmail());

        if (user.getImage() != null && !user.getImage().isEmpty()) {
            Glide.with(context)
                .load(user.getImage())
                .placeholder(R.drawable.cocktail_logo)
                .error(R.drawable.error_icon)
                .into(ivUserImage);
        } else {
            ivUserImage.setImageResource(R.drawable.cocktail_logo);
        }

        return convertView;
    }
} 