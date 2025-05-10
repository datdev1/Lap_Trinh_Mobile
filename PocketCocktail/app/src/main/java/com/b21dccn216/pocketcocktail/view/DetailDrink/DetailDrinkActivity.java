package com.b21dccn216.pocketcocktail.view.DetailDrink;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.b21dccn216.pocketcocktail.R;

import com.b21dccn216.pocketcocktail.model.Recipe;
import com.b21dccn216.pocketcocktail.model.Drink;
import com.b21dccn216.pocketcocktail.model.Ingredient;
import com.b21dccn216.pocketcocktail.dao.RecipeDAO;
import com.b21dccn216.pocketcocktail.dao.DrinkDAO;
import com.b21dccn216.pocketcocktail.dao.IngredientDAO;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class DetailDrinkActivity extends AppCompatActivity {
    private ImageView drinkImage;
    private ImageButton backButton, favoriteButton, shareButton;
    private TextView badge, drinkTitle, drinkDescription;
    private LinearLayout ingredientsLayout, instructionsLayout;
    private DrinkDAO drinkDAO;
    private RecipeDAO recipeDAO;
    private IngredientDAO ingredientDAO;

    public static final String EXTRA_DRINK_ID = "drink_id";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_drink);

        // Ánh xạ
        drinkImage = findViewById(R.id.drink_image);
        backButton = findViewById(R.id.back_button);
        favoriteButton = findViewById(R.id.favorite_button);
        shareButton = findViewById(R.id.share_button);
        badge = findViewById(R.id.badge);
        drinkTitle = findViewById(R.id.drink_title);
        drinkDescription = findViewById(R.id.drink_description);
        ingredientsLayout = findViewById(R.id.ingredients_layout);
        instructionsLayout = findViewById(R.id.instructions_layout);

        // DAO
        drinkDAO = new DrinkDAO();
        recipeDAO = new RecipeDAO();
        ingredientDAO = new IngredientDAO();


//        String drinkUuid = getIntent().getStringExtra(EXTRA_DRINK_ID);
//        if (drinkUuid != null) {
//            loadDrinkDetails(drinkUuid);
//        }
        Drink drink = (Drink) getIntent().getSerializableExtra(EXTRA_DRINK_ID);
        if (drink != null) {
            loadDrinkDetails(drink);
        } else {
            Toast.makeText(this, "Drink data not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        backButton.setOnClickListener(v -> finish());
    }
    private void loadDrinkDetails(Drink drink) {
        // Load drink info
        Glide.with(this).load(drink.getImage()).into(drinkImage);
        drinkTitle.setText(drink.getName());
        drinkDescription.setText(drink.getDescription());

        // Load Ingredient
        recipeDAO.getRecipesByDrinkId(drink.getUuid(), recipeSnapshots -> {
            List<Recipe> recipes = new ArrayList<>();
            for (DocumentSnapshot doc : recipeSnapshots.getDocuments()) {
                Recipe recipe = doc.toObject(Recipe.class);
                if (recipe != null) {
                    recipes.add(recipe);
                }
            }

            ingredientsLayout.removeAllViews();
            for (Recipe recipe : recipes) {
                ingredientDAO.getIngredient(recipe.getIngredientId(), ingredientSnapshot -> {
                    if (ingredientSnapshot.exists()) {
                        Ingredient ingredient = ingredientSnapshot.toObject(Ingredient.class);
                        if (ingredient != null) {
                            String line = ingredient.getName() + " (" + recipe.getAmount() + " " + ingredient.getUnit() + ")";
                            TextView textView = createBulletTextView(line);
                            ingredientsLayout.addView(textView);
                        }
                    }
                }, e -> {
                    Toast.makeText(this, "Failed to load ingredient: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }, e -> {
            Toast.makeText(this, "Failed to load recipes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });

        // Load instruction
        instructionsLayout.removeAllViews();
        String instructions = drink.getInstruction();
        if (instructions != null && !instructions.trim().isEmpty()) {
            for (String instruction : instructions.split("\n")) {
                if (!instruction.trim().isEmpty()) {
                    TextView textView = createBulletTextView(instruction.trim());
                    instructionsLayout.addView(textView);
                }
            }
        }
    }

    private TextView createBulletTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextColor(ContextCompat.getColor(this, R.color.black));
        textView.setTextSize(16);
        textView.setPadding(4, 4, 4, 4);
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dtd_ic_bullet, 0, 0, 0);
        textView.setCompoundDrawablePadding(8);
        return textView;
    }
}