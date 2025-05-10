package com.b21dccn216.pocketcocktail.view.DetailDrink;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.b21dccn216.pocketcocktail.R;

import com.b21dccn216.pocketcocktail.model.Drink;
import com.b21dccn216.pocketcocktail.model.Ingredient;
import com.b21dccn216.pocketcocktail.dao.DrinkDAO;
import com.b21dccn216.pocketcocktail.dao.IngredientDAO;



public class DetailDrinkActivity extends AppCompatActivity {
    private ImageView drinkImage;
    private ImageButton backButton, favoriteButton, shareButton;
    private TextView badge, drinkTitle, drinkDescription;
    private LinearLayout ingredientsLayout, instructionsLayout;
    private DrinkDAO drinkDAO;
    private IngredientDAO ingredientDAO;

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
        ingredientDAO = new IngredientDAO();

        
    }
}