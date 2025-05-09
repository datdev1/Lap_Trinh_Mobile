package com.b21dccn216.pocketcocktail.test_database.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.model.Category;
import com.bumptech.glide.Glide;

import java.util.List;

public class CategoryAdapter extends ArrayAdapter<Category> {
    private Context context;
    private List<Category> categories;

    public CategoryAdapter(Context context, List<Category> categories) {
        super(context, R.layout.test_database_item_category, categories);
        this.context = context;
        this.categories = categories;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.test_database_item_category, parent, false);
        }

        Category category = categories.get(position);

        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvDescription = convertView.findViewById(R.id.tvDescription);
        ImageView ivImage = convertView.findViewById(R.id.ivImage);

        tvName.setText(category.getName());
        tvDescription.setText(category.getDescription());

        if (category.getImage() != null && !category.getImage().isEmpty()) {
            Glide.with(context)
                    .load(category.getImage())
                    .into(ivImage);
        } else {
            ivImage.setImageResource(0);
        }

        return convertView;
    }
} 