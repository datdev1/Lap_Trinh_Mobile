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
import com.b21dccn216.pocketcocktail.dao.CategoryDAO;
import com.b21dccn216.pocketcocktail.model.Category;
import com.b21dccn216.pocketcocktail.test_database.adapter.CategoryAdapter;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends BaseModelFragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText etName, etDescription;
    private ImageView ivImage;
    private Button btnSelectImage, btnSave, btnUpdate, btnDelete;
    private ListView lvCategories;
    private CategoryAdapter adapter;
    private List<Category> categories;
    private Category selectedCategory;
    private CategoryDAO categoryDAO;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    ivImage.setImageURI(selectedImageUri);
                }
            });

    @Override
    protected int getLayoutId() {
        return R.layout.test_database_fragment_category;
    }

    @Override
    protected void initViews() {
        etName = rootView.findViewById(R.id.etName);
        etDescription = rootView.findViewById(R.id.etDescription);
        ivImage = rootView.findViewById(R.id.ivImage);
        btnSelectImage = rootView.findViewById(R.id.btnSelectImage);
        btnSave = rootView.findViewById(R.id.btnSave);
        btnUpdate = rootView.findViewById(R.id.btnUpdate);
        btnDelete = rootView.findViewById(R.id.btnDelete);
        lvCategories = rootView.findViewById(R.id.lvCategories);

        categories = new ArrayList<>();
        adapter = new CategoryAdapter(getContext(), categories);
        lvCategories.setAdapter(adapter);
        categoryDAO = new CategoryDAO();

        setupListeners();
        loadData();
    }

    private void setupListeners() {
        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        btnSave.setOnClickListener(v -> saveItem());
        btnUpdate.setOnClickListener(v -> updateItem());
        btnDelete.setOnClickListener(v -> deleteItem());

        lvCategories.setOnItemClickListener((parent, view, position, id) -> {
            selectedCategory = categories.get(position);
            fillInputs(selectedCategory);
            btnUpdate.setEnabled(true);
            btnDelete.setEnabled(true);
        });
    }

    @Override
    protected void loadData() {
        categoryDAO.getAllCategorys(new CategoryDAO.CategoryListCallback() {
            @Override
            public void onCategoryListLoaded(List<Category> categoryList) {
                categories.clear();
                categories.addAll(categoryList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                showToast("Error loading categories: " + e.getMessage());
            }
        });
    }

    @Override
    protected void clearInputs() {
        etName.setText("");
        etDescription.setText("");
        ivImage.setImageResource(0);
        selectedImageUri = null;
        selectedCategory = null;
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    @Override
    protected void fillInputs(Object item) {
        if (item instanceof Category) {
            Category category = (Category) item;
            etName.setText(category.getName());
            etDescription.setText(category.getDescription());
            
            if (category.getImage() != null && !category.getImage().isEmpty()) {
                Glide.with(this)
                    .load(category.getImage())
                    .placeholder(R.drawable.cocktail_logo)
                    .error(R.drawable.error_icon)
                    .into(ivImage);
            } else {
                ivImage.setImageResource(0);
            }
        }
    }

    @Override
    protected void saveItem() {
        String name = etName.getText().toString();
        String description = etDescription.getText().toString();

        if (name.isEmpty() || description.isEmpty()) {
            showToast("Please fill all fields");
            return;
        }

        Category category = new Category(name, description, "");
        category.generateUUID();

        if (selectedImageUri != null) {
            categoryDAO.addCategoryWithImage(getContext(), category, selectedImageUri,
                    aVoid -> {
                        showToast("Category added successfully");
                        clearInputs();
                        loadData();
                    },
                    e -> showToast("Error adding category: " + e.getMessage()));
        } else {
            categoryDAO.addCategory(category,
                    aVoid -> {
                        showToast("Category added successfully");
                        clearInputs();
                        loadData();
                    },
                    e -> showToast("Error adding category: " + e.getMessage()));
        }
    }

    @Override
    protected void updateItem() {
        if (selectedCategory == null) {
            showToast("Please select a category first");
            return;
        }

        String name = etName.getText().toString();
        String description = etDescription.getText().toString();

        if (name.isEmpty() || description.isEmpty()) {
            showToast("Please fill all fields");
            return;
        }

        selectedCategory.setName(name);
        selectedCategory.setDescription(description);

        if (selectedImageUri != null) {
            categoryDAO.addCategoryWithImage(getContext(), selectedCategory, selectedImageUri,
                    aVoid -> {
                        showToast("Category updated successfully");
                        clearInputs();
                        loadData();
                    },
                    e -> showToast("Error updating category: " + e.getMessage()));
        } else {
            categoryDAO.updateCategory(selectedCategory,
                    aVoid -> {
                        showToast("Category updated successfully");
                        clearInputs();
                        loadData();
                    },
                    e -> showToast("Error updating category: " + e.getMessage()));
        }
    }

    @Override
    protected void deleteItem() {
        if (selectedCategory == null) {
            showToast("Please select a category first");
            return;
        }

        categoryDAO.deleteCategory(selectedCategory.getUuid(),
                aVoid -> {
                    showToast("Category deleted successfully");
                    clearInputs();
                    loadData();
                },
                e -> showToast("Error deleting category: " + e.getMessage()));
    }
} 