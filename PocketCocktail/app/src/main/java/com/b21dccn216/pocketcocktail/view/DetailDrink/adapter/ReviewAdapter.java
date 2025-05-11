package com.b21dccn216.pocketcocktail.view.DetailDrink.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.b21dccn216.pocketcocktail.databinding.ItemReviewBinding;
import com.b21dccn216.pocketcocktail.model.Review;
import com.b21dccn216.pocketcocktail.model.User;
import com.b21dccn216.pocketcocktail.view.DetailDrink.model.ReviewWithUserDTO;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<ReviewWithUserDTO> reviews;

    public ReviewAdapter(List<ReviewWithUserDTO> reviews) {
        this.reviews = reviews;
    }

    public void setReviewList(List<ReviewWithUserDTO> newReviews) {
        this.reviews = newReviews;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemReviewBinding binding = ItemReviewBinding.inflate(inflater, parent, false);
        return new ReviewViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ReviewWithUserDTO item = reviews.get(position);
        Review review = item.getReview();
        User user = item.getUser();

        holder.binding.commentText.setText(review.getComment());
        holder.binding.ratingBar.setRating((float) review.getRate());

        if (user != null) {
            holder.binding.userName.setText(user.getName() != null ? user.getName() : "Unknown");
            Glide.with(holder.binding.userAvatar.getContext())
                    .load(user.getImage())
                    .placeholder(com.b21dccn216.pocketcocktail.R.drawable.cmt_ic_user_placeholder)
                    .into(holder.binding.userAvatar);
        } else {
            holder.binding.userName.setText("Unknown");
            holder.binding.userAvatar.setImageResource(com.b21dccn216.pocketcocktail.R.drawable.cmt_ic_user_placeholder);
        }

        // Format ngày nếu có timestamp
//        if (review.getTimestamp() != null) {
//            Date date = review.getTimestamp().toDate(); // giả định là com.google.firebase.Timestamp
//            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
//            holder.binding.commentDate.setText(sdf.format(date));
//        } else {
//            holder.binding.commentDate.setText("");
//        }
    }

    @Override
    public int getItemCount() {
        return reviews != null ? reviews.size() : 0;
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        final ItemReviewBinding binding;

        public ReviewViewHolder(ItemReviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
