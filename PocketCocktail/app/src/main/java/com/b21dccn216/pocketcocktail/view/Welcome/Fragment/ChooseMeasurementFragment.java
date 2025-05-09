package com.b21dccn216.pocketcocktail.view.Welcome.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.databinding.FragmentChooseMeasurementBinding;

import java.util.Random;


public class ChooseMeasurementFragment extends Fragment {
    private FragmentChooseMeasurementBinding binding;
    private String selectedUnit = "metric"; // default

    public ChooseMeasurementFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChooseMeasurementBinding.inflate(getLayoutInflater());

        highlightCard(binding.cardMetric, binding.txMetrics);
        unhighlightCard(binding.cardImperial, binding.txImperial);

        binding.cardMetric.setOnClickListener(v -> {
            selectedUnit = "metric";
            highlightCard(binding.cardMetric, binding.txMetrics);
            unhighlightCard(binding.cardImperial, binding.txImperial);
        });

        binding.cardImperial.setOnClickListener(v -> {
            selectedUnit = "imperial";
            highlightCard(binding.cardImperial, binding.txImperial);
            unhighlightCard(binding.cardMetric, binding.txMetrics);
        });

//        binding.btnContinue.setOnClickListener(v -> {
//            saveSelectedUnit(selectedUnit);
//            // TODO: Navigate to next fragment or activity
//            Toast.makeText(getContext(), "Selected: " + selectedUnit, Toast.LENGTH_SHORT).show();
//        });
//        binding.container.post(this::randomPositionImage);
//        ImageView[] imageViews = {binding.img1, binding.img2, binding.img3, binding.img4, binding.img5};
//        for (ImageView imageView : imageViews) {
//            startFloatingAnimation_2(imageView);
//        }

//        ImageCurveRecyclerAdapter adapter = new ImageCurveRecyclerAdapter();
//        CurveLayoutManager layoutManager = new CurveLayoutManager();
//        binding.recycler.setLayoutManager(layoutManager);
//        binding.recycler.setAdapter(adapter);

//        binding.arcLayout.animate()
//                .rotationBy(360f)
//                .setDuration(10000)
//                .start();

        return binding.getRoot();
    }

    private void highlightCard(CardView card, TextView textView) {
        card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.button_primary));
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_on_primary));
    }

    private void unhighlightCard(CardView card, TextView textView) {
        card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary_dark));
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_on_secondary));
    }

    private void saveSelectedUnit(String unit) {
        SharedPreferences prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("unit", unit);
        editor.apply();
    }

//    private void randomPositionImage(){
//        ImageView[] imageViews = {binding.img1, binding.img2, binding.img3, binding.img4, binding.img5};
//        int layoutWidth = binding.getRoot().getWidth();
//        int layoutHeight = binding.getRoot().getHeight();
//        ConstraintSet set = new ConstraintSet();
//        set.clone(binding.container);
//
//        Random random = new Random();
//        for (ImageView imageView : imageViews) {
//            int id = imageView.getId();
//            int x = random.nextInt(layoutWidth - 200);
//            int y = random.nextInt(layoutHeight - 200);
//
//            set.connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, x);
//            set.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, y);
//            set.clear(id, ConstraintSet.END); // Remove old constraints if any
//            set.clear(id, ConstraintSet.BOTTOM);
//        }
//
////        ChangeBounds transition = new ChangeBounds();
////        transition.setDuration(10000);
////        transition.setInterpolator(new AccelerateDecelerateInterpolator());
////        TransitionManager.beginDelayedTransition(binding.container, transition);
//
//        set.applyTo(binding.container);
//    }

    private void startFloatingAnimation(ImageView imageView) {
        Random random = new Random();
        int layoutWidth = binding.getRoot().getWidth();
        int layoutHeight = binding.getRoot().getHeight();
        float deltaX = random.nextInt(200) - 100f; // -100 to +100 px
        float deltaY = random.nextInt(200) - 100f;

        ObjectAnimator moveX = ObjectAnimator.ofFloat(imageView, "translationX", imageView.getTranslationX(), imageView.getTranslationX() + deltaX);
        ObjectAnimator moveY = ObjectAnimator.ofFloat(imageView, "translationY", imageView.getTranslationY(), imageView.getTranslationY() + deltaY);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(3000 + random.nextInt(3000)); // 3 to 6 seconds
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.playTogether(moveX, moveY);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Recursive call to keep floating
                startFloatingAnimation(imageView);
            }
        });

        animatorSet.start();
    }

    private void startFloatingAnimation_2(ImageView imageView){
        View rootView = binding.getRoot();
        int layoutWidth = rootView.getWidth();
        int layoutHeight = rootView.getHeight();

// Avoid 0 during first layout pass
        if (layoutWidth == 0 || layoutHeight == 0) {
            rootView.post(() -> startFloatingAnimation(imageView)); // delay until layout is ready
            return;
        }

        Random random = new Random();

// Pick a random position within the layout bounds
        float targetX = random.nextInt(layoutWidth - imageView.getWidth());
        float targetY = random.nextInt(layoutHeight - imageView.getHeight());

// Calculate how far to move (from current offset)
        float currentX = imageView.getTranslationX();
        float currentY = imageView.getTranslationY();

        float deltaX = targetX - imageView.getX(); // X relative to screen
        float deltaY = targetY - imageView.getY(); // same for Y

        ObjectAnimator moveX = ObjectAnimator.ofFloat(imageView, "translationX", currentX, currentX + deltaX);
        ObjectAnimator moveY = ObjectAnimator.ofFloat(imageView, "translationY", currentY, currentY + deltaY);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(1000 + random.nextInt(2000)); // 1–3 sec
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.playTogether(moveX, moveY);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                startFloatingAnimation(imageView); // recursive call
            }
        });

        animatorSet.start();

    }

}