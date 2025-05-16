package com.b21dccn216.pocketcocktail.test_database.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.dao.IngredientDAO;
import com.b21dccn216.pocketcocktail.model.Ingredient;
import com.b21dccn216.pocketcocktail.test_database.adapter.IngredientAdapter;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class IngredientFragment extends BaseModelFragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int SEARCH_DELAY = 500; // Delay in milliseconds

    private EditText etSearch, etName, etDescription;
    private EditText etUuid, etCreatedAt, etUpdatedAt;
    private Spinner spinnerUnit;
    private Button btnChooseImage, btnSave, btnUpdate, btnDelete;
    private ImageView ivPreview;
    private ListView lvIngredients;
    private IngredientAdapter adapter;
    private List<Ingredient> ingredients;
    private Ingredient selectedIngredient;
    private IngredientDAO ingredientDAO;
    private Uri selectedImageUri;
    private SimpleDateFormat dateFormat;
    private final String[] donViDoLuong = {
        "ml", "l", "g", "kg",
        "piece", "leaves", "slice", "cup",
        "tsp ~ 5ml", "tbsp ~ 15ml",
        "pinch ~ 1/8 tsp", "drop", "dash ~ 0.92ml",
        "shot ~ 30ml", "pump ~ 5-10ml",
        "oz ~ 30ml", "cl ~ 10ml", "pt ~ 500ml", "qt ~ 1l", "gal ~ 4l"
    };
    private android.os.Handler searchHandler = new android.os.Handler();
    private Runnable searchRunnable;
    private String currentSearchQuery = "";

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
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        
        etSearch = rootView.findViewById(R.id.etSearch);
        etName = rootView.findViewById(R.id.etName);
        etDescription = rootView.findViewById(R.id.etDescription);
        etUuid = rootView.findViewById(R.id.etUuid);
        etCreatedAt = rootView.findViewById(R.id.etCreatedAt);
        etUpdatedAt = rootView.findViewById(R.id.etUpdatedAt);
        spinnerUnit = rootView.findViewById(R.id.spinnerUnit);
        
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

        setupUnitSpinner();
        setupListeners();
        setupSearchListener();
        loadData();
    }

    private void setupUnitSpinner() {
        List<String> unitList = new ArrayList<>();
        unitList.add(""); // Add empty option
        unitList.addAll(Arrays.asList(donViDoLuong));
        
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
            getContext(),
            android.R.layout.simple_spinner_item,
            unitList
        );
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(unitAdapter);
        spinnerUnit.setSelection(0); // Set to empty by default
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

    private void setupSearchListener() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                searchRunnable = () -> {
                    String query = s.toString().trim();
                    if (!query.equals(currentSearchQuery)) {
                        currentSearchQuery = query;
                        searchIngredients(query);
                    }
                };
                searchHandler.postDelayed(searchRunnable, SEARCH_DELAY);
            }
        });
    }

    private void searchIngredients(String query) {
        if (query.isEmpty()) {
            loadData();
            return;
        }

        ingredientDAO.searchIngredients(query, new IngredientDAO.IngredientListCallback() {
            @Override
            public void onIngredientListLoaded(List<Ingredient> ingredientList) {
                ingredientList.sort((c1, c2) -> c2.getUpdatedAt().compareTo(c1.getUpdatedAt()));
                ingredients.clear();
                ingredients.addAll(ingredientList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                showToast("Error searching ingredients: " + e.getMessage());
            }
        });
    }

    private void setButtonsEnabled(boolean enabled) {
        btnChooseImage.setEnabled(enabled);
        btnSave.setEnabled(enabled);
        btnUpdate.setEnabled(enabled && selectedIngredient != null);
        btnDelete.setEnabled(enabled && selectedIngredient != null);
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
                ingredientList.sort((c1, c2) -> c2.getUpdatedAt().compareTo(c1.getUpdatedAt()));
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
        etUuid.setText("");
        etCreatedAt.setText("");
        etUpdatedAt.setText("");
        spinnerUnit.setSelection(0);
        selectedImageUri = null;
        selectedIngredient = null;
        ivPreview.setImageResource(R.drawable.place_holder_drink);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    @Override
    protected void fillInputs(Object item) {
        if (item instanceof Ingredient) {
            Ingredient ingredient = (Ingredient) item;
            etName.setText(ingredient.getName());
            etDescription.setText(ingredient.getDescription());
            etUuid.setText(ingredient.getUuid());
            etCreatedAt.setText(dateFormat.format(ingredient.getCreatedAt()));
            etUpdatedAt.setText(dateFormat.format(ingredient.getUpdatedAt()));
            
            // Set unit spinner
            String unit = ingredient.getUnit();
            boolean unitFound = false;
            for (int i = 0; i < donViDoLuong.length; i++) {
                if (donViDoLuong[i].equals(unit)) {
                    spinnerUnit.setSelection(i + 1); // +1 because of empty option
                    unitFound = true;
                    break;
                }
            }
            if (!unitFound) {
                spinnerUnit.setSelection(0); // Set to empty if unit not found
            }
            
            if (ingredient.getImage() != null && !ingredient.getImage().isEmpty()) {
                Glide.with(this)
                        .load(ingredient.getImage())
                        .placeholder(R.drawable.place_holder_drink)
                        .error(R.drawable.error_icon)
                        .into(ivPreview);
            } else {
                ivPreview.setImageResource(R.drawable.place_holder_drink);
            }
        }
    }

    @Override
    protected void saveItem() {
        if (selectedIngredient != null) {
            new AlertDialog.Builder(requireContext())
                .setTitle("Cảnh báo")
                .setMessage("Bạn đang chọn một nguyên liệu. Bạn có muốn tạo mới không?")
                .setPositiveButton("Có", (dialog, which) -> {

                    performSave();
                })
                .setNegativeButton("Không", null)
                .show();
        } else {
            performSave();
        }
    }

    private void performSave() {
        String name = etName.getText().toString();
        String description = etDescription.getText().toString();
        int unitPosition = spinnerUnit.getSelectedItemPosition();

        if (name.isEmpty() || description.isEmpty() || unitPosition <= 0) {
            showToast("Vui lòng điền đầy đủ thông tin");
            return;
        }

        String unit = donViDoLuong[unitPosition - 1]; // -1 because of empty option
        Ingredient ingredient = new Ingredient(name, description, unit);
        ingredient.generateUUID();

        setButtonsEnabled(false);
        if (selectedImageUri != null) {
            ingredientDAO.addIngredientWithImage(getContext(), ingredient, selectedImageUri,
                    aVoid -> {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                showToast("Thêm nguyên liệu thành công");
                                clearInputs();
                                loadData();
                                setButtonsEnabled(true);
                            });
                        }
                    },
                    e -> {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                String errorMessage = "Lỗi:";
                                if (e.getMessage() != null) {
                                    if (e.getMessage().contains("webp")) {
                                        errorMessage = "Định dạng ảnh WebP không được hỗ trợ. Vui lòng chọn ảnh khác.";
                                    } else {
                                        errorMessage += ": " + e.getMessage();
                                    }
                                }
                                Log.e("vietdung", "Ingredient Add: " + errorMessage );
                                showToast(errorMessage);
                                setButtonsEnabled(true);
                            });
                        }
                    });
        } else {
            ingredientDAO.addIngredient(ingredient,
                    aVoid -> {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                showToast("Thêm nguyên liệu thành công");
                                clearInputs();
                                loadData();
                                setButtonsEnabled(true);
                            });
                        }
                    },
                    e -> {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                showToast("Lỗi khi thêm nguyên liệu: " + e.getMessage());
                                Log.e("vietdung", "Ingredient Add 2: " + e.getMessage() );
                                setButtonsEnabled(true);
                            });
                        }
                    });
        }
    }

    @Override
    protected void updateItem() {
        if (selectedIngredient == null) {
            showToast("Vui lòng chọn một nguyên liệu");
            return;
        }

        String name = etName.getText().toString();
        String description = etDescription.getText().toString();
        int unitPosition = spinnerUnit.getSelectedItemPosition();

        if (name.isEmpty() || description.isEmpty() || unitPosition <= 0) {
            showToast("Vui lòng điền đầy đủ thông tin");
            return;
        }

        String unit = donViDoLuong[unitPosition - 1]; // -1 because of empty option
        selectedIngredient.setName(name);
        selectedIngredient.setDescription(description);
        selectedIngredient.setUnit(unit);

        setButtonsEnabled(false);
        if (selectedImageUri != null) {
            ingredientDAO.updateIngredientWithImage(getContext(), selectedIngredient, selectedImageUri,
                    aVoid -> {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                showToast("Cập nhật nguyên liệu thành công");
                                clearInputs();
                                loadData();
                                setButtonsEnabled(true);
                            });
                        }
                    },
                    e -> {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                String errorMessage = "Lỗi khi cập nhật nguyên liệu";
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
            ingredientDAO.updateIngredient(selectedIngredient,
                    aVoid -> {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                showToast("Cập nhật nguyên liệu thành công");
                                clearInputs();
                                loadData();
                                setButtonsEnabled(true);
                            });
                        }
                    },
                    e -> {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                showToast("Lỗi khi cập nhật nguyên liệu: " + e.getMessage());
                                setButtonsEnabled(true);
                            });
                        }
                    });
        }
    }

    @Override
    protected void deleteItem() {
        if (selectedIngredient == null) {
            showToast("Vui lòng chọn một nguyên liệu");
            return;
        }

        new AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa nguyên liệu này?")
            .setPositiveButton("Có", (dialog, which) -> {
                setButtonsEnabled(false);
                ingredientDAO.deleteIngredient(selectedIngredient.getUuid(),
                    aVoid -> {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                showToast("Xóa nguyên liệu thành công");
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
            })
            .setNegativeButton("Không", null)
            .show();
    }
} 