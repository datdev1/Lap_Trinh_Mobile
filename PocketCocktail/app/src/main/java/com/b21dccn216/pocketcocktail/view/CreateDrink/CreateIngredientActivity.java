package com.b21dccn216.pocketcocktail.view.CreateDrink;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.dao.IngredientDAO;
import com.b21dccn216.pocketcocktail.model.Ingredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateIngredientActivity extends AppCompatActivity {
    private EditText etName, etDescription;
    private Spinner spinnerUnit;
    private ImageView ivImage;
    private Button btnAddImage, btnSave;
    private Uri selectedImageUri;
    private IngredientDAO ingredientDAO;
    private final String[] donViDoLuong = {
        "ml", "l", "g", "kg",
        "piece", "leaves", "slice", "cup",
        "tsp ~ 5ml", "tbsp ~ 15ml",
        "pinch ~ 1/8 tsp", "drop", "dash ~ 0.92ml",
        "shot ~ 30ml", "pump ~ 5-10ml",
        "oz ~ 30ml", "cl ~ 10ml", "pt ~ 500ml", "qt ~ 1l", "gal ~ 4l"
    };
 
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    ivImage.setImageURI(selectedImageUri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_ingredient);

        // Khởi tạo DAO
        ingredientDAO = new IngredientDAO();

        // Ánh xạ view
        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        ivImage = findViewById(R.id.ivImage);
        btnAddImage = findViewById(R.id.btnAddImage);
        btnSave = findViewById(R.id.btnSave);

        setupUnitSpinner();

        // Xử lý sự kiện thêm ảnh
        btnAddImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImage.launch(intent);
        });

        // Xử lý sự kiện lưu nguyên liệu
        btnSave.setOnClickListener(v -> {
            if (validateInput()) {
                saveIngredient();
            }
        });
    }

    private void setupUnitSpinner() {
        List<String> unitList = new ArrayList<>();
        unitList.add("Unit (ml, g, piece,...)"); // Add empty option
        unitList.addAll(Arrays.asList(donViDoLuong));
        
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
            this,
            R.layout.spinner_item,
            unitList
        );
        unitAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerUnit.setAdapter(unitAdapter);
        spinnerUnit.setSelection(0); // Set to empty by default
    }

    private boolean validateInput() {
        if (etName.getText().toString().trim().isEmpty()) {
            Toast.makeText(CreateIngredientActivity.this, "Vui lòng nhập tên nguyên liệu", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etDescription.getText().toString().trim().isEmpty()) {
            Toast.makeText(CreateIngredientActivity.this, "Vui lòng nhập mô tả", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (spinnerUnit.getSelectedItemPosition() <= 0) {
            Toast.makeText(CreateIngredientActivity.this, "Vui lòng chọn đơn vị", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedImageUri == null) {
            Toast.makeText(CreateIngredientActivity.this, "Vui lòng thêm ảnh nguyên liệu", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveIngredient() {
        btnSave.setEnabled(false); // Disable nút lưu ngay khi bắt đầu
        String unit = donViDoLuong[spinnerUnit.getSelectedItemPosition() - 1]; // -1 because of empty option
        Ingredient ingredient = new Ingredient(
                etName.getText().toString().trim(),
                etDescription.getText().toString().trim(),
                unit
        );
        ingredient.generateUUID();

        ingredientDAO.addIngredientWithImage(this, ingredient, selectedImageUri,
                aVoid -> {
                    Toast.makeText(CreateIngredientActivity.this, "Thêm nguyên liệu thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                },
                e -> {
                    runOnUiThread(() -> {
                        String errorMessage = "Lỗi khi thêm nguyên liệu";
                        if (e.getMessage() != null) {
                            String msg = e.getMessage();
                            if (msg.contains("webp")) {
                                errorMessage = "Định dạng ảnh WebP không được hỗ trợ. Vui lòng chọn ảnh khác.";
                            } else if (msg.contains("400") || msg.toLowerCase().contains("upload failed")) {
                                errorMessage = "Ảnh không hợp lệ hoặc không được hỗ trợ. Vui lòng chọn ảnh khác!";
                            } else {
                                errorMessage += ": " + msg;
                            }
                        }
                        Toast.makeText(CreateIngredientActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        btnSave.setEnabled(true); // Enable lại nếu lỗi
                    });
                });
    }
} 
