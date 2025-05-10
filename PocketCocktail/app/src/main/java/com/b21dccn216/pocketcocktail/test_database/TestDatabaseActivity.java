package com.b21dccn216.pocketcocktail.test_database;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.test_database.fragment.CategoryFragment;
import com.b21dccn216.pocketcocktail.test_database.fragment.DrinkFragment;
import com.b21dccn216.pocketcocktail.test_database.fragment.IngredientFragment;
import com.b21dccn216.pocketcocktail.test_database.fragment.RecipeFragment;
import com.b21dccn216.pocketcocktail.test_database.fragment.ReviewFragment;
import com.b21dccn216.pocketcocktail.test_database.fragment.FavoriteFragment;
import com.b21dccn216.pocketcocktail.test_database.fragment.UserFragment;
import com.b21dccn216.pocketcocktail.utils.DataImporter;

public class TestDatabaseActivity extends AppCompatActivity {
    private Spinner spinnerModelSelect;
    private String[] modelTypes = {"Category", "Drink", "Ingredient", "Recipe", "Review", "Favorite", "User"};
    private Button btnImport;

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

        // Initialize import button
        btnImport = findViewById(R.id.btnImport);
        btnImport.setOnClickListener(v -> showImportConfirmationDialog());

        // Initialize spinner
        spinnerModelSelect = findViewById(R.id.spinnerModelSelect);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, modelTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerModelSelect.setAdapter(adapter);

        // Set spinner listener
        spinnerModelSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switchFragment(modelTypes[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing when nothing is selected
            }
        });

        // Set default fragment
        switchFragment(modelTypes[0]);
    }

    private void showImportConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Import Data")
                .setMessage("Are you sure you want to import data from JSON file? This will add new categories and drinks to the database.")
                .setPositiveButton("Import", (dialog, which) -> {
                    performImport();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performImport() {
        DataImporter importer = new DataImporter(this);
        importer.importData("old_database_drink_from_runtime.json");
        Toast.makeText(this, "Import started. Check logcat for progress.", Toast.LENGTH_LONG).show();
    }

    private void switchFragment(String modelType) {
        Fragment fragment = null;
        switch (modelType) {
            case "Category":
                fragment = new CategoryFragment();
                break;
            case "Drink":
                fragment = new DrinkFragment();
                break;
            case "Ingredient":
                fragment = new IngredientFragment();
                break;
            case "Recipe":
                fragment = new RecipeFragment();
                break;
            case "Review":
                fragment = new ReviewFragment();
                break;
            case "Favorite":
                fragment = new FavoriteFragment();
                break;
            case "User":
                fragment = new UserFragment();
                break;
        }
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }
}