package com.b21dccn216.pocketcocktail.view.Welcome.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.databinding.ItemImageDiscoveryBinding;

import java.util.ArrayList;

public class ImageRecyclerAdapter extends RecyclerView.Adapter<ImageRecyclerAdapter.ImageViewHolder>{
    private ArrayList<Integer> images = new ArrayList<>(
    );

    public ImageRecyclerAdapter() {
        images.add(R.drawable.sample_cocktail);
        images.add(R.drawable.sample_cocktail_2);
        images.add(R.drawable.sample_cocktail_3);
        images.add(R.drawable.sample_cocktail_4);
        images.add(R.drawable.sample_cocktail_5);
        images.add(R.drawable.sample_cocktail_6);


        images.add(R.drawable.sample_cocktail);
        images.add(R.drawable.sample_cocktail_2);
        images.add(R.drawable.sample_cocktail_3);
        images.add(R.drawable.sample_cocktail_4);
        images.add(R.drawable.sample_cocktail_5);
        images.add(R.drawable.sample_cocktail_6);
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemImageDiscoveryBinding binding = ItemImageDiscoveryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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
        holder.binding.imageFlavors.setImageResource(images.get(position));
    }

    @Override
    public int getItemCount() {
        return images == null ? 0 : images.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder{
        private ItemImageDiscoveryBinding binding;

        public ImageViewHolder(ItemImageDiscoveryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
