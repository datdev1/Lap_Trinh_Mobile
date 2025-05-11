package com.b21dccn216.pocketcocktail.view.Main.fragment.Favorite;

import com.b21dccn216.pocketcocktail.base.BasePresenter;
import com.b21dccn216.pocketcocktail.dao.DrinkDAO;
import com.b21dccn216.pocketcocktail.dao.FavoriteDAO;
import com.b21dccn216.pocketcocktail.dao.IngredientDAO;
import com.b21dccn216.pocketcocktail.dao.UserDAO;
import com.b21dccn216.pocketcocktail.helper.SessionManager;
import com.b21dccn216.pocketcocktail.model.Favorite;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Query;

import java.util.List;

public class FavoritePresenter extends BasePresenter<FavoriteContract.View>
        implements FavoriteContract.Presenter{
    private final FavoriteDAO favoriteDAO;

    private final UserDAO userDAO;
    private final DrinkDAO drinkDAO;

    private final String currentUserId;
    private boolean isFavorite = false;

    public FavoritePresenter() {
        favoriteDAO = new FavoriteDAO();
        userDAO = new UserDAO();
        drinkDAO = new DrinkDAO();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public void onCreate(){
        super.onCreate();
        getAllFavoriteByUserId();
    }
    public void getAllFavoriteByUserId() {
        favoriteDAO.getFavoriteUserId(currentUserId, new FavoriteDAO.FavoriteListCallback() {
            @Override
            public void onFavoriteListLoaded(List<Favorite> favorites) {
                if (view != null) {
                    view.showFavoriteList(favorites);  // Gửi danh sách về View để hiển thị
                }
            }

            @Override
            public void onError(Exception e) {
                if (view != null) {
                    view.showError(e.getMessage());
                }
            }
        });
    }
}
