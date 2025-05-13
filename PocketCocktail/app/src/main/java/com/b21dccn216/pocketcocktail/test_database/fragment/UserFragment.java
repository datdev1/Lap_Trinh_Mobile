package com.b21dccn216.pocketcocktail.test_database.fragment;

import android.content.Intent;
import android.net.Uri;
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
import com.b21dccn216.pocketcocktail.dao.UserDAO;
import com.b21dccn216.pocketcocktail.model.User;
import com.b21dccn216.pocketcocktail.test_database.adapter.UserAdapter;
import com.bumptech.glide.Glide;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserFragment extends BaseModelFragment {
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etUuid, etSaveUuidFromAuthen, etCreatedAt, etUpdatedAt, etName, etEmail, etPassword;
    private Spinner spinnerRole;
    private Button btnChooseImage, btnSave, btnUpdate, btnDelete, btnClear;
    private ImageView ivPreview;
    private ListView lvUsers;
    private UserAdapter adapter;
    private List<User> users;
    private User selectedUser;
    private UserDAO userDAO;
    private Uri selectedImageUri;
    private SimpleDateFormat dateFormat;

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
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        
        etUuid = rootView.findViewById(R.id.etUuid);
        etSaveUuidFromAuthen = rootView.findViewById(R.id.etSaveUuidFromAuthen);
        etCreatedAt = rootView.findViewById(R.id.etCreatedAt);
        etUpdatedAt = rootView.findViewById(R.id.etUpdatedAt);
        etName = rootView.findViewById(R.id.etName);
        etEmail = rootView.findViewById(R.id.etEmail);
        etPassword = rootView.findViewById(R.id.etPassword);
        spinnerRole = rootView.findViewById(R.id.spinnerRole);
        btnChooseImage = rootView.findViewById(R.id.btnChooseImage);
        btnSave = rootView.findViewById(R.id.btnSave);
        btnUpdate = rootView.findViewById(R.id.btnUpdate);
        btnDelete = rootView.findViewById(R.id.btnDelete);
        btnClear = rootView.findViewById(R.id.btnClear);
        ivPreview = rootView.findViewById(R.id.ivPreview);
        lvUsers = rootView.findViewById(R.id.lvUsers);

        // Initially enable email and password for new user creation
        setEmailPasswordEnabled(true);

        users = new ArrayList<>();
        adapter = new UserAdapter(getContext(), users);
        lvUsers.setAdapter(adapter);
        userDAO = new UserDAO();

        setupRoleSpinner();
        setupListeners();
        loadData();
    }

    private void setupRoleSpinner() {
        List<String> roles = new ArrayList<>();
        roles.add(User.RoleUser);
        roles.add(User.RoleAdmin);
        
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(
            getContext(),
            android.R.layout.simple_spinner_item,
            roles
        );
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(roleAdapter);
    }

    private void setEmailPasswordEnabled(boolean enabled) {
        etEmail.setEnabled(enabled);
        etPassword.setEnabled(enabled);
    }

    private void setupListeners() {
        btnChooseImage.setOnClickListener(v -> openImagePicker());
        btnSave.setOnClickListener(v -> saveItem());
        btnUpdate.setOnClickListener(v -> updateItem());
        btnDelete.setOnClickListener(v -> deleteItem());
        btnClear.setOnClickListener(v -> clearInputs());

        lvUsers.setOnItemClickListener((parent, view, position, id) -> {
            selectedUser = users.get(position);
            fillInputs(selectedUser);
            btnUpdate.setEnabled(true);
            btnDelete.setEnabled(true);
            setEmailPasswordEnabled(false); // Disable email and password when user is selected
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private String encryptPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password;
        }
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
        etUuid.setText("");
        etSaveUuidFromAuthen.setText("");
        etCreatedAt.setText("");
        etUpdatedAt.setText("");
        etName.setText("");
        etEmail.setText("");
        etPassword.setText("");
        spinnerRole.setSelection(0);
        selectedImageUri = null;
        selectedUser = null;
        ivPreview.setImageResource(R.drawable.cocktail_logo);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        setEmailPasswordEnabled(true); // Enable email and password for new user
        lvUsers.clearChoices(); // Clear selection in list
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void fillInputs(Object item) {
        if (item instanceof User) {
            User user = (User) item;
            etUuid.setText(user.getUuid());
            etSaveUuidFromAuthen.setText(user.getSaveUuidFromAuthen());
            etCreatedAt.setText(dateFormat.format(user.getCreatedAt()));
            etUpdatedAt.setText(dateFormat.format(user.getUpdatedAt()));
            etName.setText(user.getName());
            etEmail.setText(user.getEmail());
            etPassword.setText(user.getPassword());
            
            // Set role spinner
            int rolePosition = user.getRole().equals(User.RoleAdmin) ? 1 : 0;
            spinnerRole.setSelection(rolePosition);
            
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
        String role = spinnerRole.getSelectedItem().toString();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showToast("Please fill all required fields");
            return;
        }

        // Encrypt password
        String encryptedPassword = encryptPassword(password);

        User user = new User(name, email, encryptedPassword, null);
        user.setRole(role);
        user.generateUUID();

        if (selectedImageUri != null) {
            userDAO.addUserWithImage(getContext(), user, selectedImageUri,
                    aVoid -> {
                        showToast("User added successfully");
                        clearInputs();
                        loadData();
                    },
                    e -> {
                        showToast("Error adding user: " + e.getMessage());
                        Log.d("UserDAO", "Error adding user: " + e.getMessage());
                    });
        } else {
            userDAO.addUser(user,
                    aVoid -> {
                        showToast("User added successfully");
                        clearInputs();
                        loadData();
                    },
                    e -> {
                        showToast(e.getMessage());
                        Log.d("UserDAO", "Error adding user: " + e.getMessage());
                    });
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
        String role = spinnerRole.getSelectedItem().toString();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showToast("Please fill all required fields");
            return;
        }

        selectedUser.setName(name);
        selectedUser.setEmail(email);
        selectedUser.setRole(role);

        // Only encrypt and update password if it has changed
        if (!password.equals(selectedUser.getPassword())) {
            String encryptedPassword = encryptPassword(password);
            selectedUser.setPassword(encryptedPassword);
        }

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