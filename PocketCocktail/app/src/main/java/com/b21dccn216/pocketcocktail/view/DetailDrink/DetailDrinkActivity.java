package com.b21dccn216.pocketcocktail.view.DetailDrink;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.b21dccn216.pocketcocktail.R;

import com.b21dccn216.pocketcocktail.base.BaseAppCompatActivity;
import com.b21dccn216.pocketcocktail.databinding.ActivityDetailDrinkBinding;
import com.b21dccn216.pocketcocktail.databinding.DialogAddReviewBinding;
import com.b21dccn216.pocketcocktail.helper.SessionManager;
import com.b21dccn216.pocketcocktail.model.Drink;
import com.b21dccn216.pocketcocktail.model.Review;
import com.b21dccn216.pocketcocktail.model.User;
import com.b21dccn216.pocketcocktail.view.CreateDrink.CreateDrinkActivity;
import com.b21dccn216.pocketcocktail.view.DetailDrink.adapter.ReviewAdapter;
import com.b21dccn216.pocketcocktail.view.DetailDrink.adapter.SimilarDrinkAdapter;
import com.b21dccn216.pocketcocktail.view.DetailDrink.model.IngredientWithAmountDTO;
import com.b21dccn216.pocketcocktail.view.DetailDrink.model.ReviewWithUserDTO;
import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.util.List;


public class DetailDrinkActivity extends BaseAppCompatActivity<DetailDrinkContract.View, DetailDrinkContract.Presenter> implements DetailDrinkContract.View{

    public static final String EXTRA_DRINK_OBJECT = "drink_id";

    private ActivityDetailDrinkBinding binding;
    private ReviewAdapter reviewAdapter;
    private SimilarDrinkAdapter similarDrinkAdapter;
    private User currentUser = SessionManager.getInstance().getUser();
    private Drink currentDrink;
    public boolean hasUserReviewed = false;


    @Override
    protected DetailDrinkContract.Presenter createPresenter() {
        return new DetailDrinkPresenter();
    }

    @Override
    protected DetailDrinkContract.View getView() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        presenter = createPresenter();
        // Hide status bar
        try {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }catch (Exception e){

        }
        super.onCreate(savedInstanceState);
        binding = ActivityDetailDrinkBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.instructionsLayout.removeAllViews();
        binding.ingredientsLayout.removeAllViews();


        // Get current drink
        currentDrink = (Drink) getIntent().getSerializableExtra(EXTRA_DRINK_OBJECT);
        if (currentDrink != null) {
            presenter.loadDrinkDetails(currentDrink);
            presenter.checkFavorite(currentDrink.getUuid());
            if (currentUser != null) {
                presenter.checkIfUserHasReviewed(currentDrink.getUuid(), currentUser.getUuid());
            }
        } else {
            Toast.makeText(this, "Drink data not found", Toast.LENGTH_SHORT).show();
            finish();
        }



