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
import com.b21dccn216.pocketcocktail.dao.UserDAO;
import com.b21dccn216.pocketcocktail.model.User;
import com.b21dccn216.pocketcocktail.test_database.adapter.UserAdapter;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class UserFragment extends BaseModelFragment {
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etName, etEmail, etPassword;
    private Button btnChooseImage, btnSave, btnUpdate, btnDelete;
    private ImageView ivPreview;
    private ListView lvUsers;
    private UserAdapter adapter;
    private List<User> users;
    private User selectedUser;
    private UserDAO userDAO;
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
        return R.layout.test_database_fragment_user;
    }

    @Override
    protected void initViews() {
        etName = rootView.findViewById(R.id.etName);
        etEmail = rootView.findViewById(R.id.etEmail);
        etPassword = rootView.findViewById(R.id.etPassword);
        btnChooseImage = rootView.findViewById(R.id.btnChooseImage);
        btnSave = rootView.findViewById(R.id.btnSave);
        btnUpdate = rootView.findViewById(R.id.btnUpdate);
        btnDelete = rootView.findViewById(R.id.btnDelete);
        ivPreview = rootView.findViewById(R.id.ivPreview);
        lvUsers = rootView.findViewById(R.id.lvUsers);

        users = new ArrayList<>();
        adapter = new UserAdapter(getContext(), users);
        lvUsers.setAdapter(adapter);
        userDAO = new UserDAO();

        setupListeners();
        loadData();
    }

    private void setupListeners() {
        btnChooseImage.setOnClickListener(v -> openImagePicker());
        btnSave.setOnClickListener(v -> saveItem());
        btnUpdate.setOnClickListener(v -> updateItem());
        btnDelete.setOnClickListener(v -> deleteItem());

        lvUsers.setOnItemClickListener((parent, view, position, id) -> {
            selectedUser = users.get(position);
            fillInputs(selectedUser);
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
        userDAO.getAllUsers(new UserDAO.UserListCallback() {
            @Override
            public void onUserListLoaded(List<User> userList) {
                users.clear();
                users.addAll(userList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                showToast("Error loading users: " + e.getMessage());
            }
        });
    }

    @Override
    protected void clearInputs() {
        etName.setText("");
        etEmail.setText("");
        etPassword.setText("");
        selectedImageUri = null;
        selectedUser = null;
        ivPreview.setImageResource(R.drawable.cocktail_logo);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    @Override
    protected void fillInputs(Object item) {
        if (item instanceof User) {
            User user = (User) item;
            etName.setText(user.getName());
            etEmail.setText(user.getEmail());
            etPassword.setText(user.getPassword());
            
            if (user.getImage() != null && !user.getImage().isEmpty()) {
                Glide.with(this)
                        .load(user.getImage())
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
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showToast("Please fill all required fields");
            return;
        }

        User user = new User(name, email, password, null);
        user.generateUUID();

        if (selectedImageUri != null) {
            userDAO.addUserWithImage(getContext(), user, selectedImageUri,
                    aVoid -> {
                        showToast("User added successfully");
                        clearInputs();
                        loadData();
                    },
                    e -> showToast("Error adding user: " + e.getMessage()));
        } else {
            userDAO.addUser(user,
                    aVoid -> {
                        showToast("User added successfully");
                        clearInputs();
                        loadData();
                    },
                    e -> showToast("Error adding user: " + e.getMessage()));
        }
    }

    @Override
    protected void updateItem() {
        if (selectedUser == null) {
            showToast("Please select a user first");
            return;
        }

        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showToast("Please fill all required fields");
            return;
        }

        selectedUser.setName(name);
        selectedUser.setEmail(email);
        selectedUser.setPassword(password);

        if (selectedImageUri != null) {
            userDAO.updateUserWithImage(getContext(), selectedUser, selectedImageUri,
                    aVoid -> {
                        showToast("User updated successfully");
                        clearInputs();
                        loadData();
                    },
                    e -> showToast("Error updating user: " + e.getMessage()));
        } else {
            userDAO.updateUser(selectedUser,
                    aVoid -> {
                        showToast("User updated successfully");
                        clearInputs();
                        loadData();
                    },
                    e -> showToast("Error updating user: " + e.getMessage()));
        }
    }

    @Override
    protected void deleteItem() {
        if (selectedUser == null) {
            showToast("Please select a user first");
            return;
        }

        userDAO.deleteUser(selectedUser.getUuid(),
                aVoid -> {
                    showToast("User deleted successfully");
                    clearInputs();
                    loadData();
                },
                e -> showToast("Error deleting user: " + e.getMessage()));
    }
} 