package com.b21dccn216.pocketcocktail.test_database.fragment;

import android.app.AlertDialog;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CategoryFragment extends BaseModelFragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText etUuid, etCreatedAt, etUpdatedAt, etName, etDescription;
    private ImageView ivImage;
    private Button btnSelectImage, btnSave, btnUpdate, btnDelete;
    private ListView lvCategories;
    private CategoryAdapter adapter;
    private List<Category> categories;
    private Category selectedCategory;
    private CategoryDAO categoryDAO;
    private Uri selectedImageUri;
    private SimpleDateFormat dateFormat;

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
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        
        etUuid = rootView.findViewById(R.id.etUuid);
        etCreatedAt = rootView.findViewById(R.id.etCreatedAt);
        etUpdatedAt = rootView.findViewById(R.id.etUpdatedAt);
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

    private void setButtonsEnabled(boolean enabled) {
        btnSelectImage.setEnabled(enabled);
        btnSave.setEnabled(enabled);
        btnUpdate.setEnabled(enabled && selectedCategory != null);
        btnDelete.setEnabled(enabled && selectedCategory != null);
    }

    @Override
    protected void loadData() {
        categoryDAO.getAllCategorys(new CategoryDAO.CategoryListCallback() {
            @Override
            public void onCategoryListLoaded(List<Category> categoryList) {
                categoryList.sort((c1, c2) -> c2.getUpdatedAt().compareTo(c1.getUpdatedAt()));
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
        etUuid.setText("");
        etCreatedAt.setText("");
        etUpdatedAt.setText("");
        etName.setText("");
        etDescription.setText("");
        ivImage.setImageResource(R.drawable.ic_launcher_background);
        selectedImageUri = null;
        selectedCategory = null;
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    @Override
    protected void fillInputs(Object item) {
        if (item instanceof Category) {
            Category category = (Category) item;
            etUuid.setText(category.getUuid());
            etCreatedAt.setText(dateFormat.format(category.getCreatedAt()));
            etUpdatedAt.setText(dateFormat.format(category.getUpdatedAt()));
            etName.setText(category.getName());
            etDescription.setText(category.getDescription());
            
            if (category.getImage() != null && !category.getImage().isEmpty()) {
                Glide.with(this)
                    .load(category.getImage())
                    .placeholder(R.drawable.place_holder_drink)
                    .error(R.drawable.error_icon)
                    .into(ivImage);
            } else {
                ivImage.setImageResource(R.drawable.ic_launcher_background);
            }
        }
    }

    @Override
    protected void saveItem() {
        String name = etName.getText().toString();
        String description = etDescription.getText().toString();

        if (name.isEmpty() || description.isEmpty()) {
            showToast("Vui lòng điền đầy đủ thông tin");
            return;
        }

        // Check if category with same name exists
        boolean categoryExists = false;
        for (Category category : categories) {
            if (category.getName().equals(name)) {
                categoryExists = true;
                break;
            }
        }

        if (categoryExists) {
            new AlertDialog.Builder(requireContext())
                .setTitle("Cảnh báo")
                .setMessage("Đã tồn tại danh mục với tên này. Bạn có chắc chắn muốn tạo mới?")
                .setPositiveButton("Có", (dialog, which) -> {
                    performSave(name, description);
                })
                .setNegativeButton("Không", null)
                .show();
        } else {
            performSave(name, description);
        }
    }

    private void performSave(String name, String description) {
        setButtonsEnabled(false);
        Category category = new Category(name, description, "");
        category.generateUUID();

        if (selectedImageUri != null) {
            categoryDAO.addCategoryWithImage(getContext(), category, selectedImageUri,
                    aVoid -> {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                showToast("Thêm danh mục thành công");
                                clearInputs();
                                loadData();
                                setButtonsEnabled(true);
                            });
                        }
                    },
                    e -> {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                String errorMessage = "Lỗi khi thêm danh mục";
                                if (e.getMessage() != null) {
                                    if (e.getMessage().contains("webp")) {
                                        errorMessage = "Định dạng ảnh WebP không được hỗ trợ. Vui lòng chọn ảnh khác.";
                                    } else {
                                        errorMessage += ": " + e.getMessage();
                                    }
                                }
                                showToast(errorMessage);
                                setButtonsEnabled(true);
                            });
                        }
                    });
        } else {
            categoryDAO.addCategory(category,
                    aVoid -> {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                showToast("Thêm danh mục thành công");
                                clearInputs();
                                loadData();
                                setButtonsEnabled(true);
                            });
                        }
                    },
                    e -> {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                showToast("Lỗi khi thêm danh mục: " + e.getMessage());
                                setButtonsEnabled(true);
                            });
                        }
                    });
        }
    }

    @Override
    protected void updateItem() {
        if (selectedCategory == null) {
            showToast("Vui lòng chọn một danh mục");
            return;
        }

        String name = etName.getText().toString();
        String description = etDescription.getText().toString();

        if (name.isEmpty() || description.isEmpty()) {
            showToast("Vui lòng điền đầy đủ thông tin");
            return;
        }

        setButtonsEnabled(false);
        selectedCategory.setName(name);
        selectedCategory.setDescription(description);

        if (selectedImageUri != null) {
            categoryDAO.updateCategoryWithImage(getContext(), selectedCategory, selectedImageUri,
                    aVoid -> {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                showToast("Cập nhật danh mục thành công");
                                clearInputs();
                                loadData();
                                setButtonsEnabled(true);
                            });
                        }
                    },
                    e -> {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                String errorMessage = "Lỗi khi cập nhật danh mục";
                                if (e.getMessage() != null) {
                                    if (e.getMessage().contains("webp")) {
                                        errorMessage = "Định dạng ảnh WebP không được hỗ trợ. Vui lòng chọn ảnh khác.";
                                    } else {
                                        errorMessage += ": " + e.getMessage();
                                    }
                                }
                                showToast(errorMessage);
                                setButtonsEnabled(true);
                            });
                        }
                    });
        } else {
            categoryDAO.updateCategory(selectedCategory,
                    aVoid -> {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                showToast("Cập nhật danh mục thành công");
                                clearInputs();
                                loadData();
                                setButtonsEnabled(true);
                            });
                        }
                    },
                    e -> {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                showToast("Lỗi khi cập nhật danh mục: " + e.getMessage());
                                setButtonsEnabled(true);
                            });
                        }
                    });
        }
    }

    @Override
    protected void deleteItem() {
        if (selectedCategory == null) {
            showToast("Vui lòng chọn một danh mục");
            return;
        }

        new AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa danh mục này?")
            .setPositiveButton("Có", (dialog, which) -> {
                performDelete();
            })
            .setNegativeButton("Không", null)
            .show();
    }

    private void performDelete() {
        setButtonsEnabled(false);
        categoryDAO.deleteCategory(selectedCategory.getUuid(),
                aVoid -> {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            showToast("Xóa danh mục thành công");
                            clearInputs();
                            loadData();
                            setButtonsEnabled(true);
                        });
                    }
                },
                e -> {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            showToast(e.getMessage());
                            setButtonsEnabled(true);
                        });
                    }
                });
    }
} 