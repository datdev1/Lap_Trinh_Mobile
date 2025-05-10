package com.b21dccn216.pocketcocktail.view.Main.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.databinding.ItemCocktailHomeBinding;
import com.b21dccn216.pocketcocktail.model.Drink;
import com.b21dccn216.pocketcocktail.view.DetailDrink.DetailDrinkActivity;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class CocktailHomeItemAdapter extends RecyclerView.Adapter<CocktailHomeItemAdapter.ImageViewHolder>{
    private List<Drink> drinks;
    private Context context;

    public CocktailHomeItemAdapter(Context context, List<Drink> drinks) {
        this.drinks =  drinks;
        this.context = context;
//        images.add("https://cocktailclub.com/_next/image?url=https%3A%2F%2Fcocktailclub.fra1.digitaloceanspaces.com%2Fdadd692253081d2d253706f4773e7187f284f121158f4a5a409b76e94fd85866%3F&w=1920&q=75");
//        images.add("https://cocktailclub.com/_next/image?url=https%3A%2F%2Fcocktailclub.fra1.digitaloceanspaces.com%2F66934ec018b44b213232b826d50fa90068b815df98294d6927ebfc35427b0701%3F&w=3840&q=75");
//        images.add("https://cocktailclub.com/_next/image?url=https%3A%2F%2Fcocktailclub.fra1.digitaloceanspaces.com%2F22b39199e1cee35cc3eed6a0f9cb778a318d79d1fc7cafae9d18ea4c2d8c66ad%3F&w=1920&q=75");
//        images.add("https://cocktailclub.com/_next/image?url=https%3A%2F%2Fcocktailclub.fra1.digitaloceanspaces.com%2F33d3abfa161f184d362be864111282c342d25077c78cad249a3ceda88d693240%3F&w=3840&q=75");
//        images.add("https://cocktailclub.com/_next/image?url=https%3A%2F%2Fcocktailclub.fra1.digitaloceanspaces.com%2Fdadd692253081d2d253706f4773e7187f284f121158f4a5a409b76e94fd85866%3F&w=1920&q=75");
//        images.add("https://cocktailclub.com/_next/image?url=https%3A%2F%2Fcocktailclub.fra1.digitaloceanspaces.com%2F66934ec018b44b213232b826d50fa90068b815df98294d6927ebfc35427b0701%3F&w=3840&q=75");
//        images.add("https://cocktailclub.com/_next/image?url=https%3A%2F%2Fcocktailclub.fra1.digitaloceanspaces.com%2F22b39199e1cee35cc3eed6a0f9cb778a318d79d1fc7cafae9d18ea4c2d8c66ad%3F&w=1920&q=75");
//        images.add("https://cocktailclub.com/_next/image?url=https%3A%2F%2Fcocktailclub.fra1.digitaloceanspaces.com%2F33d3abfa161f184d362be864111282c342d25077c78cad249a3ceda88d693240%3F&w=3840&q=75");
    }


    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCocktailHomeBinding binding = ItemCocktailHomeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CocktailHomeItemAdapter.ImageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Drink drink = drinks.get(position);
        Glide.with(context)
            .load(drink.getImage())
            .centerCrop()
            .error(R.drawable.baseline_downloading_24)
            .into(holder.binding.image);

        holder.binding.name.setText(drink.getName());
//        holder.binding.cate.setText(String.valueOf(images.get(position).rate));

        holder.binding.layout.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailDrinkActivity.class);
            intent.putExtra(DetailDrinkActivity.EXTRA_DRINK_ID, drink);
//            intent.putExtra(DetailDrinkActivity.EXTRA_DRINK_ID, drinks.get(position).getUuid());
            context.startActivity(intent);
        });

    }



    @Override
    public int getItemCount() {
        return drinks.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder{
        private ItemCocktailHomeBinding binding;

        public ImageViewHolder(ItemCocktailHomeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
