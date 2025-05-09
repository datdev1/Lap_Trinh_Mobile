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
import com.b21dccn216.pocketcocktail.model.Review;

import java.util.List;

public class ReviewAdapter extends ArrayAdapter<Review> {
    private Context context;
    private List<Review> reviews;

    public ReviewAdapter(Context context, List<Review> reviews) {
        super(context, R.layout.test_database_item_review, reviews);
        this.context = context;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.test_database_item_review, parent, false);
        }

        Review review = reviews.get(position);

        TextView tvUserId = convertView.findViewById(R.id.tvUserId);
        TextView tvDrinkId = convertView.findViewById(R.id.tvDrinkId);
        TextView tvContent = convertView.findViewById(R.id.tvContent);
        TextView tvRate = convertView.findViewById(R.id.tvRate);

        tvUserId.setText("User ID: " + review.getUserId());
        tvDrinkId.setText("Drink ID: " + review.getDrinkId());
        tvContent.setText(review.getComment());
        tvRate.setText(String.format("Rate: %.1f", review.getRate()));

        return convertView;
    }
} 