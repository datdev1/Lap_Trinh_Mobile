package com.b21dccn216.pocketcocktail.test_database.fragment;

import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.dao.CategoryDAO;
import com.b21dccn216.pocketcocktail.dao.DrinkDAO;
import com.b21dccn216.pocketcocktail.dao.UserDAO;
import com.b21dccn216.pocketcocktail.model.Category;
import com.b21dccn216.pocketcocktail.model.Drink;
import com.b21dccn216.pocketcocktail.model.User;
import com.b21dccn216.pocketcocktail.test_database.adapter.DrinkAdapter;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DrinkFragment extends BaseModelFragment {
    private static final String TAG = "DrinkFragment";
    private static final int PAGE_SIZE = 10;
    private static final int SEARCH_DELAY = 500; // Delay in milliseconds

    private EditText etName, etDescription, etInstruction, etRate, etSearch;
    private EditText etUuid, etCreatedAt, etUpdatedAt;
    private Spinner spinnerUser, spinnerCategory;
    private Button btnSelectImage, btnSave, btnUpdate, btnDelete, btnLoadMore;
    private ImageView ivImage;
    private ListView lvDrinks;
    private DrinkAdapter adapter;
    private List<Drink> drinks;
    private Drink selectedDrink;
    private DrinkDAO drinkDAO;
    private UserDAO userDAO;
    private CategoryDAO categoryDAO;
    private Uri selectedImageUri;
    private DocumentSnapshot lastVisible;
    private boolean isLoading = false;
    private String currentSearchQuery = "";
    private android.os.Handler searchHandler = new android.os.Handler();
    private Runnable searchRunnable;
    private List<User> users;
    private List<Category> categories;
    private SimpleDateFormat dateFormat;

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
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        
        etSearch = rootView.findViewById(R.id.etSearch);
        etName = rootView.findViewById(R.id.etName);
        etDescription = rootView.findViewById(R.id.etDescription);
        etInstruction = rootView.findViewById(R.id.etInstruction);
        etRate = rootView.findViewById(R.id.etRate);
        etUuid = rootView.findViewById(R.id.etUuid);
        etCreatedAt = rootView.findViewById(R.id.etCreatedAt);
        etUpdatedAt = rootView.findViewById(R.id.etUpdatedAt);
        spinnerUser = rootView.findViewById(R.id.spinnerUser);
        spinnerCategory = rootView.findViewById(R.id.spinnerCategory);
        
        btnSelectImage = rootView.findViewById(R.id.btnSelectImage);
        btnSave = rootView.findViewById(R.id.btnSave);
        btnUpdate = rootView.findViewById(R.id.btnUpdate);
        btnDelete = rootView.findViewById(R.id.btnDelete);
        btnLoadMore = rootView.findViewById(R.id.btnLoadMore);
        ivImage = rootView.findViewById(R.id.ivImage);
        lvDrinks = rootView.findViewById(R.id.lvDrinks);

        drinks = new ArrayList<>();
        users = new ArrayList<>();
        categories = new ArrayList<>();
        
        adapter = new DrinkAdapter(getContext(), drinks);
        lvDrinks.setAdapter(adapter);
        
        drinkDAO = new DrinkDAO();
        userDAO = new UserDAO();
        categoryDAO = new CategoryDAO();

        setupListeners();
        setupSearchListener();
        loadUsers();
        loadCategories();
        loadFirstPage();
        Log.d(TAG, "Views initialized successfully");
    }

    private void loadUsers() {
        userDAO.getAllUsers(new UserDAO.UserListCallback() {
            @Override
            public void onUserListLoaded(List<User> userList) {
                userList.sort((c1, c2) -> c2.getUpdatedAt().compareTo(c1.getUpdatedAt()));
                users.clear();
                users.addAll(userList);
                List<String> userDisplayList = new ArrayList<>();
                userDisplayList.add(""); // Add empty option
                for (User user : users) {
                    userDisplayList.add(user.getName() + " (" + user.getUuid() + ")");
                }
                ArrayAdapter<String> userAdapter = new ArrayAdapter<>(
                    getContext(),
                    android.R.layout.simple_spinner_item,
                    userDisplayList
                );
                userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerUser.setAdapter(userAdapter);
                spinnerUser.setSelection(0); // Set to empty by default
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error loading users: " + e.getMessage());
                showToast("Error loading users: " + e.getMessage());
            }
        });
    }

    private void loadCategories() {
        categoryDAO.getAllCategorys(new CategoryDAO.CategoryListCallback() {
            @Override
            public void onCategoryListLoaded(List<Category> categoryList) {
                categoryList.sort((c1, c2) -> c2.getUpdatedAt().compareTo(c1.getUpdatedAt()));
                categories.clear();
                categories.addAll(categoryList);
                List<String> categoryDisplayList = new ArrayList<>();
                categoryDisplayList.add(""); // Add empty option
                for (Category category : categories) {
                    categoryDisplayList.add(category.getName() + " (" + category.getUuid() + ")");
                }
                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                    getContext(),
                    android.R.layout.simple_spinner_item,
                    categoryDisplayList
                );
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategory.setAdapter(categoryAdapter);
                spinnerCategory.setSelection(0); // Set to empty by default
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error loading categories: " + e.getMessage());
                showToast("Error loading categories: " + e.getMessage());
            }
        });
    }

    private void setupListeners() {
        Log.d(TAG, "Setting up listeners");
        btnSelectImage.setOnClickListener(v -> openImagePicker());
        btnSave.setOnClickListener(v -> saveItem());
        btnUpdate.setOnClickListener(v -> updateItem());
        btnDelete.setOnClickListener(v -> deleteItem());
        btnLoadMore.setOnClickListener(v -> loadMoreDrinks());

        lvDrinks.setOnItemClickListener((parent, view, position, id) -> {
            selectedDrink = drinks.get(position);
            Log.d(TAG, "Drink selected: " + selectedDrink.getName());
            fillInputs(selectedDrink);
            btnUpdate.setEnabled(true);
            btnDelete.setEnabled(true);
        });
    }

    private void setButtonsEnabled(boolean enabled) {
        btnSelectImage.setEnabled(enabled);
        btnSave.setEnabled(enabled);
        btnUpdate.setEnabled(enabled && selectedDrink != null);
        btnDelete.setEnabled(enabled && selectedDrink != null);
        btnLoadMore.setEnabled(enabled);
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
                        searchDrinks(query);
                    }
                };
                searchHandler.postDelayed(searchRunnable, SEARCH_DELAY);
            }
        });
    }

    private void searchDrinks(String query) {
        if (query.isEmpty()) {
            loadFirstPage();
            return;
        }

        isLoading = true;
        btnLoadMore.setVisibility(View.GONE); // Hide load more button during search
        drinkDAO.searchDrinksWithSort(
                query,
                PAGE_SIZE,
                null,
                DrinkDAO.DRINK_FIELD.NAME,
                Query.Direction.ASCENDING,
                new DrinkDAO.DrinkListWithLastDocCallback() {
                    @Override
                    public void onDrinkListLoaded(List<Drink> drinkList, DocumentSnapshot lastVisible) {
                        Log.d(TAG, "Search: onDrinkListLoaded: " + drinkList);
                        drinks.clear();
                        drinks.addAll(drinkList);
                        DrinkFragment.this.lastVisible = lastVisible;
                        adapter.notifyDataSetChanged();
                        isLoading = false;
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "Error searching drinks: " + e.getMessage(), e);
                        showToast("Error searching drinks: " + e.getMessage());
                        isLoading = false;
                    }
                }
        );
    }

    private void loadFirstPage() {
        isLoading = true;
        btnLoadMore.setVisibility(View.VISIBLE); // Show load more button for normal pagination
        btnLoadMore.setEnabled(false);
        drinkDAO.getAllDrinksWithLimitAndSort(
                DrinkDAO.DRINK_FIELD.UPDATED_AT,
                Query.Direction.DESCENDING,
                PAGE_SIZE,
                null,
                new DrinkDAO.DrinkListWithLastDocCallback() {
                    @Override
                    public void onDrinkListLoaded(List<Drink> drinkList, DocumentSnapshot lastVisible) {
                        Log.d(TAG, "Load first: onDrinkListLoaded: " + drinkList);
                        drinks.clear();
                        drinks.addAll(drinkList);
                        DrinkFragment.this.lastVisible = lastVisible;
                        adapter.notifyDataSetChanged();
                        btnLoadMore.setEnabled(true);
                        isLoading = false;
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "Error loading drinks: " + e.getMessage(), e);
                        showToast("Error loading drinks: " + e.getMessage());
                        btnLoadMore.setEnabled(true);
                        isLoading = false;
                    }
                }
        );
    }

    private void loadMoreDrinks() {
        if (isLoading || lastVisible == null) return;
        
        isLoading = true;
        btnLoadMore.setEnabled(false);

        if (!currentSearchQuery.isEmpty()) {
            // Load more search results
            drinkDAO.searchDrinksWithSort(
                    currentSearchQuery,
                    PAGE_SIZE,
                    lastVisible,
                    DrinkDAO.DRINK_FIELD.NAME,
                    Query.Direction.ASCENDING,
                    new DrinkDAO.DrinkListWithLastDocCallback() {
                        @Override
                        public void onDrinkListLoaded(List<Drink> drinkList, DocumentSnapshot lastVisible) {
                            Log.d(TAG, "Load more: onDrinkListLoaded: " + drinkList);
                            if (!drinkList.isEmpty()) {
                                drinks.addAll(drinkList);
                                DrinkFragment.this.lastVisible = lastVisible;
                                adapter.notifyDataSetChanged();
                            }
                            btnLoadMore.setEnabled(true);
                            isLoading = false;
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e(TAG, "Error loading more search results: " + e.getMessage(), e);
                            showToast("Error loading more results: " + e.getMessage());
                            btnLoadMore.setEnabled(true);
                            isLoading = false;
                        }
                    }
            );
        } else {
            // Load more normal results
            drinkDAO.getAllDrinksWithLimitAndSort(
                    DrinkDAO.DRINK_FIELD.CREATED_AT,
                    Query.Direction.DESCENDING,
                    PAGE_SIZE,
                    lastVisible,
                    new DrinkDAO.DrinkListWithLastDocCallback() {
                        @Override
                        public void onDrinkListLoaded(List<Drink> drinkList, DocumentSnapshot lastVisible) {
                            if (!drinkList.isEmpty()) {
                                drinks.addAll(drinkList);
                                DrinkFragment.this.lastVisible = lastVisible;
                                adapter.notifyDataSetChanged();
                            }
                            btnLoadMore.setEnabled(true);
                            isLoading = false;
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e(TAG, "Error loading more drinks: " + e.getMessage(), e);
                            showToast("Error loading more drinks: " + e.getMessage());
                            btnLoadMore.setEnabled(true);
                            isLoading = false;
                        }
                    }
            );
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    @Override
    protected void loadData() {
        loadFirstPage();
    }

    @Override
    protected void clearInputs() {
        Log.d(TAG, "Clearing inputs");
        etName.setText("");
        etDescription.setText("");
        etInstruction.setText("");
        etRate.setText("");
        etUuid.setText("");
        etCreatedAt.setText("");
        etUpdatedAt.setText("");
        spinnerUser.setSelection(0);
        spinnerCategory.setSelection(0);
        selectedImageUri = null;
        selectedDrink = null;
        ivImage.setImageResource(R.drawable.icon_default_drink);
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
            etRate.setText(String.valueOf(drink.getRate()));
            etUuid.setText(drink.getUuid());
            etCreatedAt.setText(dateFormat.format(drink.getCreatedAt()));
            etUpdatedAt.setText(dateFormat.format(drink.getUpdatedAt()));
            
            // Set user spinner
            boolean userFound = false;
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getUuid().equals(drink.getUserId())) {
                    spinnerUser.setSelection(i + 1); // +1 because of empty option
                    userFound = true;
                    break;
                }
            }
            if (!userFound) {
                spinnerUser.setSelection(0); // Set to empty if user not found
            }
            
            // Set category spinner
            boolean categoryFound = false;
            for (int i = 0; i < categories.size(); i++) {
                if (categories.get(i).getUuid().equals(drink.getCategoryId())) {
                    spinnerCategory.setSelection(i + 1); // +1 because of empty option
                    categoryFound = true;
                    break;
                }
            }
            if (!categoryFound) {
                spinnerCategory.setSelection(0); // Set to empty if category not found
            }
            
            if (drink.getImage() != null && !drink.getImage().isEmpty()) {
                Log.d(TAG, "Loading drink image: " + drink.getImage());
                Glide.with(this)
                        .load(drink.getImage())
                        .placeholder(R.drawable.icon_default_drink)
                        .error(R.drawable.error_icon)
                        .into(ivImage);
            } else {
                ivImage.setImageResource(R.drawable.icon_default_drink);
            }
        }
    }

    @Override
    protected void saveItem() {
        if (selectedDrink != null) {
            new AlertDialog.Builder(requireContext())
                .setTitle("Cảnh báo")
                .setMessage("Bạn đang chọn một đồ uống. Bạn có muốn tạo mới không?")
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
        String instruction = etInstruction.getText().toString();
        String rateStr = etRate.getText().toString();

        if (name.isEmpty() || description.isEmpty() || instruction.isEmpty() || rateStr.isEmpty()) {
            Log.w(TAG, "Save failed: Required fields are empty");
            showToast("Vui lòng điền đầy đủ thông tin");
            return;
        }

        double rate;
        try {
            rate = Double.parseDouble(rateStr);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Save failed: Invalid rate value", e);
            showToast("Giá trị đánh giá không hợp lệ");
            return;
        }

        // Get selected user and category
        int userPosition = spinnerUser.getSelectedItemPosition();
        int categoryPosition = spinnerCategory.getSelectedItemPosition();
        
        if (userPosition <= 0 || categoryPosition <= 0) { // Check if empty option is selected
            showToast("Vui lòng chọn người dùng và danh mục");
            return;
        }

        User selectedUser = users.get(userPosition - 1); // -1 because of empty option
        Category selectedCategory = categories.get(categoryPosition - 1); // -1 because of empty option

        Drink drink = new Drink(name, selectedUser.getUuid(), "", selectedCategory.getUuid(), instruction, description, rate);
        drink.generateUUID();
        Log.d(TAG, "Creating new drink: " + drink.getName() + " with UUID: " + drink.getUuid());

        setButtonsEnabled(false);

        if (selectedImageUri != null) {
            Log.d(TAG, "Saving drink with image");
            drinkDAO.addDrinkWithImage(getContext(), drink, selectedImageUri,
                    aVoid -> {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                Log.d(TAG, "Drink saved successfully with image");
                                showToast("Thêm đồ uống thành công");
                                clearInputs();
                                loadFirstPage(); // Reload first page after adding new drink
                                setButtonsEnabled(true);
                            });
                        }
                    },
                    e -> {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                Log.e(TAG, "Error saving drink with image", e);
                                String errorMessage = "Lỗi khi thêm đồ uống";
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
            Log.d(TAG, "Saving drink without image");
            drinkDAO.addDrink(drink,
                    aVoid -> {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                Log.d(TAG, "Drink saved successfully");
                                showToast("Thêm đồ uống thành công");
                                clearInputs();
                                loadFirstPage(); // Reload first page after adding new drink
                                setButtonsEnabled(true);
                            });
                        }
                    },
                    e -> {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                Log.e(TAG, "Error saving drink", e);
                                showToast("Lỗi khi thêm đồ uống: " + e.getMessage());
                                setButtonsEnabled(true);
                            });
                        }
                    });
        }
    }

    @Override
    protected void updateItem() {
        if (selectedDrink == null) {
            Log.w(TAG, "Update failed: No drink selected");
            showToast("Vui lòng chọn một đồ uống");
            return;
        }

        String name = etName.getText().toString();
        String description = etDescription.getText().toString();
        String instruction = etInstruction.getText().toString();
        String rateStr = etRate.getText().toString();

        if (name.isEmpty() || description.isEmpty() || instruction.isEmpty() || rateStr.isEmpty()) {
            Log.w(TAG, "Update failed: Required fields are empty");
            showToast("Vui lòng điền đầy đủ thông tin");
            return;
        }

        double rate;
        try {
            rate = Double.parseDouble(rateStr);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Update failed: Invalid rate value", e);
            showToast("Giá trị đánh giá không hợp lệ");
            return;
        }

        // Get selected user and category
        int userPosition = spinnerUser.getSelectedItemPosition();
        int categoryPosition = spinnerCategory.getSelectedItemPosition();
        
        if (userPosition <= 0 || categoryPosition <= 0) { // Check if empty option is selected
            showToast("Vui lòng chọn người dùng và danh mục");
            return;
        }

        User selectedUser = users.get(userPosition - 1); // -1 because of empty option
        Category selectedCategory = categories.get(categoryPosition - 1); // -1 because of empty option

        selectedDrink.setName(name);
        selectedDrink.setDescription(description);
        selectedDrink.setInstruction(instruction);
        selectedDrink.setRate(rate);
        selectedDrink.setUserId(selectedUser.getUuid());
        selectedDrink.setCategoryId(selectedCategory.getUuid());
        
        Log.d(TAG, "Updating drink: " + selectedDrink.getName() + " with UUID: " + selectedDrink.getUuid());

        setButtonsEnabled(false);

        if (selectedImageUri != null) {
            Log.d(TAG, "Updating drink with new image");
            drinkDAO.updateDrinkWithImage(getContext(), selectedDrink, selectedImageUri,
                    aVoid -> {
                        Log.d(TAG, "Drink updated successfully with image");
                        showToast("Cập nhật đồ uống thành công");
                        clearInputs();
                        loadFirstPage(); // Reload first page after updating drink
                        setButtonsEnabled(true);
                    },
                    e -> {
                        Log.e(TAG, "Error updating drink with image", e);
                        showToast("Lỗi khi cập nhật đồ uống: " + e.getMessage());
                        setButtonsEnabled(true);
                    });
        } else {
            Log.d(TAG, "Updating drink without image change");
            drinkDAO.updateDrink(selectedDrink,
                    aVoid -> {
                        Log.d(TAG, "Drink updated successfully");
                        showToast("Cập nhật đồ uống thành công");
                        clearInputs();
                        loadFirstPage(); // Reload first page after updating drink
                        setButtonsEnabled(true);
                    },
                    e -> {
                        Log.e(TAG, "Error updating drink", e);
                        showToast("Lỗi khi cập nhật đồ uống: " + e.getMessage());
                        setButtonsEnabled(true);
                    });
        }
    }

    @Override
    protected void deleteItem() {
        if (selectedDrink == null) {
            Log.w(TAG, "Delete failed: No drink selected");
            showToast("Vui lòng chọn một đồ uống");
            return;
        }

        new AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa đồ uống này?")
            .setPositiveButton("Có", (dialog, which) -> {
                Log.d(TAG, "Deleting drink: " + selectedDrink.getName() + " with UUID: " + selectedDrink.getUuid());
                setButtonsEnabled(false);
                drinkDAO.deleteDrink(getContext(), selectedDrink.getUuid(),
                    aVoid -> {
                        Log.d(TAG, "Drink deleted successfully");
                        showToast("Xóa đồ uống thành công");
                        clearInputs();
                        loadFirstPage(); // Reload first page after deleting drink
                        setButtonsEnabled(true);
                    },
                    e -> {
                        Log.e(TAG, "Error deleting drink", e);
                        showToast(e.getMessage());
                        setButtonsEnabled(true);
                    });
            })
            .setNegativeButton("Không", null)
            .show();
    }


} 