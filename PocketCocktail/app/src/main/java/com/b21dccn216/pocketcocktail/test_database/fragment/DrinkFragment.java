package com.b21dccn216.pocketcocktail.test_database.fragment;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.dao.DrinkDAO;
import com.b21dccn216.pocketcocktail.model.Drink;
import com.b21dccn216.pocketcocktail.test_database.adapter.DrinkAdapter;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class DrinkFragment extends BaseModelFragment {
    private static final String TAG = "DrinkFragment";
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etName, etDescription, etInstruction, etCategoryId, etRate;
    private Button btnSelectImage, btnSave, btnUpdate, btnDelete;
    private ImageView ivImage;
    private ListView lvDrinks;
    private DrinkAdapter adapter;
    private List<Drink> drinks;
    private Drink selectedDrink;
    private DrinkDAO drinkDAO;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    Log.d(TAG, "Image selected: " + selectedImageUri);
                    Glide.with(this)
                            .load(selectedImageUri)
                            .into(ivImage);
                }
            });

    @Override
    protected int getLayoutId() {
        return R.layout.test_database_fragment_drink;
    }

    @Override
    protected void initViews() {
        Log.d(TAG, "Initializing views");
        etName = rootView.findViewById(R.id.etName);
        etDescription = rootView.findViewById(R.id.etDescription);
        etInstruction = rootView.findViewById(R.id.etInstruction);
        etCategoryId = rootView.findViewById(R.id.etCategoryId);
        etRate = rootView.findViewById(R.id.etRate);
        btnSelectImage = rootView.findViewById(R.id.btnSelectImage);
        btnSave = rootView.findViewById(R.id.btnSave);
        btnUpdate = rootView.findViewById(R.id.btnUpdate);
        btnDelete = rootView.findViewById(R.id.btnDelete);
        ivImage = rootView.findViewById(R.id.ivImage);
        lvDrinks = rootView.findViewById(R.id.lvDrinks);

        drinks = new ArrayList<>();
        adapter = new DrinkAdapter(getContext(), drinks);
        lvDrinks.setAdapter(adapter);
        drinkDAO = new DrinkDAO();

        setupListeners();
        loadData();
        Log.d(TAG, "Views initialized successfully");
    }

    private void setupListeners() {
        Log.d(TAG, "Setting up listeners");
        btnSelectImage.setOnClickListener(v -> openImagePicker());
        btnSave.setOnClickListener(v -> saveItem());
        btnUpdate.setOnClickListener(v -> updateItem());
        btnDelete.setOnClickListener(v -> deleteItem());

        lvDrinks.setOnItemClickListener((parent, view, position, id) -> {
            selectedDrink = drinks.get(position);
            Log.d(TAG, "Drink selected: " + selectedDrink.getName());
            fillInputs(selectedDrink);
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
        Log.d(TAG, "Loading drinks data");
        drinkDAO.getAllDrinks(new DrinkDAO.DrinkListCallback() {
            @Override
            public void onDrinkListLoaded(List<Drink> drinkList) {
                Log.d(TAG, "Drinks loaded successfully. Count: " + drinkList.size());
                drinks.clear();
                drinks.addAll(drinkList);
                adapter.notifyDataSetChanged();
                Log.d(TAG, "Adapter notified of data change");
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error loading drinks: " + e.getMessage(), e);
                showToast("Error loading drinks: " + e.getMessage());
            }
        });
    }

    @Override
    protected void clearInputs() {
        Log.d(TAG, "Clearing inputs");
        etName.setText("");
        etDescription.setText("");
        etInstruction.setText("");
        etCategoryId.setText("");
        etRate.setText("");
        selectedImageUri = null;
        selectedDrink = null;
        ivImage.setImageResource(R.drawable.cocktail_logo);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    @Override
    protected void fillInputs(Object item) {
        if (item instanceof Drink) {
            Drink drink = (Drink) item;
            Log.d(TAG, "Filling inputs for drink: " + drink.getName());
            etName.setText(drink.getName());
            etDescription.setText(drink.getDescription());
            etInstruction.setText(drink.getInstruction());
            etCategoryId.setText(drink.getCategoryId());
            etRate.setText(String.valueOf(drink.getRate()));
            
            if (drink.getImage() != null && !drink.getImage().isEmpty()) {
                Log.d(TAG, "Loading drink image: " + drink.getImage());
                Glide.with(this)
                        .load(drink.getImage())
                        .placeholder(R.drawable.cocktail_logo)
                        .error(R.drawable.error_icon)
                        .into(ivImage);
            } else {
                ivImage.setImageResource(R.drawable.cocktail_logo);
            }
        }
    }

    @Override
    protected void saveItem() {
        String name = etName.getText().toString();
        String description = etDescription.getText().toString();
        String instruction = etInstruction.getText().toString();
        String categoryId = etCategoryId.getText().toString();
        String rateStr = etRate.getText().toString();

        if (name.isEmpty() || description.isEmpty() || instruction.isEmpty() || categoryId.isEmpty() || rateStr.isEmpty()) {
            Log.w(TAG, "Save failed: Required fields are empty");
            showToast("Please fill all required fields");
            return;
        }

        double rate;
        try {
            rate = Double.parseDouble(rateStr);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Save failed: Invalid rate value", e);
            showToast("Invalid rate value");
            return;
        }

        Drink drink = new Drink(name, description, instruction, categoryId, rate);
        drink.generateUUID();
        Log.d(TAG, "Creating new drink: " + drink.getName() + " with UUID: " + drink.getUuid());

        if (selectedImageUri != null) {
            Log.d(TAG, "Saving drink with image");
            drinkDAO.addDrinkWithImage(getContext(), drink, selectedImageUri,
                    aVoid -> {
                        Log.d(TAG, "Drink saved successfully with image");
                        showToast("Drink added successfully");
                        clearInputs();
                        loadData();
                    },
                    e -> {
                        Log.e(TAG, "Error saving drink with image", e);
                        showToast("Error adding drink: " + e.getMessage());
                    });
        } else {
            Log.d(TAG, "Saving drink without image");
            drinkDAO.addDrink(drink,
                    aVoid -> {
                        Log.d(TAG, "Drink saved successfully");
                        showToast("Drink added successfully");
                        clearInputs();
                        loadData();
                    },
                    e -> {
                        Log.e(TAG, "Error saving drink", e);
                        showToast("Error adding drink: " + e.getMessage());
                    });
        }
    }

    @Override
    protected void updateItem() {
        if (selectedDrink == null) {
            Log.w(TAG, "Update failed: No drink selected");
            showToast("Please select a drink first");
            return;
        }

        String name = etName.getText().toString();
        String description = etDescription.getText().toString();
        String instruction = etInstruction.getText().toString();
        String categoryId = etCategoryId.getText().toString();
        String rateStr = etRate.getText().toString();

        if (name.isEmpty() || description.isEmpty() || instruction.isEmpty() || categoryId.isEmpty() || rateStr.isEmpty()) {
            Log.w(TAG, "Update failed: Required fields are empty");
            showToast("Please fill all required fields");
            return;
        }

        double rate;
        try {
            rate = Double.parseDouble(rateStr);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Update failed: Invalid rate value", e);
            showToast("Invalid rate value");
            return;
        }

        selectedDrink.setName(name);
        selectedDrink.setDescription(description);
        selectedDrink.setInstruction(instruction);
        selectedDrink.setCategoryId(categoryId);
        selectedDrink.setRate(rate);
        Log.d(TAG, "Updating drink: " + selectedDrink.getName() + " with UUID: " + selectedDrink.getUuid());

        if (selectedImageUri != null) {
            Log.d(TAG, "Updating drink with new image");
            drinkDAO.updateDrinkWithImage(getContext(), selectedDrink, selectedImageUri,
                    aVoid -> {
                        Log.d(TAG, "Drink updated successfully with image");
                        showToast("Drink updated successfully");
                        clearInputs();
                        loadData();
                    },
                    e -> {
                        Log.e(TAG, "Error updating drink with image", e);
                        showToast("Error updating drink: " + e.getMessage());
                    });
        } else {
            Log.d(TAG, "Updating drink without image change");
            drinkDAO.updateDrink(selectedDrink,
                    aVoid -> {
                        Log.d(TAG, "Drink updated successfully");
                        showToast("Drink updated successfully");
                        clearInputs();
                        loadData();
                    },
                    e -> {
                        Log.e(TAG, "Error updating drink", e);
                        showToast("Error updating drink: " + e.getMessage());
                    });
        }
    }

    @Override
    protected void deleteItem() {
        if (selectedDrink == null) {
            Log.w(TAG, "Delete failed: No drink selected");
            showToast("Please select a drink first");
            return;
        }

        Log.d(TAG, "Deleting drink: " + selectedDrink.getName() + " with UUID: " + selectedDrink.getUuid());
        drinkDAO.deleteDrink(selectedDrink.getUuid(),
                aVoid -> {
                    Log.d(TAG, "Drink deleted successfully");
                    showToast("Drink deleted successfully");
                    clearInputs();
                    loadData();
                },
                e -> {
                    Log.e(TAG, "Error deleting drink", e);
                    showToast("Error deleting drink: " + e.getMessage());
                });
    }
} 