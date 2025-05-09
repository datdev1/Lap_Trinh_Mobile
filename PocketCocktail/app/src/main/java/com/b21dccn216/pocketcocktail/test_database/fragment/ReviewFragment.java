package com.b21dccn216.pocketcocktail.test_database.fragment;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.dao.ReviewDAO;
import com.b21dccn216.pocketcocktail.model.Review;
import com.b21dccn216.pocketcocktail.test_database.adapter.ReviewAdapter;

import java.util.ArrayList;
import java.util.List;

public class ReviewFragment extends BaseModelFragment {
    private EditText etDrinkId, etUserId, etComment, etRate;
    private Button btnSave, btnUpdate, btnDelete;
    private ListView lvReviews;
    private ReviewAdapter adapter;
    private List<Review> reviews;
    private Review selectedReview;
    private ReviewDAO reviewDAO;

    @Override
    protected int getLayoutId() {
        return R.layout.test_database_fragment_review;
    }

    @Override
    protected void initViews() {
        etDrinkId = rootView.findViewById(R.id.etDrinkId);
        etUserId = rootView.findViewById(R.id.etUserId);
        etComment = rootView.findViewById(R.id.etComment);
        etRate = rootView.findViewById(R.id.etRate);
        btnSave = rootView.findViewById(R.id.btnSave);
        btnUpdate = rootView.findViewById(R.id.btnUpdate);
        btnDelete = rootView.findViewById(R.id.btnDelete);
        lvReviews = rootView.findViewById(R.id.lvReviews);

        reviews = new ArrayList<>();
        adapter = new ReviewAdapter(getContext(), reviews);
        lvReviews.setAdapter(adapter);
        reviewDAO = new ReviewDAO();

        setupListeners();
        loadData();
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveItem());
        btnUpdate.setOnClickListener(v -> updateItem());
        btnDelete.setOnClickListener(v -> deleteItem());

        lvReviews.setOnItemClickListener((parent, view, position, id) -> {
            selectedReview = reviews.get(position);
            fillInputs(selectedReview);
            btnUpdate.setEnabled(true);
            btnDelete.setEnabled(true);
        });
    }

    @Override
    protected void loadData() {
        reviewDAO.getAllReviews(new ReviewDAO.ReviewListCallback() {
            @Override
            public void onReviewListLoaded(List<Review> reviewList) {
                reviews.clear();
                reviews.addAll(reviewList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                showToast("Error loading reviews: " + e.getMessage());
            }
        });
    }

    @Override
    protected void clearInputs() {
        etDrinkId.setText("");
        etUserId.setText("");
        etComment.setText("");
        etRate.setText("");
        selectedReview = null;
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    @Override
    protected void fillInputs(Object item) {
        if (item instanceof Review) {
            Review review = (Review) item;
            etDrinkId.setText(review.getDrinkId());
            etUserId.setText(review.getUserId());
            etComment.setText(review.getComment());
            etRate.setText(String.valueOf(review.getRate()));
        }
    }

    @Override
    protected void saveItem() {
        String drinkId = etDrinkId.getText().toString();
        String userId = etUserId.getText().toString();
        String comment = etComment.getText().toString();
        String rateStr = etRate.getText().toString();

        if (drinkId.isEmpty() || userId.isEmpty() || comment.isEmpty() || rateStr.isEmpty()) {
            showToast("Please fill all required fields");
            return;
        }

        double rate;
        try {
            rate = Double.parseDouble(rateStr);
            if (rate < 0 || rate > 5) {
                showToast("Rate must be between 0 and 5");
                return;
            }
        } catch (NumberFormatException e) {
            showToast("Invalid rate value");
            return;
        }

        Review review = new Review(drinkId, userId, comment, rate);
        review.generateUUID();

        reviewDAO.addReview(review,
                aVoid -> {
                    showToast("Review added successfully");
                    clearInputs();
                    loadData();
                },
                e -> showToast("Error adding review: " + e.getMessage()));
    }

    @Override
    protected void updateItem() {
        if (selectedReview == null) {
            showToast("Please select a review first");
            return;
        }

        String drinkId = etDrinkId.getText().toString();
        String userId = etUserId.getText().toString();
        String comment = etComment.getText().toString();
        String rateStr = etRate.getText().toString();

        if (drinkId.isEmpty() || userId.isEmpty() || comment.isEmpty() || rateStr.isEmpty()) {
            showToast("Please fill all required fields");
            return;
        }

        double rate;
        try {
            rate = Double.parseDouble(rateStr);
            if (rate < 0 || rate > 5) {
                showToast("Rate must be between 0 and 5");
                return;
            }
        } catch (NumberFormatException e) {
            showToast("Invalid rate value");
            return;
        }

        selectedReview.setDrinkId(drinkId);
        selectedReview.setUserId(userId);
        selectedReview.setComment(comment);
        selectedReview.setRate(rate);

        reviewDAO.updateReview(selectedReview,
                aVoid -> {
                    showToast("Review updated successfully");
                    clearInputs();
                    loadData();
                },
                e -> showToast("Error updating review: " + e.getMessage()));
    }

    @Override
    protected void deleteItem() {
        if (selectedReview == null) {
            showToast("Please select a review first");
            return;
        }

        reviewDAO.deleteReview(selectedReview.getUuid(),
                aVoid -> {
                    showToast("Review deleted successfully");
                    clearInputs();
                    loadData();
                },
                e -> showToast("Error deleting review: " + e.getMessage()));
    }
} 