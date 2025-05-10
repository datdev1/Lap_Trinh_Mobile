package com.b21dccn216.pocketcocktail.view.CreateDrink;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.dao.CategoryDAO;
import com.b21dccn216.pocketcocktail.dao.DrinkDAO;
import com.b21dccn216.pocketcocktail.dao.IngredientDAO;
import com.b21dccn216.pocketcocktail.dao.RecipeDAO;
import com.b21dccn216.pocketcocktail.model.Category;
import com.b21dccn216.pocketcocktail.model.Drink;
import com.b21dccn216.pocketcocktail.model.Ingredient;
import com.b21dccn216.pocketcocktail.model.Recipe;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class CreateDrinkActivity extends AppCompatActivity implements IngredientAdapter.OnIngredientRemoveListener {

    private EditText etDrinkName, etInstruction, etDescription;
    private ImageView ivDrinkImage;
    private Button btnAddImage, btnAddIngredient, btnSave;
    private RatingBar ratingBar;
    private Spinner spCategory;
    private RecyclerView rvIngredients;
    private List<Recipe> recipeList;
    private IngredientAdapter ingredientAdapter;
    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private CategoryDAO categoryDAO;
    private DrinkDAO drinkDAO;
    private IngredientDAO ingredientDAO;
    private List<Ingredient> ingredients;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_drink);

        // Khởi tạo DAO
        categoryDAO = new CategoryDAO();
        drinkDAO = new DrinkDAO();
        ingredientDAO = new IngredientDAO();

        // Ánh xạ view
        etDrinkName = findViewById(R.id.etDrinkName);
        ivDrinkImage = findViewById(R.id.ivDrinkImage);
        btnAddImage = findViewById(R.id.btnAddImage);
        etInstruction = findViewById(R.id.etInstruction);
        ratingBar = findViewById(R.id.ratingBar);
        etDescription = findViewById(R.id.etDescription);
        spCategory = findViewById(R.id.spCategory);
        rvIngredients = findViewById(R.id.rvIngredients);
        btnAddIngredient = findViewById(R.id.btnAddIngredient);
        btnSave = findViewById(R.id.btnSave);

        // Khởi tạo danh sách công thức
        recipeList = new ArrayList<>();
        ingredientAdapter = new IngredientAdapter(recipeList);
        ingredientAdapter.setOnIngredientRemoveListener(this);
        rvIngredients.setLayoutManager(new LinearLayoutManager(this));
        rvIngredients.setAdapter(ingredientAdapter);

        // Khởi tạo image picker launcher
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    ivDrinkImage.setImageURI(selectedImageUri);
                }
            }
        );

        // Load danh mục
        loadCategories();

        // Xử lý sự kiện
        btnAddImage.setOnClickListener(v -> openImagePicker());
        btnAddIngredient.setOnClickListener(v -> showAddIngredientDialog());
        btnSave.setOnClickListener(v -> saveDrink());
    }

    private void loadCategories() {
        categoryDAO.getAllCategorys(new CategoryDAO.CategoryListCallback() {
            @Override
            public void onCategoryListLoaded(List<Category> categories) {
                List<String> categoryNames = new ArrayList<>();
                for (Category category : categories) {
                    categoryNames.add(category.getName());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    CreateDrinkActivity.this,
                    android.R.layout.simple_spinner_item,
                    categoryNames
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spCategory.setAdapter(adapter);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(CreateDrinkActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void showAddIngredientDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_ingredient);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        RecyclerView rvIngredients = dialog.findViewById(R.id.rvIngredients);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnAddNewIngredient = dialog.findViewById(R.id.btnAddNewIngredient);

        // Load danh sách nguyên liệu
        ingredientDAO.getAllIngredients(new IngredientDAO.IngredientListCallback() {
            @Override
            public void onIngredientListLoaded(List<Ingredient> ingredientList) {
                ingredients = ingredientList;
                IngredientSelectionAdapter adapter = new IngredientSelectionAdapter(ingredients);
                rvIngredients.setLayoutManager(new LinearLayoutManager(CreateDrinkActivity.this));
                rvIngredients.setAdapter(adapter);

                adapter.setOnIngredientSelectedListener(selectedIngredient -> {
                    // Hiển thị dialog nhập số lượng
                    showQuantityDialog(selectedIngredient, dialog);
                });
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(CreateDrinkActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        
        btnAddNewIngredient.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(CreateDrinkActivity.this, CreateIngredientActivity.class);
            startActivity(intent);
        });

        // Hiển thị dialog
        dialog.show();
    }

    private void showQuantityDialog(Ingredient ingredient, Dialog parentDialog) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_quantity);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        EditText etQuantity = dialog.findViewById(R.id.etQuantity);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnAdd = dialog.findViewById(R.id.btnAdd);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnAdd.setOnClickListener(v -> {
            String quantityStr = etQuantity.getText().toString();
            if (quantityStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số lượng", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double quantity = Double.parseDouble(quantityStr);
                Recipe recipe = new Recipe("", ingredient.getUuid(), quantity);
                recipe.generateUUID();
                recipeList.add(recipe);
                ingredientAdapter.notifyItemInserted(recipeList.size() - 1);
                dialog.dismiss();
                parentDialog.dismiss();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Số lượng không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadIngredients();
    }

    private void loadIngredients() {
        ingredientDAO.getAllIngredients(new IngredientDAO.IngredientListCallback() {
            @Override
            public void onIngredientListLoaded(List<Ingredient> ingredientList) {
                ingredients = ingredientList;
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(CreateDrinkActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveDrink() {
        String name = etDrinkName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String instructions = etInstruction.getText().toString().trim();
        float rating = ratingBar.getRating();
        String category = spCategory.getSelectedItem().toString();

        if (name.isEmpty() || description.isEmpty() || instructions.isEmpty() || selectedImageUri == null) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo đối tượng Drink
        Drink drink = new Drink();
        drink.generateUUID();
        drink.setName(name);
        drink.setDescription(description);
        drink.setInstruction(instructions);
        drink.setRate(rating);
        drink.setCategoryId(category);

        // Sử dụng DrinkDAO để lưu đồ uống và ảnh
        drinkDAO.addDrinkWithImage(this, drink, selectedImageUri,
            aVoid -> {
                // Lưu các công thức
                saveRecipes(drink.getUuid());
            },
            e -> {
                Toast.makeText(CreateDrinkActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }

    private void saveRecipes(String drinkId) {
        RecipeDAO recipeDAO = new RecipeDAO();
        AtomicInteger successCount = new AtomicInteger(0);
        int totalRecipes = recipeList.size();

        if (totalRecipes == 0) {
            Toast.makeText(this, "Thêm đồ uống thành công", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        for (Recipe recipe : recipeList) {
            recipe.setDrinkId(drinkId);
            recipeDAO.addRecipe(recipe, 
                aVoid -> {
                    int currentCount = successCount.incrementAndGet();
                    if (currentCount == totalRecipes) {
                        Toast.makeText(CreateDrinkActivity.this, 
                            "Thêm đồ uống thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                },
                e -> {
                    Toast.makeText(CreateDrinkActivity.this, 
                        "Lỗi khi lưu công thức: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        }
    }

    @Override
    public void onRemove(int position) {
        recipeList.remove(position);
        ingredientAdapter.notifyItemRemoved(position);
    }
}
