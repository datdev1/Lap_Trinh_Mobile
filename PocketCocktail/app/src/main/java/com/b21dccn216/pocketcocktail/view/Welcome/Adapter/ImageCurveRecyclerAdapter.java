package com.b21dccn216.pocketcocktail.view.Welcome.Adapter;


import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.b21dccn216.pocketcocktail.R;

import com.b21dccn216.pocketcocktail.databinding.ItemImageBinding;

import java.util.ArrayList;

public class ImageCurveRecyclerAdapter extends RecyclerView.Adapter<ImageCurveRecyclerAdapter.ImageViewHolder>{
    private ArrayList<Integer> images = new ArrayList<>(
    );

    public ImageCurveRecyclerAdapter() {
        images.add(R.drawable.view_top_refreshing_mint_lime);
        images.add(R.drawable.view_top_blood_whisky_leaf);
        images.add(R.drawable.view_top_blue_lagoon_mocktail);
        images.add(R.drawable.view_top_cocktail_chery);
        images.add(R.drawable.view_top_smoothie_raspberry_blueberry);


        images.add(R.drawable.view_top_refreshing_mint_lime);
        images.add(R.drawable.view_top_blood_whisky_leaf);
        images.add(R.drawable.view_top_blue_lagoon_mocktail);
        images.add(R.drawable.view_top_cocktail_chery);
        images.add(R.drawable.view_top_smoothie_raspberry_blueberry);




    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemImageBinding binding = ItemImageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ImageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
//        float size = holder.binding.imageFlavors.getHeight();
//        ShapeAppearanceModel.Builder shapeAppearanceModelBuilder = holder.binding.imageFlavors.getShapeAppearanceModel()
//                .toBuilder();
//        if(position == 0){
//            shapeAppearanceModelBuilder
//                    .setTopLeftCorner(CornerFamily.CUT, 0);
//        }
//        else{
//            shapeAppearanceModelBuilder
//                    .setTopLeftCorner(CornerFamily.CUT, 0.8f)
//                    .setBottomRightCorner(CornerFamily.CUT,  0.8f);
//        }
//        holder.binding.imageFlavors.setShapeAppearanceModel(shapeAppearanceModelBuilder.build());
        holder.binding.img.setImageResource(images.get(position));
    }

    @Override
    public int getItemCount() {
        return images == null ? 0 : images.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder{
        private ItemImageBinding binding;

        public ImageViewHolder(ItemImageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}