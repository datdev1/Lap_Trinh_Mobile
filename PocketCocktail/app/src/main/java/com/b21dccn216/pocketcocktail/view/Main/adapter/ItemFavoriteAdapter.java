//package com.b21dccn216.pocketcocktail.view.Main.adapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.b21dccn216.pocketcocktail.databinding.ItemFavoriteBinding;
//import com.b21dccn216.pocketcocktail.model.Favorite;
//import com.bumptech.glide.Glide;
//
//import java.util.List;
//
//public class ItemFavoriteAdapter extends RecyclerView.Adapter<ItemFavoriteAdapter.FavoriteViewHolder> {
//    private final List<Favorite> favoriteList;
//    private final Context context;
//
//    public ItemFavoriteAdapter(Context context, List<Favorite> favoriteList, List<Favorite> favoriteList1, Context context1){
//
//        this.favoriteList = favoriteList1;
//        this.context = context1;
//    }
//
//    @NonNull
//    @Override
//    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        ItemFavoriteBinding binding = ItemFavoriteBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
//        return new FavoriteViewHolder(binding);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
//        Favorite favorite = favoriteList.get(position);
//        Glide.with(context)
//                .load(favorite.getDrinkId())
//    }
//
//    @Override
//    public int getItemCount() {
//        return 0;
//    }
//}
