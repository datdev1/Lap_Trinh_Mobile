package com.b21dccn216.pocketcocktail.test_database.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
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

    private void setButtonsEnabled(boolean enabled) {
        btnChooseImage.setEnabled(enabled);
        btnSave.setEnabled(enabled);
        btnUpdate.setEnabled(enabled && selectedUser != null);
        btnDelete.setEnabled(enabled && selectedUser != null);
        btnClear.setEnabled(enabled);
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
                userList.sort((c1, c2) -> c2.getUpdatedAt().compareTo(c1.getUpdatedAt()));
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
        ivPreview.setImageResource(R.drawable.place_holder_drink);
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
        if (selectedUser != null) {
            new AlertDialog.Builder(requireContext())
                .setTitle("Cảnh báo")
                .setMessage("Bạn đang chọn một người dùng. Bạn có muốn tạo mới không?")
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
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String role = spinnerRole.getSelectedItem().toString();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showToast("Vui lòng điền đầy đủ thông tin");
            return;
        }

        // Check if user with same email exists
        boolean userExists = false;
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                userExists = true;
                break;
            }
        }

        if (userExists) {
            new AlertDialog.Builder(requireContext())
                .setTitle("Cảnh báo")
                .setMessage("Đã tồn tại người dùng với email này. Bạn có chắc chắn muốn tạo mới?")
                .setPositiveButton("Có", (dialog, which) -> {
                    performSave(name, email, password, role);
                })
                .setNegativeButton("Không", null)
                .show();
        } else {
            performSave(name, email, password, role);
        }
    }

    private void performSave(String name, String email, String password, String role) {
        // Encrypt password
        String encryptedPassword = encryptPassword(password);

        User user = new User(name, email, encryptedPassword, null);
        user.setRole(role);
        user.generateUUID();

        setButtonsEnabled(false);
        if (selectedImageUri != null) {
            userDAO.addUserWithImage(getContext(), user, selectedImageUri,
                    aVoid -> {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                showToast("Thêm người dùng thành công");
                                clearInputs();
                                loadData();
                                setButtonsEnabled(true);
                            });
                        }
                    },
                    e -> {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                String errorMessage = "Lỗi khi thêm người dùng";
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
            userDAO.addUser(user,
                    aVoid -> {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                showToast("Thêm người dùng thành công");
                                clearInputs();
                                loadData();
                                setButtonsEnabled(true);
                            });
                        }
                    },
                    e -> {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                showToast("Lỗi khi thêm người dùng: " + e.getMessage());
                                setButtonsEnabled(true);
                            });
                        }
                    });
        }
    }

    @Override
    protected void updateItem() {
        if (selectedUser == null) {
            showToast("Vui lòng chọn một người dùng");
            return;
        }

        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String role = spinnerRole.getSelectedItem().toString();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showToast("Vui lòng điền đầy đủ thông tin");
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

        setButtonsEnabled(false);
        if (selectedImageUri != null) {
            userDAO.updateUserWithImage(getContext(), selectedUser, selectedImageUri,
                    aVoid -> {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                showToast("Cập nhật người dùng thành công");
                                clearInputs();
                                loadData();
                                setButtonsEnabled(true);
                            });
                        }
                    },
                    e -> {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                String errorMessage = "Lỗi khi cập nhật người dùng";
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
            userDAO.updateUser(selectedUser,
                    aVoid -> {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                showToast("Cập nhật người dùng thành công");
                                clearInputs();
                                loadData();
                                setButtonsEnabled(true);
                            });
                        }
                    },
                    e -> {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                showToast("Lỗi khi cập nhật người dùng: " + e.getMessage());
                                setButtonsEnabled(true);
                            });
                        }
                    });
        }
    }

    @Override
    protected void deleteItem() {
        if (selectedUser == null) {
            showToast("Vui lòng chọn một người dùng");
            return;
        }

        new AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa người dùng này?")
            .setPositiveButton("Có", (dialog, which) -> {
                setButtonsEnabled(false);
                userDAO.deleteUser(selectedUser.getUuid(),
                    aVoid -> {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                showToast("Xóa người dùng thành công");
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