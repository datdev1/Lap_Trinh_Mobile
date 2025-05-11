package com.b21dccn216.pocketcocktail.view.CreateDrink;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.dao.IngredientDAO;
import com.b21dccn216.pocketcocktail.model.Ingredient;
import com.b21dccn216.pocketcocktail.model.Recipe;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {
 
    private List<Recipe> recipeList;
    private OnIngredientRemoveListener listener;
    private IngredientDAO ingredientDAO;

    public interface OnIngredientRemoveListener {
        void onRemove(int position);
    }

    public IngredientAdapter(List<Recipe> recipeList) {
        this.recipeList = recipeList;
        this.ingredientDAO = new IngredientDAO();
    }

    public void setOnIngredientRemoveListener(OnIngredientRemoveListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        
        // Lấy thông tin nguyên liệu từ database
//        ingredientDAO.getIngredient(recipe.getIngredientId(),
//            new OnSuccessListener<DocumentSnapshot>() {
//                @Override
//                public void onSuccess(DocumentSnapshot documentSnapshot) {
//                    Ingredient ingredient = documentSnapshot.toObject(Ingredient.class);
//                    if (ingredient != null) {
//                        holder.tvIngredientName.setText(ingredient.getName());
//                        holder.tvQuantity.setText(String.format("%.1f %s", recipe.getAmount(), ingredient.getUnit()));
//
//                        // Load ảnh nguyên liệu nếu có
//                        if (ingredient.getImage() != null && !ingredient.getImage().isEmpty()) {
//                            Glide.with(holder.itemView.getContext())
//                                    .load(ingredient.getImage())
//                                    .placeholder(R.drawable.ic_launcher)
//                                    .error(R.drawable.ic_launcher)
//                                    .into(holder.ivIngredient);
//                        }
//                    }
//                }
//            },
//            new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    holder.tvIngredientName.setText("Lỗi tải nguyên liệu");
//                    holder.tvQuantity.setText("");
//                }
//            });
        ingredientDAO.getIngredient(recipe.getIngredientId(),
                new IngredientDAO.IngredientCallback() {
                    @Override
                    public void onIngredientLoaded(Ingredient ingredient) {
                        if (ingredient != null) {
                            holder.tvIngredientName.setText(ingredient.getName());
                            holder.tvQuantity.setText(String.format("%.1f %s", recipe.getAmount(), ingredient.getUnit()));

                            // Load ảnh nguyên liệu nếu có
                            if (ingredient.getImage() != null && !ingredient.getImage().isEmpty()) {
                                Glide.with(holder.itemView.getContext())
                                        .load(ingredient.getImage())
                                        .placeholder(R.drawable.ic_launcher)
                                        .error(R.drawable.ic_launcher)
                                        .into(holder.ivIngredient);
                            }
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        holder.tvIngredientName.setText("Lỗi tải nguyên liệu");
                        holder.tvQuantity.setText("");
                    }
                }
                );

        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemove(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    static class IngredientViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIngredient;
        TextView tvIngredientName;
        TextView tvQuantity;
        ImageButton btnRemove;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIngredient = itemView.findViewById(R.id.ivIngredient);
            tvIngredientName = itemView.findViewById(R.id.tvIngredientName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
} 
