package com.b21dccn216.pocketcocktail.view.CreateDrink;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.dao.IngredientDAO;
import com.b21dccn216.pocketcocktail.model.Ingredient;

public class CreateIngredientActivity extends AppCompatActivity {
    private EditText etName, etDescription, etUnit;
    private ImageView ivImage;
    private Button btnAddImage, btnSave;
    private Uri selectedImageUri;
    private IngredientDAO ingredientDAO;
 
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
        etUnit = findViewById(R.id.etUnit);
        ivImage = findViewById(R.id.ivImage);
        btnAddImage = findViewById(R.id.btnAddImage);
        btnSave = findViewById(R.id.btnSave);

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

    private boolean validateInput() {
        if (etName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên nguyên liệu", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etDescription.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mô tả", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etUnit.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đơn vị", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedImageUri == null) {
            Toast.makeText(this, "Vui lòng thêm ảnh nguyên liệu", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveIngredient() {
        btnSave.setEnabled(false); // Disable nút lưu ngay khi bắt đầu
        Ingredient ingredient = new Ingredient(
                etName.getText().toString().trim(),
                etDescription.getText().toString().trim(),
                etUnit.getText().toString().trim()
        );
        ingredient.generateUUID();

        ingredientDAO.addIngredientWithImage(this, ingredient, selectedImageUri,
                aVoid -> {
                    Toast.makeText(this, "Thêm nguyên liệu thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                },
                e -> {
                    Toast.makeText(this, "Lỗi khi thêm nguyên liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnSave.setEnabled(true); // Enable lại nếu lỗi
                });
    }
} 
