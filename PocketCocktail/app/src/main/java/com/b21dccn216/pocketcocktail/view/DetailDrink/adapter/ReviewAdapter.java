package com.b21dccn216.pocketcocktail.view.DetailDrink.adapter;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.helper.SessionManager;
import com.bumptech.glide.Glide;
import com.b21dccn216.pocketcocktail.databinding.ItemReviewBinding;
import com.b21dccn216.pocketcocktail.model.Review;
import com.b21dccn216.pocketcocktail.model.User;
import com.b21dccn216.pocketcocktail.view.DetailDrink.model.ReviewWithUserDTO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<ReviewWithUserDTO> reviews;
    private final String currentUserId;

    public ReviewAdapter(List<ReviewWithUserDTO> reviews) {
        this.reviews = reviews;
        currentUserId = String.valueOf(SessionManager.getInstance().getUser().getUuid());
    }

    public void setReviewList(List<ReviewWithUserDTO> newReviews) {
        this.reviews = newReviews;
        notifyDataSetChanged();
    }

    public interface OnReviewLongClickListener {
        void onReviewLongClick(Review review, User user);
    }

    private OnReviewLongClickListener listener;

    public void setOnReviewLongClickListener(OnReviewLongClickListener listener) {
        this.listener = listener;
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


        LayerDrawable stars = (LayerDrawable) holder.binding.ratingBar.getProgressDrawable();
        int pink = ContextCompat.getColor(holder.itemView.getContext(), R.color.secondary);
        stars.getDrawable(2).setColorFilter(pink, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(pink, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP);

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
        if (review.getCreatedAtTimestamp() != null) {
            Date date = review.getCreatedAtTimestamp().toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            holder.binding.commentDate.setText(sdf.format(date));
        } else {
            holder.binding.commentDate.setText("");
        }

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null && review.getUserId().equals(currentUserId)) {
                listener.onReviewLongClick(review, user);
            }
            return true;
        });
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
