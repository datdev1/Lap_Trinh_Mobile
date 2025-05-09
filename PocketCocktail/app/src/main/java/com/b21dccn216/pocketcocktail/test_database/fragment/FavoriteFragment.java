package com.b21dccn216.pocketcocktail.test_database.fragment;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.dao.FavoriteDAO;
import com.b21dccn216.pocketcocktail.model.Favorite;
import com.b21dccn216.pocketcocktail.test_database.adapter.FavoriteAdapter;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends BaseModelFragment {
    private EditText etUserId, etDrinkId;
    private Button btnSave, btnUpdate, btnDelete;
    private ListView lvFavorites;
    private FavoriteAdapter adapter;
    private List<Favorite> favorites;
    private Favorite selectedFavorite;
    private FavoriteDAO favoriteDAO;

    @Override
    protected int getLayoutId() {
        return R.layout.test_database_fragment_favorite;
    }

    @Override
    protected void initViews() {
        etUserId = rootView.findViewById(R.id.etUserId);
        etDrinkId = rootView.findViewById(R.id.etDrinkId);
        btnSave = rootView.findViewById(R.id.btnSave);
        btnUpdate = rootView.findViewById(R.id.btnUpdate);
        btnDelete = rootView.findViewById(R.id.btnDelete);
        lvFavorites = rootView.findViewById(R.id.lvFavorites);

        favorites = new ArrayList<>();
        adapter = new FavoriteAdapter(getContext(), favorites);
        lvFavorites.setAdapter(adapter);
        favoriteDAO = new FavoriteDAO();

        setupListeners();
        loadData();
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveItem());
        btnUpdate.setOnClickListener(v -> updateItem());
        btnDelete.setOnClickListener(v -> deleteItem());

        lvFavorites.setOnItemClickListener((parent, view, position, id) -> {
            selectedFavorite = favorites.get(position);
            fillInputs(selectedFavorite);
            btnUpdate.setEnabled(true);
            btnDelete.setEnabled(true);
        });
    }

    @Override
    protected void loadData() {
        favoriteDAO.getAllFavorites(new FavoriteDAO.FavoriteListCallback() {
            @Override
            public void onFavoriteListLoaded(List<Favorite> favoriteList) {
                favorites.clear();
                favorites.addAll(favoriteList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                showToast("Error loading favorites: " + e.getMessage());
            }
        });
    }

    @Override
    protected void clearInputs() {
        etUserId.setText("");
        etDrinkId.setText("");
        selectedFavorite = null;
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    @Override
    protected void fillInputs(Object item) {
        if (item instanceof Favorite) {
            Favorite favorite = (Favorite) item;
            etUserId.setText(favorite.getUserId());
            etDrinkId.setText(favorite.getDrinkId());
        }
    }

    @Override
    protected void saveItem() {
        String userId = etUserId.getText().toString();
        String drinkId = etDrinkId.getText().toString();

        if (userId.isEmpty() || drinkId.isEmpty()) {
            showToast("Please fill all required fields");
            return;
        }

        Favorite favorite = new Favorite(userId, drinkId);
        favorite.generateUUID();

        favoriteDAO.addFavorite(favorite,
                aVoid -> {
                    showToast("Favorite added successfully");
                    clearInputs();
                    loadData();
                },
                e -> showToast("Error adding favorite: " + e.getMessage()));
    }

    @Override
    protected void updateItem() {
        if (selectedFavorite == null) {
            showToast("Please select a favorite first");
            return;
        }

        String userId = etUserId.getText().toString();
        String drinkId = etDrinkId.getText().toString();

        if (userId.isEmpty() || drinkId.isEmpty()) {
            showToast("Please fill all required fields");
            return;
        }

        selectedFavorite.setUserId(userId);
        selectedFavorite.setDrinkId(drinkId);

        favoriteDAO.updateFavorite(selectedFavorite,
                aVoid -> {
                    showToast("Favorite updated successfully");
                    clearInputs();
                    loadData();
                },
                e -> showToast("Error updating favorite: " + e.getMessage()));
    }

    @Override
    protected void deleteItem() {
        if (selectedFavorite == null) {
            showToast("Please select a favorite first");
            return;
        }

        favoriteDAO.deleteFavorite(selectedFavorite.getUuid(),
                aVoid -> {
                    showToast("Favorite deleted successfully");
                    clearInputs();
                    loadData();
                },
                e -> showToast("Error deleting favorite: " + e.getMessage()));
    }
} 