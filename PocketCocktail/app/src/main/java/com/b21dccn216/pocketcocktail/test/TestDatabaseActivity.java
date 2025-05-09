package com.b21dccn216.pocketcocktail.test;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestDatabaseActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DrinkAdapter drinkAdapter;
    private List<Drink> drinkList = new ArrayList<>();
    private DrinkDAO drinkDAO;
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

        recyclerView = findViewById(R.id.recyclerView);  // Đảm bảo bạn có recyclerView trong layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        drinkAdapter = new DrinkAdapter(drinkList);
        recyclerView.setAdapter(drinkAdapter);

        drinkDAO = new DrinkDAO();
        addSampleDrinks();

        loadDrinks();

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
}