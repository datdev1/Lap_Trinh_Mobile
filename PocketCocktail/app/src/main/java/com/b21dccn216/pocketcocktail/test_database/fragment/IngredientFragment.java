package com.b21dccn216.pocketcocktail.test_database.fragment;

import android.content.Intent;
import android.net.Uri;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.dao.IngredientDAO;
import com.b21dccn216.pocketcocktail.model.Ingredient;
import com.b21dccn216.pocketcocktail.test_database.adapter.IngredientAdapter;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class IngredientFragment extends BaseModelFragment {
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etName, etDescription, etUnit;
    private Button btnChooseImage, btnSave, btnUpdate, btnDelete;
    private ImageView ivPreview;
    private ListView lvIngredients;
    private IngredientAdapter adapter;
    private List<Ingredient> ingredients;
    private Ingredient selectedIngredient;
    private IngredientDAO ingredientDAO;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    Glide.with(this)
                            .load(selectedImageUri)
                            .into(ivPreview);
                }
            });

    @Override
    protected int getLayoutId() {
        return R.layout.test_database_fragment_ingredient;
    }

    @Override
    protected void initViews() {
        etName = rootView.findViewById(R.id.etName);
        etDescription = rootView.findViewById(R.id.etDescription);
        etUnit = rootView.findViewById(R.id.etUnit);
        btnChooseImage = rootView.findViewById(R.id.btnChooseImage);
        btnSave = rootView.findViewById(R.id.btnSave);
        btnUpdate = rootView.findViewById(R.id.btnUpdate);
        btnDelete = rootView.findViewById(R.id.btnDelete);
        ivPreview = rootView.findViewById(R.id.ivPreview);
        lvIngredients = rootView.findViewById(R.id.lvIngredients);

        ingredients = new ArrayList<>();
        adapter = new IngredientAdapter(getContext(), ingredients);
        lvIngredients.setAdapter(adapter);
        ingredientDAO = new IngredientDAO();

        setupListeners();
        loadData();
    }

    private void setupListeners() {
        btnChooseImage.setOnClickListener(v -> openImagePicker());
        btnSave.setOnClickListener(v -> saveItem());
        btnUpdate.setOnClickListener(v -> updateItem());
        btnDelete.setOnClickListener(v -> deleteItem());

        lvIngredients.setOnItemClickListener((parent, view, position, id) -> {
            selectedIngredient = ingredients.get(position);
            fillInputs(selectedIngredient);
            btnUpdate.setEnabled(true);
            btnDelete.setEnabled(true);
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    @Override
    protected void loadData() {
        ingredientDAO.getAllIngredients(new IngredientDAO.IngredientListCallback() {
            @Override
            public void onIngredientListLoaded(List<Ingredient> ingredientList) {
                ingredients.clear();
                ingredients.addAll(ingredientList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                showToast("Error loading ingredients: " + e.getMessage());
            }
        });
    }

    @Override
    protected void clearInputs() {
        etName.setText("");
        etDescription.setText("");
        etUnit.setText("");
        selectedImageUri = null;
        selectedIngredient = null;
        ivPreview.setImageResource(R.drawable.cocktail_logo);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    @Override
    protected void fillInputs(Object item) {
        if (item instanceof Ingredient) {
            Ingredient ingredient = (Ingredient) item;
            etName.setText(ingredient.getName());
            etDescription.setText(ingredient.getDescription());
            etUnit.setText(ingredient.getUnit());
            
            if (ingredient.getImage() != null && !ingredient.getImage().isEmpty()) {
                Glide.with(this)
                        .load(ingredient.getImage())
                        .placeholder(R.drawable.cocktail_logo)
                        .error(R.drawable.error_icon)
                        .into(ivPreview);
            } else {
                ivPreview.setImageResource(R.drawable.cocktail_logo);
            }
        }
    }


    @Override
    protected void saveItem() {
        String name = etName.getText().toString();
        String description = etDescription.getText().toString();
        String unit = etUnit.getText().toString();

        if (name.isEmpty() || description.isEmpty() || unit.isEmpty()) {
            showToast("Please fill all required fields");
            return;
        }

        Ingredient ingredient = new Ingredient(name, description, unit);
        ingredient.generateUUID();

        if (selectedImageUri != null) {
            ingredientDAO.addIngredientWithImage(getContext(), ingredient, selectedImageUri,
                    aVoid -> {
                        showToast("Ingredient added successfully");
                        clearInputs();
                        loadData();
                    },
                    e -> showToast("Error adding ingredient: " + e.getMessage()));
        } else {
            ingredientDAO.addIngredient(ingredient,
                    aVoid -> {
                        showToast("Ingredient added successfully");
                        clearInputs();
                        loadData();
                    },
                    e -> showToast("Error adding ingredient: " + e.getMessage()));
        }
    }

    @Override
    protected void updateItem() {
        if (selectedIngredient == null) {
            showToast("Please select an ingredient first");
            return;
        }

        String name = etName.getText().toString();
        String description = etDescription.getText().toString();
        String unit = etUnit.getText().toString();

        if (name.isEmpty() || description.isEmpty() || unit.isEmpty()) {
            showToast("Please fill all required fields");
            return;
        }

        selectedIngredient.setName(name);
        selectedIngredient.setDescription(description);
        selectedIngredient.setUnit(unit);

        if (selectedImageUri != null) {
            ingredientDAO.updateIngredientWithImage(getContext(), selectedIngredient, selectedImageUri,
                    aVoid -> {
                        showToast("Ingredient updated successfully");
                        clearInputs();
                        loadData();
                    },
                    e -> showToast("Error updating ingredient: " + e.getMessage()));
        } else {
            ingredientDAO.updateIngredient(selectedIngredient,
                    aVoid -> {
                        showToast("Ingredient updated successfully");
                        clearInputs();
                        loadData();
                    },
                    e -> showToast("Error updating ingredient: " + e.getMessage()));
        }
    }

    @Override
    protected void deleteItem() {
        if (selectedIngredient == null) {
            showToast("Please select an ingredient first");
            return;
        }

        ingredientDAO.deleteIngredient(selectedIngredient.getUuid(),
                aVoid -> {
                    showToast("Ingredient deleted successfully");
                    clearInputs();
                    loadData();
                },
                e -> showToast("Error deleting ingredient: " + e.getMessage()));
    }
} 