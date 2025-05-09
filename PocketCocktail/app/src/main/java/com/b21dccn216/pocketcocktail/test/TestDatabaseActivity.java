package com.b21dccn216.pocketcocktail.test;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.dao.DrinkDAO;
import com.b21dccn216.pocketcocktail.data.Dao.DrinksDao;
import com.b21dccn216.pocketcocktail.model.Drink;
import com.b21dccn216.pocketcocktail.test.adapter.DrinkAdapter;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestDatabaseActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText edtName, edtDescription, edtInstruction, edtCategoryId, edtRate;
    private ImageView imgPreview;
    private Uri selectedImageUri;
    private RecyclerView recyclerView;
    private DrinkAdapter drinkAdapter;
    private List<Drink> drinkList = new ArrayList<>();
    private Button btnChooseImage, btnCreateDrink, btnUpdateDrink, btnDeleteDrink;
    private DrinkDAO drinkDAO;
    private Drink selectedDrink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_test_database);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ view
        edtName = findViewById(R.id.edtName);
        edtDescription = findViewById(R.id.edtDescription);
        edtInstruction = findViewById(R.id.edtInstruction);
        edtCategoryId = findViewById(R.id.edtCategoryId);
        edtRate = findViewById(R.id.edtRate);
        imgPreview = findViewById(R.id.imgPreview);
        recyclerView = findViewById(R.id.recyclerView);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnCreateDrink = findViewById(R.id.btnCreateDrink);
        btnUpdateDrink = findViewById(R.id.btnUpdateDrink);
        btnDeleteDrink = findViewById(R.id.btnDeleteDrink);

        drinkDAO = new DrinkDAO();

        // Khởi tạo RecyclerView
        drinkAdapter = new DrinkAdapter(this, new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(drinkAdapter);

        // Set click listener cho adapter
        drinkAdapter.setOnDrinkClickListener(drink -> {
            selectedDrink = drink;
            displayDrinkInfo(drink);
            btnUpdateDrink.setEnabled(true);
            btnDeleteDrink.setEnabled(true);
        });

        // Sự kiện chọn ảnh
        btnChooseImage.setOnClickListener(v -> openImagePicker());

        // Sự kiện tạo drink
        btnCreateDrink.setOnClickListener(v -> createDrink());

        // Sự kiện cập nhật drink
        btnUpdateDrink.setOnClickListener(v -> updateDrink());

        // Sự kiện xóa drink
        btnDeleteDrink.setOnClickListener(v -> showDeleteConfirmationDialog());

        // Tải danh sách ban đầu
        loadDrinksFromDatabase();


//        addSampleDrinks();
//
//        loadDrinks();

    }
    private void addSampleDrinks() {
        List<Drink> sampleDrinks = Arrays.asList(
                new Drink("Trà sữa truyền thống", "user_001", "https://www.pinterest.com/pin/48765608460802032/", "cat_tea", "Lắc đều với đá", "Ngon nhất khi uống lạnh", 4.5),
                new Drink("Cà phê sữa đá", "user_002", "https://www.pinterest.com/pin/7459155627005968/", "cat_coffee", "Khuấy đều", "Đậm đà vị Việt", 4.7),
                new Drink("Matcha Latte", "user_003", "https://www.pinterest.com/pin/2603712281133445/", "cat_matcha", "Khuấy đều với sữa", "Hương vị Nhật Bản", 4.2)
        );

        for (Drink drink : sampleDrinks) {
            drink.generateUUID(); // đảm bảo có uuid
            drinkDAO.addDrink(drink,
                    unused -> Log.d("Firestore", "Drink added: " + drink.getName()),
                    e -> Log.e("Firestore", "Lỗi thêm drink", e));
        }
    }
    private void loadDrinks() {
        drinkDAO.getAllDrinks(new DrinkDAO.DrinkListCallback() {
            @Override
            public void onDrinkListLoaded(List<Drink> drinks) {
                drinkList.clear();
                drinkList.addAll(drinks);
                drinkAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(TestDatabaseActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayDrinkInfo(Drink drink) {
        edtName.setText(drink.getName());
        edtDescription.setText(drink.getDescription());
        edtInstruction.setText(drink.getInstruction());
        edtCategoryId.setText(drink.getCategoryId());
        edtRate.setText(String.valueOf(drink.getRate()));
        
        // Load ảnh
        Glide.with(this)
                .load(drink.getImage())
                .placeholder(R.drawable.cocktail_logo)
                .error(R.drawable.error_icon)
                .into(imgPreview);
        
        selectedImageUri = null; // Reset selected image
    }

    private void showDeleteConfirmationDialog() {
        if (selectedDrink == null) return;

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa đồ uống này?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteDrink())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteDrink() {
        if (selectedDrink == null) return;

        drinkDAO.deleteDrink(selectedDrink.getUuid(),
                unused -> {
                    Toast.makeText(this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                    clearForm();
                    loadDrinksFromDatabase();
                },
                e -> Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void updateDrink() {
        if (selectedDrink == null) return;

        String name = edtName.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();
        String instruction = edtInstruction.getText().toString().trim();
        String categoryId = edtCategoryId.getText().toString().trim();
        String rateStr = edtRate.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty() || rateStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        double rate;
        try {
            rate = Double.parseDouble(rateStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Đánh giá không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        selectedDrink.setName(name);
        selectedDrink.setDescription(description);
        selectedDrink.setInstruction(instruction);
        selectedDrink.setCategoryId(categoryId);
        selectedDrink.setRate(rate);

        if (selectedImageUri != null) {
            // Cập nhật với ảnh mới
            drinkDAO.updateDrinkWithImage(this, selectedDrink, selectedImageUri,
                    unused -> {
                        Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        clearForm();
                        loadDrinksFromDatabase();
                    },
                    e -> Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            // Cập nhật không có ảnh mới
            drinkDAO.updateDrink(selectedDrink,
                    unused -> {
                        Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        clearForm();
                        loadDrinksFromDatabase();
                    },
                    e -> Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imgPreview.setImageURI(selectedImageUri);
        }
    }

    private void createDrink() {
        String name = edtName.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();
        String instruction = edtInstruction.getText().toString().trim();
        String categoryId = edtCategoryId.getText().toString().trim();
        String rateStr = edtRate.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty() || rateStr.isEmpty() || selectedImageUri == null) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin và chọn ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        double rate;
        try {
            rate = Double.parseDouble(rateStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Đánh giá không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        Drink drink = new Drink();
        drink.setName(name);
        drink.setDescription(description);
        drink.setInstruction(instruction);
        drink.setCategoryId(categoryId);
        drink.setRate(rate);
        drink.setUserId("testUser"); // hoặc ID thực tế

        drinkDAO.addDrinkWithImage(this, drink, selectedImageUri,
                unused -> {
                    Toast.makeText(this, "Tạo đồ uống thành công", Toast.LENGTH_SHORT).show();
                    clearForm();
                    loadDrinksFromDatabase();
                },
                e -> Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void clearForm() {
        edtName.setText("");
        edtDescription.setText("");
        edtInstruction.setText("");
        edtCategoryId.setText("");
        edtRate.setText("");
        selectedImageUri = null;
        selectedDrink = null;
        imgPreview.setImageResource(R.drawable.cocktail_logo);
        btnUpdateDrink.setEnabled(false);
        btnDeleteDrink.setEnabled(false);
    }

    private void loadDrinksFromDatabase() {
        drinkDAO.getAllDrinks(new DrinkDAO.DrinkListCallback() {
            @Override
            public void onDrinkListLoaded(List<Drink> drinks) {
                drinkAdapter.setDrinkList(drinks);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(TestDatabaseActivity.this, "Lỗi tải danh sách: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}