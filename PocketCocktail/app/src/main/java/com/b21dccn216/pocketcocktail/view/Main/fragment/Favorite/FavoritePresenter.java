package com.b21dccn216.pocketcocktail.view.Main.fragment.Favorite;

import android.util.Log;

import androidx.fragment.app.Fragment;

import com.b21dccn216.pocketcocktail.base.BasePresenter;
import com.b21dccn216.pocketcocktail.dao.DrinkDAO;
import com.b21dccn216.pocketcocktail.dao.FavoriteDAO;
import com.b21dccn216.pocketcocktail.dao.UserDAO;
import com.b21dccn216.pocketcocktail.helper.DialogHelper;
import com.b21dccn216.pocketcocktail.helper.HelperDialog;
import com.b21dccn216.pocketcocktail.helper.SessionManager;
import com.b21dccn216.pocketcocktail.model.Drink;
import com.b21dccn216.pocketcocktail.model.Favorite;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class FavoritePresenter extends BasePresenter<FavoriteContract.View>
        implements FavoriteContract.Presenter{
    private final FavoriteDAO favoriteDAO;

    private final UserDAO userDAO;
    private final DrinkDAO drinkDAO;

    private String currentUserId;
    private boolean isFavorite = false;

    public FavoritePresenter() {
        favoriteDAO = new FavoriteDAO();
        userDAO = new UserDAO();
        drinkDAO = new DrinkDAO();
    }

    @Override
    public void onCreate(){
        super.onCreate();
        currentUserId = SessionManager.getInstance().getUser().getUuid();
        getAllFavoriteByUserId();
        getAllDrinksCreatedByCurrentUser();
    }

    public void getAllFavoriteByUserId() {
        Log.d("favourite", "currentUserId: " + currentUserId);
        favoriteDAO.getFavoriteUserId(currentUserId, new FavoriteDAO.FavoriteListCallback() {
            @Override
            public void onFavoriteListLoaded(List<Favorite> favorites) {
                // TODO
                Log.d("favourite", "Size: " + favorites.size());

                if (favorites.isEmpty() && view != null) {
                    view.showFavoriteDrinkList(new ArrayList<>()); // Truyền list rỗng để xử lý hiển thị thông báo
                    return;
                }

                List<Drink> drinks = new ArrayList<>();
                for(Favorite favorite : favorites){
                    drinkDAO.getDrink(favorite.getDrinkId(),
                            new DrinkDAO.DrinkCallback() {
                                @Override
                                public void onDrinkLoaded(Drink drink) {
                                    drinks.add(drink);
                                    drinks.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
                                    if(view == null) return;
                                    view.showFavoriteDrinkList(drinks);

                                }

                                @Override
                                public void onError(Exception e) {
                                    Log.d("favourite", "onError: " + e.getMessage());
                                }
                            });
                }
            }

            @Override
            public void onError(Exception e) {
                DialogHelper.showAlertDialog(((Fragment) view).requireActivity(),
                        "Error", e.getMessage(),
                        HelperDialog.DialogType.ERROR);
            }
        });
    }

    public void getAllDrinksCreatedByCurrentUser() {
        Log.d("drink", "currentUserId: " + currentUserId);
        drinkDAO.getDrinksByUserId(currentUserId, new DrinkDAO.DrinkListCallback() {
            @Override
            public void onDrinkListLoaded(List<Drink> drinks) {
                Log.d("drink", "Drinks created by user: " + drinks.size());
                if(view == null) return;
                if (drinks.isEmpty()) {
                    view.showFavoriteDrinkList(new ArrayList<>()); // Truyền list rỗng để xử lý hiển thị thông báo
                    return;
                }
                view.showFavoriteDrinkCreateByUserId(drinks); // Hiển thị danh sách nếu có
            }

            @Override
            public void onError(Exception e) {
                DialogHelper.showAlertDialog(((Fragment) view).requireActivity(),
                        "Error", e.getMessage(),
                        HelperDialog.DialogType.ERROR);
            }
        });
    }

}
