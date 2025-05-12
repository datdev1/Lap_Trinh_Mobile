package com.b21dccn216.pocketcocktail.test_database.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.dao.DrinkDAO;
import com.b21dccn216.pocketcocktail.dao.UserDAO;
import com.b21dccn216.pocketcocktail.model.Drink;
import com.b21dccn216.pocketcocktail.model.Favorite;
import com.b21dccn216.pocketcocktail.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavoriteAdapter extends ArrayAdapter<Favorite> {
    private Context context;
    private List<Favorite> favorites;
    private DrinkDAO drinkDAO;
    private UserDAO userDAO;
    private Map<String, String> drinkNames;
    private Map<String, String> userNames;

    public FavoriteAdapter(Context context, List<Favorite> favorites) {
        super(context, R.layout.test_database_item_favorite, favorites);
        this.context = context;
        this.favorites = favorites;
        this.drinkDAO = new DrinkDAO();
        this.userDAO = new UserDAO();
        this.drinkNames = new HashMap<>();
        this.userNames = new HashMap<>();
        loadDrinksAndUsers();
    }

    private void loadDrinksAndUsers() {
        // Load drinks
        drinkDAO.getAllDrinks(new DrinkDAO.DrinkListCallback() {
            @Override
            public void onDrinkListLoaded(List<Drink> drinks) {
                for (Drink drink : drinks) {
                    drinkNames.put(drink.getUuid(), drink.getName());
                }
                notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                // Handle error
            }
        });

        // Load users
        userDAO.getAllUsers(new UserDAO.UserListCallback() {
            @Override
            public void onUserListLoaded(List<User> users) {
                for (User user : users) {
                    userNames.put(user.getUuid(), user.getName());
                }
                notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                // Handle error
            }
        });
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.test_database_item_favorite, parent, false);
        }

        Favorite favorite = favorites.get(position);

        TextView tvUserId = convertView.findViewById(R.id.tvUserId);
        TextView tvUserName = convertView.findViewById(R.id.tvUserName);
        TextView tvDrinkId = convertView.findViewById(R.id.tvDrinkId);
        TextView tvDrinkName = convertView.findViewById(R.id.tvDrinkName);

        String userName = userNames.getOrDefault(favorite.getUserId(), "Unknown User");
        String drinkName = drinkNames.getOrDefault(favorite.getDrinkId(), "Unknown Drink");

        tvUserId.setText("User ID: " + favorite.getUserId());
        tvUserName.setText("User Name: " + userName);
        tvDrinkId.setText("Drink ID: " + favorite.getDrinkId());
        tvDrinkName.setText("Drink Name: " + drinkName);

        return convertView;
    }
} 