        // Binding data
        binding.favoriteButton.setOnClickListener(v -> presenter.toggleFavorite(currentDrink));
        binding.shareButton.setOnClickListener(v -> presenter.shareDrink(currentDrink));
        binding.addCommentButton.setOnClickListener(v -> {
            if (currentUser == null) {
                Toast.makeText(this, "Please login to review", Toast.LENGTH_SHORT).show();
                return;
            }

            if (hasUserReviewed) {
                Toast.makeText(this, "You have already reviewed this drink", Toast.LENGTH_SHORT).show();
                return;
            }
            presenter.onAddReviewClicked(currentDrink.getUuid());
        });
        binding.btnEditOrCopy.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateDrinkActivity.class);
            User currentUser = SessionManager.getInstance().getUser();
            if(currentUser == null){
                return;
            }
            String mode = currentUser.getUuid().equals(currentDrink.getUserId()) ? "edit" : "copy";
            intent.putExtra("mode", mode);
            intent.putExtra("drink", currentDrink);
            intent.putExtra("categoryId", currentDrink.getCategoryId());
            intent.putExtra("recipes", (Serializable) presenter.getRecipes());
            startActivity(intent);
        });


        binding.backButton.setOnClickListener(v -> finish());
    }

    private TextView createBulletTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextColor(ContextCompat.getColor(this, R.color.black));
        textView.setTextSize(16);
        textView.setPadding(4, 4, 4, 4);
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dtd_ic_bullet, 0, 0, 0);
        textView.setCompoundDrawablePadding(8);
        // Set custom font
        Typeface typeface = ResourcesCompat.getFont(this, R.font.kanit);
        textView.setTypeface(typeface);
        return textView;
    }

    @Override
    public void showDrinkDetail(Drink drink) {
        Glide.with(this).load(drink.getImage()).into(binding.drinkImage);
        binding.drinkTitle.setText(drink.getName());
        binding.drinkDescription.setText(drink.getDescription());
    }

    @Override
    public void showIngredient(String ingredientText) {
        TextView textView = createBulletTextView(ingredientText);
        binding.ingredientsLayout.addView(textView);
    }

    @Override
    public void showInstruction(String instructionText) {
        TextView textView = createBulletTextView(instructionText);
        binding.instructionsLayout.addView(textView);
    }

    @Override
    public void updateFavoriteIcon(boolean isFavorite) {
        int icon = isFavorite ? R.drawable.dtd_ic_favorite_filled : R.drawable.dtd_ic_favorite_outline;
        binding.favoriteButton.setImageResource(icon);
    }

    @Override
    public void showShareIntent(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(intent, "Share via"));
    }

    @Override
    public void showSimilarDrinks(List<Drink> drinks) {
        similarDrinkAdapter = new SimilarDrinkAdapter(drinks, drink -> {
            Intent intent = new Intent(this, DetailDrinkActivity.class);
            intent.putExtra(EXTRA_DRINK_OBJECT, drink);
            startActivity(intent);
        });
        binding.similarDrinksRecyclerView.setAdapter(similarDrinkAdapter);
        binding.similarDrinksRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    public void showReviews(List<ReviewWithUserDTO> reviews) {
        reviewAdapter = new ReviewAdapter(reviews);
        checkUserReviewStatus(reviews);
        reviewAdapter.setOnReviewLongClickListener((review, user) -> {
            new AlertDialog.Builder(this)
                    .setTitle("Review options")
                    .setItems(new String[]{"Edit", "Delete"}, (dialog, which) -> {
                        if (which == 0) {
                            presenter.onEditReviewClicked(review);
                        } else {
                            presenter.onDeleteReviewClicked(review);
                        }
                    })
                    .show();
        });
        binding.commentsRecyclerView.setAdapter(reviewAdapter);
        binding.commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void showAddReviewSuccess() {
        Toast.makeText(this, "Comment added successfully", Toast.LENGTH_SHORT).show();
        hasUserReviewed = true;
        showUserHasReviewed(true);

        if (currentDrink != null) {
            presenter.loadReviews(currentDrink.getUuid());
        }
    }

    @Override
    public void showAddReviewError(String message) {
        Toast.makeText(this, "Failed to add comment: " + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showAddReviewDialog(String drinkId, @Nullable Review review) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        DialogAddReviewBinding dialogBinding = DialogAddReviewBinding.inflate(inflater);
        builder.setView(dialogBinding.getRoot());
        AlertDialog dialog = builder.create();

        if (review != null) {
            dialogBinding.commentEditText.setText(review.getComment());
            dialogBinding.commentRatingBar.setRating((float) review.getRate());
            dialogBinding.submitButton.setText("Update");
        }

        dialogBinding.characterCount.setText(
                dialogBinding.commentEditText.getText().length() + " / 4000"
        );

        dialogBinding.commentEditText.addTextChangedListener(new TextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                dialogBinding.characterCount.setText(s.length() + " / 4000");
                hasUserReviewed = false;
                updateReviewButtonState();
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        dialogBinding.cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialogBinding.submitButton.setOnClickListener(v -> {
            float rating = dialogBinding.commentRatingBar.getRating();
            String comment = dialogBinding.commentEditText.getText().toString().trim();

            if (comment.isEmpty()) {
                dialogBinding.commentEditText.setError("Please enter a comment");
                return;
            }

            if (review == null) {
                presenter.submitReview(comment, drinkId, rating);
            } else {
                presenter.updateReview(comment, drinkId, rating, review);
            }

            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void showCreatorInfo(User creator) {
        binding.creatorName.setText(creator.getName());
        Glide.with(this)
                .load(creator.getImage())
                .placeholder(R.drawable.baseline_downloading_24)
                .placeholder(R.drawable.user)
                .into(binding.creatorImage);
    }

    @Override
    public void showCountFavourite(int count) {
        binding.numberFav.setText(String.valueOf(count));
    }

    @Override
    public void showUserHasReviewed(boolean hasReviewed) {
        this.hasUserReviewed = hasReviewed;
        binding.addCommentButton.setEnabled(!hasReviewed);
        binding.addCommentButton.setAlpha(hasReviewed ? 0.5f : 1f);
    }

    @Override
    public void showMessage(String message) {

    }
    private void checkUserReviewStatus(List<ReviewWithUserDTO> reviews) {
        hasUserReviewed = false;
        if (currentUser != null) {
            for (ReviewWithUserDTO review : reviews) {
                if (review.getUser().getUuid().equals(currentUser.getUuid())) {
                    hasUserReviewed = true;
                    break;
                }
            }
        }
        updateReviewButtonState();
    }
    private void updateReviewButtonState() {
        binding.addCommentButton.setEnabled(!hasUserReviewed);
        binding.addCommentButton.setAlpha(hasUserReviewed ? 0.5f : 1f);
    }
}