package com.b21dccn216.pocketcocktail.test_database.fragment;

import android.app.AlertDialog;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.dao.DrinkDAO;
import com.b21dccn216.pocketcocktail.dao.FavoriteDAO;
import com.b21dccn216.pocketcocktail.dao.UserDAO;
import com.b21dccn216.pocketcocktail.model.Drink;
import com.b21dccn216.pocketcocktail.model.Favorite;
import com.b21dccn216.pocketcocktail.model.User;
import com.b21dccn216.pocketcocktail.test_database.adapter.FavoriteAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FavoriteFragment extends BaseModelFragment {
    private EditText etUuid, etCreatedAt, etUpdatedAt;
    private Spinner spinnerUser, spinnerDrink;
    private Button btnSave, btnUpdate, btnDelete;
    private ListView lvFavorites;
    private FavoriteAdapter adapter;
    private List<Favorite> favorites;
    private Favorite selectedFavorite;
    private FavoriteDAO favoriteDAO;
    private DrinkDAO drinkDAO;
    private UserDAO userDAO;
    private SimpleDateFormat dateFormat;
    private List<Drink> drinks;
    private List<User> users;

    @Override
    protected int getLayoutId() {
        return R.layout.test_database_fragment_favorite;
    }

    @Override
    protected void initViews() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        etUuid = rootView.findViewById(R.id.etUuid);
        etCreatedAt = rootView.findViewById(R.id.etCreatedAt);
        etUpdatedAt = rootView.findViewById(R.id.etUpdatedAt);
        spinnerUser = rootView.findViewById(R.id.spinnerUser);
        spinnerDrink = rootView.findViewById(R.id.spinnerDrink);

        btnSave = rootView.findViewById(R.id.btnSave);
        btnUpdate = rootView.findViewById(R.id.btnUpdate);
        btnDelete = rootView.findViewById(R.id.btnDelete);
        lvFavorites = rootView.findViewById(R.id.lvFavorites);

        favorites = new ArrayList<>();
        drinks = new ArrayList<>();
        users = new ArrayList<>();

        adapter = new FavoriteAdapter(getContext(), favorites);
        lvFavorites.setAdapter(adapter);

        favoriteDAO = new FavoriteDAO();
        drinkDAO = new DrinkDAO();
        userDAO = new UserDAO();

        setupSpinners();
        setupListeners();
        loadData();
    }

    private void setupSpinners() {
        // Setup User spinner
        List<String> userItems = new ArrayList<>();
        userItems.add(""); // Add empty option
        ArrayAdapter<String> userAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                userItems
        );
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUser.setAdapter(userAdapter);

        // Setup Drink spinner
        List<String> drinkItems = new ArrayList<>();
        drinkItems.add(""); // Add empty option
        ArrayAdapter<String> drinkAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                drinkItems
        );
        drinkAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDrink.setAdapter(drinkAdapter);

        // Load users and drinks
        loadUsers();
        loadDrinks();
    }

    private void loadUsers() {
        userDAO.getAllUsers(new UserDAO.UserListCallback() {
            @Override
            public void onUserListLoaded(List<User> userList) {
                userList.sort((c1, c2) -> c2.getUpdatedAt().compareTo(c1.getUpdatedAt()));
                users.clear();
                users.addAll(userList);

                List<String> userItems = new ArrayList<>();
                userItems.add(""); // Add empty option
                for (User user : users) {
                    userItems.add(user.getName() + " (" + user.getUuid() + ")");
                }

                ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerUser.getAdapter();
                adapter.clear();
                adapter.addAll(userItems);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                showToast("Error loading users: " + e.getMessage());
            }
        });
    }

    private void loadDrinks() {
        drinkDAO.getAllDrinks(new DrinkDAO.DrinkListCallback() {
            @Override
            public void onDrinkListLoaded(List<Drink> drinkList) {
                drinkList.sort((c1, c2) -> c2.getUpdatedAt().compareTo(c1.getUpdatedAt()));
                drinks.clear();
                drinks.addAll(drinkList);

                List<String> drinkItems = new ArrayList<>();
                drinkItems.add(""); // Add empty option
                for (Drink drink : drinks) {
                    drinkItems.add(drink.getName() + " (" + drink.getUuid() + ")");
                }

                ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerDrink.getAdapter();
                adapter.clear();
                adapter.addAll(drinkItems);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                showToast("Error loading drinks: " + e.getMessage());
            }
        });
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
                favoriteList.sort((c1, c2) -> c2.getUpdatedAt().compareTo(c1.getUpdatedAt()));
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
        etUuid.setText("");
        etCreatedAt.setText("");
        etUpdatedAt.setText("");
        spinnerUser.setSelection(0);
        spinnerDrink.setSelection(0);
        selectedFavorite = null;
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    @Override
    protected void fillInputs(Object item) {
        if (item instanceof Favorite) {
            Favorite favorite = (Favorite) item;
            etUuid.setText(favorite.getUuid());
            etCreatedAt.setText(dateFormat.format(favorite.getCreatedAt()));
            etUpdatedAt.setText(dateFormat.format(favorite.getUpdatedAt()));

            // Set user spinner
            String userId = favorite.getUserId();
            boolean userFound = false;
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getUuid().equals(userId)) {
                    spinnerUser.setSelection(i + 1); // +1 because of empty option
                    userFound = true;
                    break;
                }
            }
            if (!userFound) {
                spinnerUser.setSelection(0);
            }

            // Set drink spinner
            String drinkId = favorite.getDrinkId();
            boolean drinkFound = false;
            for (int i = 0; i < drinks.size(); i++) {
                if (drinks.get(i).getUuid().equals(drinkId)) {
                    spinnerDrink.setSelection(i + 1); // +1 because of empty option
                    drinkFound = true;
                    break;
                }
            }
            if (!drinkFound) {
                spinnerDrink.setSelection(0);
            }
        }
    }

    private void setButtonsEnabled(boolean enabled) {
        btnSave.setEnabled(enabled);
        btnUpdate.setEnabled(enabled && selectedFavorite != null);
        btnDelete.setEnabled(enabled && selectedFavorite != null);
    }

    @Override
    protected void saveItem() {
        int userPosition = spinnerUser.getSelectedItemPosition();
        int drinkPosition = spinnerDrink.getSelectedItemPosition();

        if (userPosition <= 0 || drinkPosition <= 0) {
            showToast("Please select both user and drink");
            return;
        }

        String userId = users.get(userPosition - 1).getUuid();
        String drinkId = drinks.get(drinkPosition - 1).getUuid();

        // Check if favorite already exists
        boolean favoriteExists = false;
        for (Favorite favorite : favorites) {
            if (favorite.getUserId().equals(userId) && favorite.getDrinkId().equals(drinkId)) {
                favoriteExists = true;
                break;
            }
        }

        if (favoriteExists) {
            new AlertDialog.Builder(requireContext())
                .setTitle("Warning")
                .setMessage("This favorite already exists. Are you sure you want to create it?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    performSave(userId, drinkId);
                })
                .setNegativeButton("No", null)
                .show();
        } else {
            performSave(userId, drinkId);
        }
    }

    private void performSave(String userId, String drinkId) {
        setButtonsEnabled(false);
        Favorite favorite = new Favorite(drinkId, userId);
        favorite.generateUUID();

        favoriteDAO.addFavorite(favorite,
                aVoid -> {
                    showToast("Favorite added successfully");
                    clearInputs();
                    loadData();
                    setButtonsEnabled(true);
                },
                e -> {
                    showToast("Error adding favorite: " + e.getMessage());
                    setButtonsEnabled(true);
                });
    }

    @Override
    protected void updateItem() {
        if (selectedFavorite == null) {
            showToast("Please select a favorite first");
            return;
        }

        int userPosition = spinnerUser.getSelectedItemPosition();
        int drinkPosition = spinnerDrink.getSelectedItemPosition();

        if (userPosition <= 0 || drinkPosition <= 0) {
            showToast("Please select both user and drink");
            return;
        }

        setButtonsEnabled(false);
        String userId = users.get(userPosition - 1).getUuid();
        String drinkId = drinks.get(drinkPosition - 1).getUuid();

        selectedFavorite.setUserId(userId);
        selectedFavorite.setDrinkId(drinkId);

        favoriteDAO.updateFavorite(selectedFavorite,
                aVoid -> {
                    showToast("Favorite updated successfully");
                    clearInputs();
                    loadData();
                    setButtonsEnabled(true);
                },
                e -> {
                    showToast("Error updating favorite: " + e.getMessage());
                    setButtonsEnabled(true);
                });
    }

    @Override
    protected void deleteItem() {
        if (selectedFavorite == null) {
            showToast("Please select a favorite first");
            return;
        }

        new AlertDialog.Builder(requireContext())
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete this favorite?")
            .setPositiveButton("Yes", (dialog, which) -> {
                performDelete();
            })
            .setNegativeButton("No", null)
            .show();
    }

    private void performDelete() {
        setButtonsEnabled(false);
        favoriteDAO.deleteFavorite(selectedFavorite.getUuid(),
                aVoid -> {
                    showToast("Favorite deleted successfully");
                    clearInputs();
                    loadData();
                    setButtonsEnabled(true);
                },
                e -> {
                    showToast(e.getMessage());
                    setButtonsEnabled(true);
                });
    }
} 